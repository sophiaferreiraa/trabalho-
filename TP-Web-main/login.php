<?php
require_once 'conexao.php';

if (session_status() == PHP_SESSION_NONE) {
    session_start();
}

$feedback = '';
$feedback_type = '';
$email = ''; 

if ($_SERVER["REQUEST_METHOD"] == "POST") {

    $email = filter_input(INPUT_POST, 'email', FILTER_SANITIZE_EMAIL) ?: '';
    $password_raw = filter_input(INPUT_POST, 'password', FILTER_UNSAFE_RAW, FILTER_FLAG_NO_ENCODE_QUOTES) ?: '';

    if (empty($email) || empty($password_raw)) {
        $feedback = "Por favor, preencha todos os campos.";
        $feedback_type = 'error';
    } else {
        try {
            $stmt = $pdo->prepare("SELECT id, senha, nome_completo, tipo_usuario FROM Users WHERE email = ?");
            $stmt->execute([$email]);
            $user = $stmt->fetch();

            if ($user && password_verify($password_raw, $user['senha'])) {
                
                $_SESSION['user_id'] = $user['id'];
                $_SESSION['nome_completo'] = $user['nome_completo'];
                $_SESSION['tipo_usuario'] = $user['tipo_usuario']; 
                
                header("Location: index.php");
                exit; 
                
            } else {
                $feedback = "Email ou senha incorretos.";
                $feedback_type = 'error';
            }
        } catch (PDOException $e) {
            $feedback = "Erro no servidor.";
            $feedback_type = 'error';
        }
    }
}
?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Login - Beleza Capilar</title>
    <link rel="stylesheet" href="stylesheet.css"> 
</head>
<body>
    <header class="header-menu">
        <nav class="navbar">
            <div class="logo"><a href="index.php">Beleza Capilar</a></div>
            <ul class="nav-links">
                <li><a href="cadastro.php">Cadastre-se</a></li>
                <li><a href="login.php">Login</a></li> 
                <li><a href="produtos.php">Produtos</a></li>
                <li class="cta"><a href="contato.php">Ajuda</a></li>
            </ul>
        </nav>
    </header>

    <div class="content-wrapper">
        <h2 class="page-title">Acesso ao Sistema</h2>
        <?php if ($feedback): ?>
            <div class="feedback-box <?php echo $feedback_type; ?>">
                <?php echo $feedback; ?>
            </div>
        <?php endif; ?>

        <form class="cadastro-form" method="POST" action="login.php">
            <input type="email" name="email" placeholder="Email" required value="<?= htmlspecialchars($email); ?>">
            <input type="password" name="password" placeholder="Senha" required>
            <button type="submit" class="cadastro-button">Entrar</button>
            <p style="text-align: center; margin-top: 10px;">
                Novo por aqui? <a href="cadastro.php" class="link-secondary">Cadastre-se</a>
            </p>
        </form>
    </div>
</body>
</html>