import dotenv from "dotenv";
import express from "express";
import cors from "cors";
import pool from "./db.js";

dotenv.config();

const app = express();
const port = process.env.PORT || 3001;
const host = process.env.SERVER_HOST || "0.0.0.0";

// Aumenta o limite do body para aceitar fotos em Base64
app.use(express.json({ limit: '50mb' }));
app.use(express.urlencoded({ limit: '50mb', extended: true }));
app.use(cors());

// --- AUTENTICAÇÃO ---
app.post("/cadastrar", async (req, res) => {
  const { nome, email, senha } = req.body;
  try {
    const result = await pool.query(
      "INSERT INTO usuarios (nome, email, senha) VALUES ($1, $2, $3) RETURNING *",
      [nome, email, senha]
    );
    res.status(201).json({ message: "Cadastrado!", user: result.rows[0] });
  } catch (err) {
    res.status(500).send("Erro ao cadastrar.");
  }
});

app.post("/login", async (req, res) => {
  const { email, senha } = req.body;
  try {
    const result = await pool.query(
      "SELECT * FROM usuarios WHERE email = $1 AND senha = $2",
      [email, senha]
    );
    if (result.rows.length > 0) {
      res.status(200).json({ message: "Login OK", user: result.rows[0] });
    } else {
      res.status(401).json({ message: "Login falhou." });
    }
  } catch (err) {
    res.status(500).send("Erro servidor.");
  }
});

// --- PERFIL DO USUÁRIO (NOVOS ENDPOINTS) ---

// GET: Busca dados do usuário
app.get("/usuario/:id", async (req, res) => {
  const { id } = req.params;
  try {
    const result = await pool.query(
      "SELECT id, nome, email, foto, notificacoes FROM usuarios WHERE id = $1",
      [id]
    );
    if (result.rows.length > 0) {
      res.json(result.rows[0]);
    } else {
      res.status(404).send("Usuário não encontrado");
    }
  } catch (err) {
    console.error(err);
    res.status(500).send("Erro ao buscar perfil");
  }
});

// PUT: Atualiza dados do usuário
app.put("/usuario/:id", async (req, res) => {
  const { id } = req.params;
  const { nome, email, foto, notificacoes } = req.body;
  try {
    await pool.query(
      "UPDATE usuarios SET nome = $1, email = $2, foto = $3, notificacoes = $4 WHERE id = $5",
      [nome, email, foto, notificacoes, id]
    );
    res.sendStatus(200);
  } catch (err) {
    console.error(err);
    res.status(500).send("Erro ao atualizar perfil");
  }
});

// --- METAS ---
app.get("/metas/:usuarioId", async (req, res) => {
  try {
    const { usuarioId } = req.params;
    const resultMetas = await pool.query("SELECT * FROM metas WHERE usuario_id = $1 ORDER BY id ASC", [usuarioId]);
    const metas = resultMetas.rows;

    for (let meta of metas) {
      const resultMarcos = await pool.query("SELECT * FROM marcos WHERE meta_id = $1 ORDER BY id ASC", [meta.id]);
      meta.marcos = resultMarcos.rows.map(m => ({
        id: m.id,
        descricao: m.descricao,
        vencimento: m.vencimento ? new Date(m.vencimento).toISOString().split('T')[0] : '',
        progresso: m.concluido ? 100 : 0
      }));
    }

    res.json(metas);
  } catch (err) { 
    console.error(err);
    res.status(500).send("Erro metas"); 
  }
});

