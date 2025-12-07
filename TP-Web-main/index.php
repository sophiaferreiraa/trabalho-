<?php
require_once 'conexao.php';
if (session_status() == PHP_SESSION_NONE) {
    session_start();
}
?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Início - Beleza Capilar</title>
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
    <section class="hero-section">
        <?php if (isset($_SESSION['nome_completo'])): ?>
            <p style="color: var(--cor-secundaria); font-weight: bold; margin-bottom: 10px;">
                Bem-vindo(a), <?= htmlspecialchars($_SESSION['nome_completo']); ?>!
            </p>
        <?php endif; ?>

        <h1>Sua Jornada Capilar Começa Aqui.</h1>
        <p>
            Encontre o produto ideal para o seu fio de forma fácil e precisa com a Beleza Capilar.
        </p>
        
        <?php if (isset($_SESSION['user_id'])): ?>
            <div style="margin-top: 20px; padding: 20px; background: #fff0f3; border: 1px dashed var(--cor-principal); border-radius: 8px; display: inline-block;">
                <h3 style="font-size:1.2rem; margin-bottom:10px;">Testeeeeeee - (modificar essa parte)</h3>
                <a href="simular_compra.php" class="cadastro-button" style="text-decoration:none; display:inline-block; margin:5px;">1. Simular Compra</a>
                <a href="ver_compras.php" class="btn-secondary" style="margin:5px;">2. Ver Cliente</a>
                <a href="admin_vendas.php" class="btn-secondary" style="margin:5px;">3. Ver Admin</a>
            </div>
        <?php else: ?>
            <a href="produtos.php" class="btn-primary-large">Ver Produtos Agora</a>
        <?php endif; ?>
    </section>
    
    <section class="features-grid">
        <div class="feature-card">
            <h3>Filtro por Curvatura</h3>
            <p>Seja liso, ondulado, cacheado ou crespo (2A a 4C), nosso catálogo segmenta os produtos para você.</p>
        </div>
        <div class="feature-card">
            <h3>Tratamentos Direcionados</h3>
            <p>Busca nutrição ou reconstrução? Filtre instantaneamente para encontrar o ideal.</p>
        </div>
        <div class="feature-card">
            <h3>Clean Chic</h3>
            <p>Design moderno e focado na transparência para a melhor experiência de compra.</p>
        </div>
    </section>
</div>

<script>
    document.getElementById('menu-toggle').onclick = function() {
        document.getElementById('nav-links').classList.toggle('active');
    };
</script>

</body>
</html>