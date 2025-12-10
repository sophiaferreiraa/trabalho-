<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cadastro - Beleza Capilar</title>
     <link rel="stylesheet" href="/e-commerce/public/stylesheet.css">
</head>
<body>

<header class="header-menu">
    <nav class="navbar">
        <div class="logo">
            <a href="index.php">Beleza Capilar</a> 
        </div>
        
        <div class="menu-toggle" id="menu-toggle">&#9776;</div> 
        
        <ul class="nav-links" id="nav-links">
          <li><a href="/e-commerce/public/index.php?url=cadastro">Cadastre-se</a></li>
<li><a href="/e-commerce/public/index.php?url=login">Login</a></li>
<li><a href="/e-commerce/public/index.php?url=produtos">Produtos</a></li>
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
            <form class="cadastro-form" method="POST" action="/e-commerce/public/index.php?url=cadastro">            <input type="text" name="nome_completo" placeholder="Nome completo" required value="<?= htmlspecialchars($nome); ?>"><br>
            <input type="date" name="data_nascimento" required value="<?= htmlspecialchars($nascimento); ?>"><br>
            <input type="email" name="email" placeholder="E-mail" required value="<?= htmlspecialchars($email); ?>"><br>
            <input type="password" name="senha" placeholder="Senha" required><br>
            <button type="submit" class="cadastro-button">Cadastrar</button>
            <p style="text-align: center; margin-top: 10px;">
                Já possui uma conta? <a href="/e-commerce/public/index.php?url=login" class="link-secondary">Faça login</a>
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

