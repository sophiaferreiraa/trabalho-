<?php
require_once __DIR__ . '/../models/User.php';

class AuthController {

    public function login() {
        session_start();
        $feedback = "";
        $tipo = "";
        $email = "";
        $success = false;

        if ($_SERVER["REQUEST_METHOD"] == "POST") {

            $email = filter_input(INPUT_POST, 'email', FILTER_SANITIZE_EMAIL);
            $senha = $_POST["password"] ?? "";

            $userModel = new User();
            $user = $userModel->findByEmail($email);

            if ($user && password_verify($senha, $user["senha"])) {
                $_SESSION["user_id"] = $user["user_id"];
                $_SESSION["nome"] = $user["nome_completo"];
                $_SESSION["tipo_usuario"] = $user["tipo_usuario"];
                $success = true;
                $tipo = "success";
                $feedback = "Login realizado com sucesso!";
            } else {
                $tipo = "error";
                $feedback = "E-mail ou senha incorretos.";
            }
        }

        require __DIR__ . '/../views/auth/login.php';
    }

    public function cadastro() {
        session_start();
        $erro = "";
        $sucesso = "";
        $nome = "";
        $email = "";
        $nascimento = "";

        if ($_SERVER["REQUEST_METHOD"] == "POST") {

            $nome = trim($_POST["nome_completo"]);
            $email = trim($_POST["email"]);
            $senha = $_POST["senha"];
            $nascimento = $_POST["data_nascimento"];

            if (empty($nome) || empty($email) || empty($senha) || empty($nascimento)) {
                $erro = "Todos os campos são obrigatórios.";
            } elseif (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
                $erro = "E-mail inválido.";
            } else {

                $userModel = new User();

                $result = $userModel->create(
                    $nome,
                    $email,
                    password_hash($senha, PASSWORD_DEFAULT),
                    $nascimento
                );

                if ($result === "duplicate") {
                    $erro = "Este e-mail já está cadastrado.";
                } elseif ($result) {
                    $sucesso = "Cadastro realizado com sucesso!";
                } else {
                    $erro = "Erro ao cadastrar.";
                }
               
            }
        }

        require __DIR__ . '/../views/auth/cadastro.php';
    }
}
