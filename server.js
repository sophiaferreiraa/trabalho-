// server.js
import dotenv from "dotenv";
import express from "express";
import cors from "cors";
import pool from "./db.js"; // importa a conexão do db.js

dotenv.config(); // Carrega as variáveis do .env

const app = express();
const port = process.env.PORT || 3001; // Porta do servidor
const host = process.env.SERVER_HOST || "0.0.0.0"; // Host do servidor (aceita conexões externas)

app.use(cors());
app.use(express.json()); // Permite que o servidor entenda JSON

// Rota de teste para verificar a conexão com o banco de dados
app.get("/", async (req, res) => {
  try {
    await pool.query("SELECT NOW()"); // Testa a conexão
    res.status(200).send("Conexão com o banco de dados bem-sucedida!");
  } catch (err) {
    console.error("Erro na conexão com o banco de dados", err);
    res.status(500).send("Erro na conexão com o banco de dados.");
  }
});

// Rota para criar a tabela de usuários
app.get("/criar-tabela", async (req, res) => {
  try {
    const queryText = `
      CREATE TABLE IF NOT EXISTS usuarios (
        id SERIAL PRIMARY KEY,
        nome VARCHAR(100) NOT NULL,
        email VARCHAR(100) UNIQUE NOT NULL,
        senha VARCHAR(100) NOT NULL
      );
    `;
    await pool.query(queryText);
    res.status(200).send("Tabela de usuários criada com sucesso!");
  } catch (err) {
    console.error("Erro ao criar a tabela", err);
    res.status(500).send("Erro ao criar a tabela.");
  }
});

// >>>>> ROTA PARA CADASTRO (INSERE UM NOVO USUÁRIO) <<<<<
app.post("/cadastrar", async (req, res) => {
  const { nome, email, senha } = req.body;
  try {
    const result = await pool.query(
      "INSERT INTO usuarios (nome, email, senha) VALUES ($1, $2, $3) RETURNING *",
      [nome, email, senha]
    );
    res.status(201).json({ message: "Usuário cadastrado com sucesso!", user: result.rows[0] });
  } catch (err) {
    console.error("Erro ao cadastrar usuário", err);
    res.status(500).send("Erro ao cadastrar usuário.");
  }
});

// >>>>> ROTA PARA LOGIN (VERIFICA SE O USUÁRIO EXISTE) <<<<<
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
    console.error("Erro no login", err);
    res.status(500).send("Erro no servidor.");
  }
});

// Rota para buscar todos os usuários
app.get("/usuarios", async (req, res) => {
  try {
    const result = await pool.query("SELECT * FROM usuarios");
    res.status(200).json(result.rows);
  } catch (err) {
    console.error("Erro ao buscar usuários", err);
    res.status(500).send("Erro no servidor");
  }
});

// Faz o servidor escutar em todas as interfaces de rede
app.listen(port, host, () => {
  console.log(` Servidor rodando em http://${host}:${port}`);
});
