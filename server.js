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

// --- METAS ---
app.get("/metas/:usuarioId", async (req, res) => {
  try {
    const { usuarioId } = req.params;
    const result = await pool.query("SELECT * FROM metas WHERE usuario_id = $1 ORDER BY id ASC", [usuarioId]);
    res.json(result.rows);
  } catch (err) { res.status(500).send("Erro metas"); }
});

app.post("/metas", async (req, res) => {
  const { usuario_id, titulo, descricao, progresso, vencimento } = req.body;
  try {
    const result = await pool.query(
      "INSERT INTO metas (usuario_id, titulo, descricao, progresso, vencimento) VALUES ($1, $2, $3, $4, $5) RETURNING *",
      [usuario_id, titulo, descricao, progresso, vencimento]
    );
    res.status(201).json(result.rows[0]);
  } catch (err) { res.status(500).send("Erro criar meta"); }
});

app.put("/metas/:id", async (req, res) => {
  const { id } = req.params;
  const { titulo, descricao, progresso, vencimento } = req.body;
  try {
    await pool.query(
      "UPDATE metas SET titulo = $1, descricao = $2, progresso = $3, vencimento = $4 WHERE id = $5",
      [titulo, descricao, progresso, vencimento, id]
    );
    res.sendStatus(200);
  } catch (err) { res.status(500).send("Erro editar meta"); }
});

app.delete("/metas/:id", async (req, res) => {
  const { id } = req.params;
  try {
    await pool.query("DELETE FROM metas WHERE id = $1", [id]);
    res.sendStatus(200);
  } catch (err) { res.status(500).send("Erro excluir meta"); }
});

// --- TAREFAS ---
app.get("/tarefas/:usuarioId", async (req, res) => {
  try {
    const { usuarioId } = req.params;
    // Trazemos todas e ordenamos no front, ou poderíamos ordenar aqui.
    // Manter ID ASC como padrão de inserção, a lógica de negócio fica no front ou query complexa.
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

// --- ATUALIZAÇÃO IMPORTANTE AQUI ---
// Adicionado o campo 'cor' no UPDATE para salvar a mudança de prioridade
app.put("/tarefas/editar/:id", async (req, res) => {
  const { id } = req.params;
  const { titulo, horario, meta_id, cor } = req.body; // Adicionado 'cor'
  try {
    await pool.query(
      "UPDATE tarefas SET titulo = $1, horario = $2, meta_id = $3, cor = $4 WHERE id = $5",
      [titulo, horario, meta_id || null, cor, id]
    );
    res.sendStatus(200);
  } catch (err) { res.status(500).send("Erro editar tarefa"); }
});
// -----------------------------------

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
