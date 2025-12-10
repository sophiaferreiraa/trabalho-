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
                <span class="painel-title">Painel do Usuário</span>
                
                <div class="btn-group">
                    <a href="/e-commerce/public/index.php?url=simular_compra" class="btn-pink">Simular Compra</a>
                    
                    <a href="/e-commerce/public/index.php?url=minhas_compras" class="btn-outline">Meus Pedidos</a>
                </div>

                <?php if (isset($_SESSION['tipo_usuario']) && $_SESSION['tipo_usuario'] === 'admin'): ?>
                    <div style="margin-top: 15px; border-top: 1px solid #eee; padding-top: 15px;">
                        <a href="/e-commerce/public/index.php?url=admin_vendas" class="btn-admin">Painel Admin (Relatórios)</a>
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
            <p>Seja liso, ondulado, cacheado ou crespo (2A a 4C), nosso catálogo segmenta os produtos para garantir que você compre exatamente o que seu cabelo precisa.</p>
        </div>
        
        <div class="feature-card">
            <h3>Tratamentos Direcionados</h3>
            <p>Busca nutrição, reconstrução, reparação pós-química ou acidificação? Filtre instantaneamente para encontrar máscaras e linhas completas.</p>
        </div>
        
        <div class="feature-card">
            <h3>Clean Chic</h3>
            <p>Design moderno, limpo e focado na transparência. Garantimos a melhor experiência de navegação e compra.</p>
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