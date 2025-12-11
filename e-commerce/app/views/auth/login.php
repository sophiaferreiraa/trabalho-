
<!DOCTYPE html>
<html lang="pt">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Acesso - Login</title>
    <link rel="stylesheet" href="/e-commerce/public/stylesheet.css">
</head>
<body>

<header class="header-menu">
    <nav class="navbar">
        <div class="logo">
            <a href="/e-commerce/public/index.php">Beleza Capilar</a>
        </div>

        <div class="menu-toggle" id="menu-toggle">&#9776;</div>

        <ul class="nav-links" id="nav-links">
            <?php if (isset($_SESSION['user_id'])): ?>
                <li><a href="#">Minha Conta</a></li>
                <li><a href="#">Sair</a></li>
            <?php else: ?>
                <li><a href="/e-commerce/public/index.php?url=cadastro">Cadastre-se</a></li>
                <li><a href="/e-commerce/public/index.php?url=login">Login</a></li>
            <?php endif; ?>
            <li><a href="/e-commerce/public/index.php?url=produtos">Produtos</a></li>
        </ul>
    </nav>
</header>

<div class="content-wrapper">
    <h2 class="page-title">Acesso ao Sistema</h2>

    <?php if (!empty($feedback)): ?>
        <div class="feedback-box <?= $tipo ?>">
            <?= htmlspecialchars($feedback) ?>
        </div>
    <?php endif; ?>

    <form class="cadastro-form" method="POST" action="/e-commerce/public/index.php?url=login">
        <input type="email" name="email" placeholder="Email" required aria-label="Email"
               value="<?= htmlspecialchars($email ?? '') ?>">

        <input type="password" name="password" placeholder="Senha" required aria-label="Senha">

        <button type="submit" class="cadastro-button">Entrar</button>

        <p style="text-align: center; margin-top: 10px;">
            Ainda não tem conta?
            <a href="/e-commerce/public/index.php?url=cadastro" class="link-secondary">Cadastre-se</a>
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

