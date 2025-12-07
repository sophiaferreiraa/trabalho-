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
    
    <section class="hero-section">
        <h1>Sua Jornada Capilar Começa Aqui.</h1>
        
        <p>
            Bem-vindo à Beleza Capilar, sua loja online especializada em cosméticos de alta performance. Encontre o produto ideal para o seu fio de forma fácil e precisa, com o sistema de filtragem mais avançado do mercado.
        </p>
        
        <a href="produtos.php" class="btn-primary-large">Ver Produtos Agora</a>
    </section>
    
    <section class="features-grid">
        
        <div class="feature-card">
            <h3>Filtro por Curvatura</h3>
            <p>Seja liso, ondulado, cacheado ou crespo (2A a 4C), nosso catálogo segmenta os produtos para garantir que você compre exatamente o que seu cabelo precisa. Sem erro!</p>
        </div>
        
        <div class="feature-card">
            <h3>Tratamentos Direcionados</h3>
            <p>Busca nutrição, reconstrução, reparação pós-química ou acidificação? Filtre instantaneamente para encontrar máscaras e linhas completas focadas no seu cronograma capilar.</p>
        </div>
        
        <div class="feature-card">
            <h3>Clean Chic e Profissional</h3>
            <p>Design moderno, limpo e focado na transparência. Garantimos a melhor experiência de navegação e compra, com foco total na qualidade e informação dos produtos.</p>
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
