<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Checkout - Beleza Capilar</title>
    <link rel="stylesheet" href="/e-commerce/public/stylesheet.css">
</head>
<body>
<header class="header-menu">
    <nav class="navbar">
        <div class="logo"><a href="/e-commerce/public/index.php">Beleza Capilar</a></div>
    </nav>
</header>

<div class="content-wrapper">
    <h2 class="page-title">Simular Checkout</h2>
    
    <div style="background:white; padding:30px; border-radius:8px; max-width:500px; margin:0 auto; text-align:center; box-shadow:0 2px 5px rgba(0,0,0,0.1);">
        <h3>Carrinho de Teste</h3>
        <ul style="list-style:none; padding:0; margin:20px 0; color:#555; text-align:left;">
            <li style="padding:5px 0; border-bottom:1px solid #eee;">1x Shampoo Wella - R$ 89,90</li>
            <li style="padding:5px 0; border-bottom:1px solid #eee;">2x Máscara L'Oréal - R$ 149,90</li>
            <li style="padding:15px 0; font-weight:bold; font-size:1.2rem;">Total: R$ 389,70</li>
        </ul>
        <form method="POST" action="/e-commerce/public/index.php?url=finalizar_compra">
            <button type="submit" class="cadastro-button">Finalizar Compra</button>
        </form>
        <br>
        <a href="/e-commerce/public/index.php" class="btn-secondary">Cancelar</a>
    </div>
</div>
</body>
</html>