<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Produtos - Beleza Capilar</title>
    <link rel="stylesheet" href="/e-commerce/public/stylesheet.css">
    <style>
        .filter-bar { margin-bottom: 30px; text-align: center; }
        .filter-btn { 
            padding: 8px 15px; margin: 5px; 
            border: 1px solid #264653; border-radius: 20px; 
            color: #264653; display: inline-block;
        }
        .filter-btn:hover, .filter-btn.active { 
            background-color: #264653; color: white; 
        }
        .btn-novo-menu {
            margin-left: 20px; 
            background-color: #fa8a99; 
            color: white !important; 
            padding: 8px 15px; 
            border-radius: 20px; 
            font-size: 0.9rem;
            text-transform: uppercase;
            font-weight: bold;
        }
        .btn-novo-menu:hover { background-color: #fe7cbf; }
    </style>
</head>
<body>

<header class="header-menu">
    <nav class="navbar">
        <div class="logo">
            <a href="/e-commerce/public/index.php">Beleza Capilar</a>
            <a href="/e-commerce/public/index.php?url=produtos" class="btn-novo-menu">PRODUTOS</a>
        </div>
        <ul class="nav-links">
            <?php if (isset($_SESSION['user_id'])): ?>
                <li><a href="/e-commerce/public/index.php?url=minhas_compras">Meus Pedidos</a></li>
                <li><a href="/e-commerce/public/index.php?url=logout">Sair</a></li>
            <?php else: ?>
                <li><a href="/e-commerce/public/index.php?url=login">Entrar</a></li>
                <li><a href="/e-commerce/public/index.php?url=cadastro">Cadastrar</a></li>
            <?php endif; ?>
        </ul>
    </nav>
</header>

<div class="content-wrapper">
    <h2 class="page-title">Nossos Produtos</h2>

    <div class="filter-bar">
        <a href="/e-commerce/public/index.php?url=produtos" class="filter-btn <?= !isset($_GET['categoria']) ? 'active' : '' ?>">Todos</a>
        <?php foreach ($categorias as $cat): ?>
            <a href="/e-commerce/public/index.php?url=produtos&categoria=<?= $cat['id'] ?>" 
               class="filter-btn <?= (isset($_GET['categoria']) && $_GET['categoria'] == $cat['id']) ? 'active' : '' ?>">
               <?= htmlspecialchars($cat['nome']) ?>
            </a>
        <?php endforeach; ?>
    </div>

    <div class="product-grid">
        <?php if (!empty($produtos)): ?>
            <?php foreach ($produtos as $p): ?>
            <div class="product-card">
                <div style="background:#ffe4e4; height:150px; margin-bottom:15px; display:flex; align-items:center; justify-content:center; color:#fa8a99; font-weight:bold;">Imagem</div>
                
                <h3><?= htmlspecialchars($p->getNome()) ?></h3>
                <p style="color: #666; font-size: 0.9rem; min-height: 40px; margin-bottom:10px;">
                    <?= htmlspecialchars($p->getDescricao()) ?>
                </p>
                <div style="background:#eee; display:inline-block; padding:2px 8px; border-radius:4px; font-size:0.8rem; margin-bottom:10px;">
                    <?= htmlspecialchars($p->getNomeCategoria()) ?>
                </div>
                <div class="price">R$ <?= number_format($p->getPreco(), 2, ',', '.') ?></div>
                
                <?php if (isset($_SESSION['user_id'])): ?>
                    <a href="/e-commerce/public/index.php?url=simular_compra" class="btn-pink">Comprar</a>
                <?php else: ?>
                    <a href="/e-commerce/public/index.php?url=login" class="btn-outline">Login para Comprar</a>
                <?php endif; ?>
            </div>
            <?php endforeach; ?>
        <?php else: ?>
            <p style="text-align: center; width: 100%;">Nenhum produto encontrado nesta categoria.</p>
        <?php endif; ?>
    </div>
</div>
</body>
</html>