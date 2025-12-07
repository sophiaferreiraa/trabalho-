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
    <title>Início - Beleza Capilar</title>
    <link rel="stylesheet" href="stylesheet.css">
</head>
<body>

<header class="header-menu">
    <nav class="navbar">
        <div class="logo"><a href="index.php">Beleza Capilar</a></div>
        <ul class="nav-links">
            <?php if (isset($_SESSION['user_id'])): ?>
                <li><a href="ver_compras.php">Minha Conta</a></li>
                
                <?php if (isset($_SESSION['tipo_usuario']) && $_SESSION['tipo_usuario'] === 'admin'): ?>
                    <li><a href="admin_vendas.php" style="color:#fa8a99; font-weight:bold;">Admin</a></li>
                <?php endif; ?>

                <li><a href="logout.php">Sair</a></li>
            <?php else: ?>
                <li><a href="cadastro.php">Cadastre-se</a></li>
                <li><a href="login.php">Login</a></li> 
            <?php endif; ?>
            
            <li><a href="produtos.php">Produtos</a></li>
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
        <p>Encontre o produto ideal para o seu fio com a Beleza Capilar.</p>
        
        <?php if (isset($_SESSION['user_id'])): ?>
            <div style="margin-top: 20px; padding: 20px; background: #fff0f3; border: 1px dashed var(--cor-principal); border-radius: 8px; display: inline-block;">
                <h3 style="font-size:1.2rem; margin-bottom:10px;">Painel do Usuário</h3>
                
                <a href="simular_compra.php" class="cadastro-button" style="text-decoration:none; margin:5px;">Simular Compra</a>
                <a href="ver_compras.php" class="btn-secondary" style="margin:5px;">Meus Pedidos</a>
                
                <?php if (isset($_SESSION['tipo_usuario']) && $_SESSION['tipo_usuario'] === 'admin'): ?>
                    <a href="admin_vendas.php" class="btn-secondary" style="background:#264653; color:white!important; margin:5px;">Painel Admin</a>
                <?php endif; ?>
            </div>
        <?php else: ?>
            <a href="produtos.php" class="btn-primary-large">Ver Produtos Agora</a>
        <?php endif; ?>
    </section>
    
    <section class="features-grid">
        <div class="feature-card">
            <h3>Filtro por Curvatura</h3>
            <p>Seja liso, ondulado, cacheado ou crespo, temos o produto certo.</p>
        </div>
        <div class="feature-card">
            <h3>Tratamentos Direcionados</h3>
            <p>Nutrição, reconstrução e muito mais para o seu cronograma.</p>
        </div>
        <div class="feature-card">
            <h3>Clean Chic</h3>
            <p>A melhor experiência de compra para você.</p>
        </div>
    </section>
</div>

</body>
</html>