import dotenv from "dotenv";
import express from "express";
import cors from "cors";
import pool from "./db.js";

dotenv.config();

const app = express();
const port = process.env.PORT || 3001;
const host = process.env.SERVER_HOST || "0.0.0.0";

app.use(cors());
app.use(express.json());

// --- ROTAS DE AUTENTICAÇÃO ---

// Cadastro
app.post("/cadastrar", async (req, res) => {
  const { nome, email, senha } = req.body;
  try {
    const result = await pool.query(
      "INSERT INTO usuarios (nome, email, senha) VALUES ($1, $2, $3) RETURNING *",
      [nome, email, senha]
    );
    res.status(201).json({ message: "Usuário cadastrado!", user: result.rows[0] });
  } catch (err) {
    console.error(err);
    res.status(500).send("Erro ao cadastrar usuário.");
  }
});

// Login
app.post("/login", async (req, res) => {
  const { email, senha } = req.body;
  try {
    const result = await pool.query(
      "SELECT * FROM usuarios WHERE email = $1 AND senha = $2",
      [email, senha]
    );

    if (result.rows.length > 0) {
      res.status(200).json({ message: "Login bem-sucedido!", user: result.rows[0] });
    } else {
      res.status(401).json({ message: "Email ou senha incorretos." });
    }
  } catch (err) {
    console.error(err);
    res.status(500).send("Erro no servidor.");
  }
});

// Atualizar Perfil
app.put("/usuarios/:id", async (req, res) => {
    const { id } = req.params;
    const { nome, email } = req.body;
    try {
        await pool.query("UPDATE usuarios SET nome = $1, email = $2 WHERE id = $3", [nome, email, id]);
        res.sendStatus(200);
    } catch (err) {
        console.error(err);
        res.status(500).send("Erro ao atualizar perfil");
    }
});

// --- ROTAS DE METAS ---

app.get("/metas/:usuarioId", async (req, res) => {
  try {
    const { usuarioId } = req.params;
    const result = await pool.query("SELECT * FROM metas WHERE usuario_id = $1", [usuarioId]);
    res.json(result.rows);
  } catch (err) {
    res.status(500).send("Erro ao buscar metas");
  }
});

app.post("/metas", async (req, res) => {
  const { usuario_id, titulo, descricao, progresso, vencimento } = req.body;
  try {
    const result = await pool.query(
      "INSERT INTO metas (usuario_id, titulo, descricao, progresso, vencimento) VALUES ($1, $2, $3, $4, $5) RETURNING *",
      [usuario_id, titulo, descricao, progresso, vencimento]
    );
    res.status(201).json(result.rows[0]);
  } catch (err) {
    res.status(500).send("Erro ao criar meta");
  }
});

// --- ROTAS DE TAREFAS ---

app.get("/tarefas/:usuarioId", async (req, res) => {
  try {
    const { usuarioId } = req.params;
    const result = await pool.query("SELECT * FROM tarefas WHERE usuario_id = $1", [usuarioId]);
    // Formata a data para YYYY-MM-DD para o frontend
    const tarefasFormatadas = result.rows.map(t => ({
        ...t,
        data: new Date(t.data).toISOString().split('T')[0] 
    }));
    res.json(tarefasFormatadas);
  } catch (err) {
    res.status(500).send("Erro ao buscar tarefas");
  }
});

app.post("/tarefas", async (req, res) => {
  const { usuario_id, titulo, horario, data, meta_id, cor } = req.body;
  try {
    const result = await pool.query(
      "INSERT INTO tarefas (usuario_id, titulo, horario, data, meta_id, cor) VALUES ($1, $2, $3, $4, $5, $6) RETURNING *",
      [usuario_id, titulo, horario, data, meta_id || null, cor]
    );
    // Formata a resposta
    const novaTarefa = { ...result.rows[0], data: new Date(result.rows[0].data).toISOString().split('T')[0] };
    res.status(201).json(novaTarefa);
  } catch (err) {
    console.error(err);
    res.status(500).send("Erro ao criar tarefa");
  }
});

app.put("/tarefas/:id", async (req, res) => {
    const { id } = req.params;
    const { completa } = req.body;
    try {
        await pool.query("UPDATE tarefas SET completa = $1 WHERE id = $2", [completa, id]);
        res.sendStatus(200);
    } catch (err) {
        res.status(500).send("Erro ao atualizar tarefa");
    }
});

// --- DASHBOARD (GRÁFICOS) ---

app.get("/dashboard/:usuarioId", async (req, res) => {
    const { usuarioId } = req.params;
    try {
        const totalResult = await pool.query("SELECT COUNT(*) FROM tarefas WHERE usuario_id = $1", [usuarioId]);
        const completasResult = await pool.query("SELECT COUNT(*) FROM tarefas WHERE usuario_id = $1 AND completa = true", [usuarioId]);
        
        // Pega tarefas completas dos últimos 7 dias agrupadas por dia da semana
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
        console.error(err);
        res.status(500).send("Erro ao carregar dashboard");
    }
});

app.listen(port, host, () => {
  console.log(`🚀 Servidor rodando em http://${host}:${port}`);
});
