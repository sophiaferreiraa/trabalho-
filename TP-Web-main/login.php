<?php
require_once 'conexao.php';

if (session_status() == PHP_SESSION_NONE) {
    session_start();
}

$feedback = '';
$feedback_type = '';
$email = ''; 
$login_successful = false; 


if ($_SERVER["REQUEST_METHOD"] == "POST") {

    $email = filter_input(INPUT_POST, 'email', FILTER_SANITIZE_EMAIL) ?: '';
    $password_raw = filter_input(INPUT_POST, 'password', FILTER_UNSAFE_RAW, FILTER_FLAG_NO_ENCODE_QUOTES) ?: '';

    if (empty($email) || empty($password_raw)) {
        $feedback = "Por favor, preencha o e-mail e a senha.";
        $feedback_type = 'error';
    } else {
        
        try {
            $stmt = $pdo->prepare("SELECT id, senha, nome_completo FROM Users WHERE email = ?");
            $stmt->execute([$email]);
            $user = $stmt->fetch();

            if ($user) {
                if (password_verify($password_raw, $user['senha'])) {
                    
                    $_SESSION['user_id'] = $user['id'];
                    $_SESSION['nome_completo'] = $user['nome_completo'];
                    
                    header("Location: index.php");
                    exit; 
                    
                } else {
                    $feedback = "Email ou senha incorretos.";
                    $feedback_type = 'error';
                }
            } else {
                $feedback = "Email ou senha incorretos.";
                $feedback_type = 'error';
            }
        } catch (PDOException $e) {
            $feedback = "Erro no servidor. Tente novamente mais tarde.";
            $feedback_type = 'error';
        }
    }
}

?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Acesso - Login</title>
    <link rel="stylesheet" href="stylesheet.css"> 
</head>
<body>
    
    <header class="header-menu">
    <nav class="navbar">
        <div class="logo">
            <a href="index.php">Beleza Capilar</a> 
        </div>
        
        <div class="menu-toggle" id="menu-toggle">&#9776;</div> 
        
        <ul class="nav-links" id="nav-links">
            <?php if (isset($_SESSION['user_id'])): ?>
                <li><a href="ver_compras.php">Minha Conta</a></li>
                <li><a href="logout.php">Sair</a></li>
            <?php else: ?>
                <li><a href="cadastro.php">Cadastre-se</a></li>
                <li><a href="login.php">Login</a></li> 
            <?php endif; ?>
            <li><a href="produtos.php">Produtos</a></li>
            <li class="cta">
                <a href="contato.php">Ajuda</a>
            </li>
        </ul>
    </nav>
</header>


    <div class="content-wrapper">
        <h2 class="page-title">Acesso ao Sistema</h2>

        <?php 
        if ($feedback): 
        ?>
            <div class="feedback-box <?php echo $feedback_type; ?>">
                <?php echo $feedback; ?>
            </div>
        <?php endif; ?>

        <form class="cadastro-form" method="POST" action="login.php">
            <input type="email" name="email" placeholder="Email" required aria-label="Email" value="<?php echo htmlspecialchars($email); ?>">
            
            <input type="password" name="password" placeholder="Senha" required aria-label="Senha">
            <button type="submit" class="cadastro-button">Entrar</button>
            
            <p style="text-align: center; margin-top: 10px;">
                Ainda não tem conta? <a href="cadastro.php" class="link-secondary">Cadastre-se</a>
            </p>
        </form>
    </div>
    
    <script>
        document.getElementById('menu-toggle').onclick = function() {
            document.getElementById('nav-links').classList.toggle('active');
        };
    </script>
</body>
</html>