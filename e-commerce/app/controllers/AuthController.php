<?php
require_once __DIR__ . '/../models/User.php';

class AuthController {

    public function login() {
        if (session_status() === PHP_SESSION_NONE) {
            session_start();
        }
        
        $feedback = "";
        $tipo = "";
        $email = "";
        
        // Se já estiver logado, manda pra home direto
        if (isset($_SESSION['user_id'])) {
            $this->redirecionarParaHome();
            return;
        }

        if ($_SERVER["REQUEST_METHOD"] == "POST") {

            $email = filter_input(INPUT_POST, 'email', FILTER_SANITIZE_EMAIL);
            $senha = $_POST["password"] ?? "";

            $userModel = new User();
            $user = $userModel->findByEmail($email);

            // Verifica se achou o usuário e se a senha bate
            if ($user && password_verify($senha, $user["senha"])) {
                
                // CORREÇÃO CRÍTICA: Usa 'id' (do banco) e não 'user_id'
                $_SESSION["user_id"] = $user["id"]; 
                $_SESSION["nome"] = $user["nome_completo"];
                $_SESSION["tipo_usuario"] = $user["tipo_usuario"];
                
                $tipo = "success";
                $feedback = "Login realizado! Redirecionando...";
                
                // CHAMA O REDIRECIONAMENTO SEGURO
                $this->redirecionarParaHome();
                exit;

            } else {
                $tipo = "error";
                $feedback = "E-mail ou senha incorretos.";
            }
        }

        require __DIR__ . '/../views/auth/login.php';
    }

    public function cadastro() {
        if (session_status() === PHP_SESSION_NONE) {
            session_start();
        }
        
        $erro = "";
        $sucesso = "";
        $nome = "";
        $email = "";
        $nascimento = "";

        if ($_SERVER["REQUEST_METHOD"] == "POST") {
            $nome = trim($_POST["nome_completo"] ?? '');
            $email = trim($_POST["email"] ?? '');
            $senha = $_POST["senha"] ?? '';
            $nascimento = $_POST["data_nascimento"] ?? '';

            if (empty($nome) || empty($email) || empty($senha) || empty($nascimento)) {
                $erro = "Todos os campos são obrigatórios.";
            } elseif (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
                $erro = "E-mail inválido.";
            } else {
                $userModel = new User();
                $result = $userModel->create($nome, $email, password_hash($senha, PASSWORD_DEFAULT), $nascimento);

                if ($result === "duplicate") {
                    $erro = "Este e-mail já está cadastrado.";
                } elseif ($result) {
                    $sucesso = "Cadastro realizado! Faça login.";
                } else {
                    $erro = "Erro ao cadastrar. Tente novamente.";
                }
            }
        }
        require __DIR__ . '/../views/auth/cadastro.php';
    }
    
    public function logout() {
        if (session_status() === PHP_SESSION_NONE) {
            session_start();
        }
        session_destroy();
        // Redireciona para login ao sair
        echo "<script>window.location.href='/e-commerce/public/index.php?url=login';</script>";
        exit;
    }

    // FUNÇÃO AUXILIAR PARA REDIRECIONAR MESMO COM ERROS
    private function redirecionarParaHome() {
        if (!headers_sent()) {
            header("Location: /e-commerce/public/index.php");
        } else {
            // Se o header falhar (erro na linha 22), o JavaScript resolve
            echo "<script>window.location.href='/e-commerce/public/index.php';</script>";
        }
        exit;
    }
}