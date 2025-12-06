<?php
require_once 'conexao.php';
if (session_status() == PHP_SESSION_NONE) {
    session_start();
}


$erro = '';
$sucesso = '';
$nome = '';
$email = '';
$nascimento = '';

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    
    $nome = filter_input(INPUT_POST, 'nome_completo', FILTER_SANITIZE_FULL_SPECIAL_CHARS) ?: '';
    $email = filter_input(INPUT_POST, 'email', FILTER_SANITIZE_EMAIL) ?: '';
    $senha_raw = filter_input(INPUT_POST, 'senha', FILTER_UNSAFE_RAW, FILTER_FLAG_NO_ENCODE_QUOTES) ?: '';
    $nascimento = $_POST['data_nascimento'] ?? '';

    $nome = trim($nome);
    $email = trim($email);

    if (empty($nome) || empty($email) || empty($senha_raw) || empty($nascimento)) {
        $erro = "Todos os campos são obrigatórios.";
    } elseif (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $erro = "Formato de e-mail inválido.";
    }
    
    if (!$erro) {
        $senha_hash = password_hash($senha_raw, PASSWORD_DEFAULT);
        
        try {
            $stmt = $pdo->prepare("
                INSERT INTO Users (
                    nome_completo, 
                    data_nascimento, 
                    email, 
                    senha, 
                    tipo_usuario
                ) VALUES (?, ?, ?, ?, 'usuario')
            ");
            
            $stmt->execute([$nome, $nascimento, $email, $senha_hash]);

            $_SESSION['user_id'] = $pdo->lastInsertId();
            
            $sucesso = "Cadastro realizado com sucesso! Seu ID é: " . $_SESSION['user_id'] . ". Agora faça login."; 
            
        } catch (PDOException $e) {
            if (strpos($e->getMessage(), 'Duplicate entry') !== false) {
                 $erro = "Este e-mail já está cadastrado.";
            } else {
                 $erro = "Erro ao cadastrar: " . $e->getMessage();
            }
        }
    }
}
?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cadastro - Beleza Capilar</title>
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
            <li><a href="cadastro.php">Cadastre-se</a></li>
            <li><a href="login.php">Login</a></li> 
            <li><a href="produtos.php">Produtos</a></li>
            <li class="cta">
                <a href="contato.php">Ajuda</a>
            </li>
        </ul>
    </nav>
</header>

<div class="content-wrapper">
    <h2 class="page-title">Cadastre-se</h2>

    <?php if ($erro): ?>
        <div class="feedback-box error">
            <?= htmlspecialchars($erro); ?> 
        </div>
    <?php elseif ($sucesso): ?>
        <div class="feedback-box success">
            <?= htmlspecialchars($sucesso); ?> 
        </div>
    <?php endif; ?>

    <?php if (empty($sucesso)): ?>
        <form class="cadastro-form" method="POST" action="cadastro.php">
            <input type="text" name="nome_completo" placeholder="Nome completo" required value="<?= htmlspecialchars($nome); ?>"><br>
            <input type="date" name="data_nascimento" required value="<?= htmlspecialchars($nascimento); ?>"><br>
            <input type="email" name="email" placeholder="E-mail" required value="<?= htmlspecialchars($email); ?>"><br>
            <input type="password" name="senha" placeholder="Senha" required><br>
            <button type="submit" class="cadastro-button">Cadastrar</button>
            <p style="text-align: center; margin-top: 10px;">
                Já possui uma conta? <a href="login.php" class="link-secondary">Faça login</a>
            </p>
        </form>
    <?php endif; ?>
    
</div>

<script>
    document.getElementById('menu-toggle').onclick = function() {
        document.getElementById('nav-links').classList.toggle('active');
    };
</script>

</body>
</html>