app.post("/metas", async (req, res) => {
  const { usuario_id, titulo, descricao, progresso, vencimento, marcos, cor } = req.body;
  
  const client = await pool.connect();
  try {
    await client.query('BEGIN');

    const metaResult = await client.query(
      "INSERT INTO metas (usuario_id, titulo, descricao, progresso, vencimento, cor) VALUES ($1, $2, $3, $4, $5, $6) RETURNING *",
      [usuario_id, titulo, descricao, progresso, vencimento, cor || '#007BFF']
    );
    const novaMeta = metaResult.rows[0];

    if (marcos && marcos.length > 0) {
      for (const m of marcos) {
        const dataVencimento = m.vencimento && m.vencimento.length === 10 
          ? m.vencimento.split('/').reverse().join('-')
          : null;
        
        await client.query(
          "INSERT INTO marcos (meta_id, descricao, vencimento, concluido) VALUES ($1, $2, $3, $4)",
          [novaMeta.id, m.descricao, dataVencimento, m.progresso === 100]
        );
      }
    }

    await client.query('COMMIT');
    res.status(201).json(novaMeta);
  } catch (err) { 
    await client.query('ROLLBACK');
    console.error(err);
    res.status(500).send("Erro criar meta e marcos"); 
  } finally {
    client.release();
  }
});

app.put("/metas/:id", async (req, res) => {
  const { id } = req.params;
  const { titulo, descricao, progresso, vencimento, marcos, cor } = req.body;

  const client = await pool.connect();
  try {
    await client.query('BEGIN');

    await client.query(
      "UPDATE metas SET titulo = $1, descricao = $2, progresso = $3, vencimento = $4, cor = $5 WHERE id = $6",
      [titulo, descricao, progresso, vencimento, cor, id]
    );

    await client.query("DELETE FROM marcos WHERE meta_id = $1", [id]);

    if (marcos && marcos.length > 0) {
      for (const m of marcos) {
        let dataVencimento = null;
        if (m.vencimento) {
            if (m.vencimento.includes('/')) {
                dataVencimento = m.vencimento.split('/').reverse().join('-');
            } else {
                dataVencimento = m.vencimento;
            }
        }

        await client.query(
          "INSERT INTO marcos (meta_id, descricao, vencimento, concluido) VALUES ($1, $2, $3, $4)",
          [id, m.descricao, dataVencimento, m.progresso === 100]
        );
      }
    }

    await client.query('COMMIT');
    res.sendStatus(200);
  } catch (err) { 
    await client.query('ROLLBACK');
    console.error(err);
    res.status(500).send("Erro editar meta"); 
  } finally {
    client.release();
  }
});

app.delete("/metas/:id", async (req, res) => {
  const { id } = req.params;
  try {
    await pool.query("DELETE FROM metas WHERE id = $1", [id]);
    res.sendStatus(200);
  } catch (err) { res.status(500).send("Erro excluir meta"); }
});

app.get("/calendario-marcos/:usuarioId", async (req, res) => {
    try {
        const { usuarioId } = req.params;
        const query = `
            SELECT m.id, m.descricao, m.vencimento, m.concluido, m.meta_id, mt.cor, mt.titulo as meta_titulo
            FROM marcos m
            JOIN metas mt ON m.meta_id = mt.id
            WHERE mt.usuario_id = $1
        `;
        const result = await pool.query(query, [usuarioId]);
        
        const marcosFormatados = result.rows.map(m => ({
            ...m,
            vencimento: m.vencimento ? new Date(m.vencimento).toISOString().split('T')[0] : null
        }));

        res.json(marcosFormatados);
    } catch (err) {
        console.error(err);
        res.status(500).send("Erro ao buscar marcos para calendário");
    }
});

app.patch("/marcos/check/:id", async (req, res) => {
    const { id } = req.params;
    const client = await pool.connect();
    
    try {
        await client.query('BEGIN');

        const marcoRes = await client.query(
            "UPDATE marcos SET concluido = true WHERE id = $1 RETURNING meta_id, descricao",
            [id]
        );
        
        if (marcoRes.rows.length === 0) throw new Error("Marco não encontrado");
        
        const metaId = marcoRes.rows[0].meta_id;
        const descricao = marcoRes.rows[0].descricao;

        const contagemRes = await client.query(
            "SELECT count(*) as total, count(*) filter (where concluido = true) as concluidos FROM marcos WHERE meta_id = $1",
            [metaId]
        );
        
        const total = parseInt(contagemRes.rows[0].total);
        const concluidos = parseInt(contagemRes.rows[0].concluidos);
        const novoProgresso = total > 0 ? Math.round((concluidos / total) * 100) : 0;

        await client.query(
            "UPDATE metas SET progresso = $1 WHERE id = $2",
            [novoProgresso, metaId]
        );

        await client.query('COMMIT');
        
        res.json({ 
            success: true, 
            novoProgresso, 
            metaId,
            marcoTitulo: descricao
        });

    } catch (err) {
        await client.query('ROLLBACK');
        console.error(err);
        res.status(500).send("Erro ao concluir marco");
    } finally {
        client.release();
    }
});

