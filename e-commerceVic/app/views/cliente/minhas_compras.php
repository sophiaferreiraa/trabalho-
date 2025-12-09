<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Meus Pedidos - Beleza Capilar</title>
    <link rel="stylesheet" href="/e-commerce/public/stylesheet.css">
    <style>
        .card-tabela {
            background-color: white; padding: 30px; border-radius: 10px;    
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); width: 100%; max-width: 900px; margin: 0 auto;
        }
        table { width: 100%; border-collapse: collapse; margin-top: 10px; }
        th { text-align: left; color: #264653; padding: 15px; border-bottom: 2px solid #ffe4e4; }
        td { padding: 15px; border-bottom: 1px solid #eee; }
        .status { background: #ffe4e4; color: #fa8a99; padding: 5px 10px; border-radius: 15px; font-weight: bold; font-size: 0.9rem; }
    </style>
</head>
<body>

<header class="header-menu">
    <nav class="navbar">
        <div class="logo"><a href="/e-commerce/public/index.php">Beleza Capilar</a></div>
        <ul class="nav-links">
            <li><a href="/e-commerce/public/index.php?url=produtos">Produtos</a></li>
            <li><a href="/e-commerce/public/index.php?url=logout">Sair</a></li>
        </ul>
    </nav>
</header>

<div class="content-wrapper">
    <h2 class="page-title">Meus Pedidos</h2>

    <div class="card-tabela">
        <?php if (empty($compras)): ?>
            <div style="text-align: center; padding: 20px;">
                <p>Você ainda não realizou nenhuma compra.</p>
                <a href="/e-commerce/public/index.php?url=simular_compra" class="cadastro-button" style="display:inline-block; margin-top:10px;">Simular Compra</a>
            </div>
        <?php else: ?>
            <div style="overflow-x: auto;">
                <table>
                    <thead>
                        <tr>
                            <th>Pedido</th>
                            <th>Data</th>
                            <th>Status</th>
                            <th>Total</th>
                        </tr>
                    </thead>
                    <tbody>
                        <?php foreach ($compras as $compra): ?>
                        <tr>
                            <td>#<?= $compra['id']; ?></td>
                            <td><?= date('d/m/Y', strtotime($compra['data_pedido'])); ?></td>
                            <td><span class="status"><?= $compra['status']; ?></span></td>
                            <td style="color: #fa8a99; font-weight: bold;">
                                R$ <?= number_format($compra['valor_total'], 2, ',', '.'); ?>
                            </td>
                        </tr>
                        <?php endforeach; ?>
                    </tbody>
                </table>
            </div>
        <?php endif; ?>
        
        <div style="margin-top: 20px; text-align: center;">
            <a href="/e-commerce/public/index.php" class="btn-secondary">Voltar ao Início</a>
        </div>
    </div>
</div>
</body>
</html>