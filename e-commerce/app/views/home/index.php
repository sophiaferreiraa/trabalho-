<?php
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}
?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Início - Beleza Capilar</title>
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
                <li><a href="#" style="color: #264653;">Minha Conta</a></li>
                <li><a href="/e-commerce/public/index.php?url=logout">Sair</a></li>
            <?php else: ?>
                <li><a href="/e-commerce/public/index.php?url=cadastro">Cadastre-se</a></li>
                <li><a href="/e-commerce/public/index.php?url=login">Login</a></li> 
            <?php endif; ?>
            
            <li><a href="/e-commerce/public/index.php?url=produtos">Produtos</a></li>
        </ul>
    </nav>
</header>

<div class="content-wrapper">
    
    <section class="hero-section">
        <?php if (isset($_SESSION['nome'])): ?>
            <p style="color: #fa8a99; font-weight: bold; margin-bottom: 5px;">
                Olá, <?= htmlspecialchars($_SESSION['nome']); ?>!
            </p>
        <?php endif; ?>

        <h1>Sua Jornada Capilar Começa Aqui.</h1>
        
        <p>
            Encontre o produto ideal para o seu fio com a Beleza Capilar.
        </p>
        
        <?php if (isset($_SESSION['user_id'])): ?>
            
            <div class="painel-box">
                <span class="painel-title">Painel do Cliente</span>
                <div class="btn-group">
                    <a href="/e-commerce/public/index.php?url=simular_compra" class="btn-pink">Simular Compra</a>
                    <a href="/e-commerce/public/index.php?url=minhas_compras" class="btn-outline">Meus Pedidos</a>
                </div>

                <?php if (isset($_SESSION['tipo_usuario']) && $_SESSION['tipo_usuario'] === 'admin'): ?>
                    <div style="margin-top: 25px; border-top: 2px dashed #fa8a99; padding-top: 20px;">
                        <span class="painel-title" style="color: #fa8a99;">Área Administrativa</span>
                        
                        <div class="btn-group">
                            <a href="/e-commerce/public/index.php?url=novo_produto" class="btn-pink">Cadastrar Produto</a>
                            
                            <a href="/e-commerce/public/index.php?url=admin_produtos" class="btn-outline">Gerenciar Produtos</a>
                            
                            <a href="/e-commerce/public/index.php?url=admin_vendas" class="btn-admin">Relatório Vendas</a>
                        </div>
                    </div>
                <?php endif; ?>
            </div>

        <?php else: ?>
            <a href="/e-commerce/public/index.php?url=produtos" class="btn-primary-large">Ver Produtos Agora</a>
        <?php endif; ?>
    </section>
    
    <section class="features-grid">
        <div class="feature-card">
            <h3>Filtro por Curvatura</h3>
            <p>Seja liso, ondulado, cacheado ou crespo (2A a 4C), nosso catálogo segmenta os produtos.</p>
        </div>
        
        <div class="feature-card">
            <h3>Tratamentos Direcionados</h3>
            <p>Busca nutrição, reconstrução ou reparação? Filtre instantaneamente.</p>
        </div>
        
        <div class="feature-card">
            <h3>Clean Chic</h3>
            <p>Design moderno, limpo e focado na transparência.</p>
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