// --- TAREFAS ---
app.get("/tarefas/:usuarioId", async (req, res) => {
  try {
    const { usuarioId } = req.params;
    const result = await pool.query("SELECT * FROM tarefas WHERE usuario_id = $1 ORDER BY id ASC", [usuarioId]);
    const tarefas = result.rows.map(t => ({
      ...t,
      data: new Date(t.data).toISOString().split('T')[0]
    }));
    res.json(tarefas);
  } catch (err) { res.status(500).send("Erro tarefas"); }
});

app.post("/tarefas", async (req, res) => {
  const { usuario_id, titulo, horario, data, meta_id, cor } = req.body;
  try {
    const result = await pool.query(
      "INSERT INTO tarefas (usuario_id, titulo, horario, data, meta_id, cor) VALUES ($1, $2, $3, $4, $5, $6) RETURNING *",
      [usuario_id, titulo, horario, data, meta_id || null, cor]
    );
    const t = { ...result.rows[0], data: new Date(result.rows[0].data).toISOString().split('T')[0] };
    res.status(201).json(t);
  } catch (err) { res.status(500).send("Erro criar tarefa"); }
});

app.put("/tarefas/editar/:id", async (req, res) => {
  const { id } = req.params;
  const { titulo, horario, meta_id, cor } = req.body;
  try {
    await pool.query(
      "UPDATE tarefas SET titulo = $1, horario = $2, meta_id = $3, cor = $4 WHERE id = $5",
      [titulo, horario, meta_id || null, cor, id]
    );
    res.sendStatus(200);
  } catch (err) { res.status(500).send("Erro editar tarefa"); }
});

app.put("/tarefas/:id", async (req, res) => {
  const { id } = req.params;
  const { completa } = req.body;
  try {
    await pool.query("UPDATE tarefas SET completa = $1 WHERE id = $2", [completa, id]);
    res.sendStatus(200);
  } catch (err) { res.status(500).send("Erro status"); }
});

app.delete("/tarefas/:id", async (req, res) => {
  const { id } = req.params;
  try {
    await pool.query("DELETE FROM tarefas WHERE id = $1", [id]);
    res.sendStatus(200);
  } catch (err) { res.status(500).send("Erro excluir tarefa"); }
});

// --- DASHBOARD ---
app.get("/dashboard/:usuarioId", async (req, res) => {
    const { usuarioId } = req.params;
    try {
        const totalResult = await pool.query("SELECT COUNT(*) FROM tarefas WHERE usuario_id = $1", [usuarioId]);
        const completasResult = await pool.query("SELECT COUNT(*) FROM tarefas WHERE usuario_id = $1 AND completa = true", [usuarioId]);
        const graficoQuery = `
            SELECT TO_CHAR(data, 'Dy') as dia, COUNT(*) as total 
            FROM tarefas 
            WHERE usuario_id = $1 AND completa = true AND data >= NOW() - INTERVAL '7 days'
            GROUP BY dia, data
            ORDER BY data ASC;
        `;
        const graficoResult = await pool.query(graficoQuery, [usuarioId]);
        res.json({
            total: parseInt(totalResult.rows[0].count),
            completas: parseInt(completasResult.rows[0].count),
            dadosGrafico: graficoResult.rows
        });
    } catch (err) {
        res.status(500).send("Erro ao carregar dashboard");
    }
});

app.listen(port, host, () => {
  console.log(`Rodando na porta ${port}`);
});
