<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Meu Carrinho - Beleza Capilar</title>
    <link rel="stylesheet" href="/e-commerce/public/stylesheet.css">
</head>
<body>
<header class="header-menu">
    <nav class="navbar">
        <div class="logo"><a href="/e-commerce/public/index.php">Beleza Capilar</a></div>
    </nav>
</header>

<div class="content-wrapper">
    <h2 class="page-title">Meu Carrinho</h2>

    <div style="background:white; padding:30px; border-radius:8px; max-width:600px; margin:0 auto; box-shadow:0 2px 5px rgba(0,0,0,0.1);">
        
        <?php if (!empty($carrinho)): ?>
            <ul style="list-style:none; padding:0; margin:0; color:#555; text-align:left;">
                <?php
                $total = 0;
                foreach ($carrinho as $item):
                    $produto = ProdutoDAO::buscarPorId($item->getId());
                    $subtotal = $item->getSubtotal();
                    $total += $subtotal;
                ?>
                <li style="padding:5px 0; border-bottom:1px solid #eee;">
                    <form method="POST" action="/e-commerce/public/index.php?url=alterarQtd" style="display:flex; justify-content:space-between; align-items:center;">
                        <span><?= htmlspecialchars($produto['nome']) ?> - R$ <?= number_format($produto['preco'],2,',','.') ?></span>
                        <div>
                            <input type="hidden" name="id_produto" value="<?= $item->getId() ?>">
                            <input type="number" name="qtd" value="<?= $item->getQtd() ?>" min="1" style="width:50px;">
                            <button type="submit" class="cadastro-button" style="margin-left:5px;">Atualizar</button>
                            <a href="/e-commerce/public/index.php?url=removerItem&id_produto=<?= $item->getId() ?>" class="btn-secondary" style="margin-left:5px;">Remover</a>
                        </div>
                    </form>
                </li>
                <?php endforeach; ?>

                <li style="padding:15px 0; font-weight:bold; font-size:1.2rem; text-align:right;">
                    Total: R$ <?= number_format($total,2,',','.') ?>
                </li>
            </ul>

            <form method="POST" action="/e-commerce/public/index.php?url=finalizarCompra" style="text-align:center; margin-top:20px;">
                <button type="submit" class="cadastro-button">Finalizar Compra</button>
            </form>
            <br>
            <a href="/e-commerce/public/index.php" class="btn-secondary" style="display:inline-block; text-align:center;">Cancelar</a>

        <?php else: ?>
            <p style="text-align:center; font-size:1rem; color:#888;">Seu carrinho está vazio.</p>
            <a href="/e-commerce/public/index.php" class="btn-secondary" style="display:block; text-align:center; margin-top:20px;">Voltar à loja</a>
        <?php endif; ?>

    </div>
</div>
</body>
</html>
