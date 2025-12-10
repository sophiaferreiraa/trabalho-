<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Relatório Admin - Beleza Capilar</title>
    <link rel="stylesheet" href="/e-commerce/public/stylesheet.css">
    <style>
        .card-tabela {
            background-color: white; padding: 30px; border-radius: 10px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); width: 100%; max-width: 1000px; margin: 0 auto;
        }
        .filtro-form {
            display: flex; gap: 15px; align-items: flex-end; flex-wrap: wrap;
            background: #fff0f3; padding: 20px; border-radius: 8px; margin-bottom: 25px;
        }
        .input-group { display: flex; flex-direction: column; }
        .total-box { text-align: right; margin-top: 20px; padding-top: 20px; border-top: 2px dashed #eee; }
        .total-valor { font-size: 2.5rem; color: #fa8a99; font-weight: 900; }
        table { width: 100%; border-collapse: collapse; }
        th { background-color: #264653; color: white; padding: 15px; text-align: left; }
        td { padding: 15px; border-bottom: 1px solid #eee; }
    </style>
</head>
<body>

<header class="header-menu">
    <nav class="navbar">
        <div class="logo"><a href="/e-commerce/public/index.php">Painel Admin</a></div>
        <ul class="nav-links">
            <li><a href="/e-commerce/public/index.php">Voltar ao Site</a></li>
            <li><a href="/e-commerce/public/index.php?url=logout">Sair</a></li>
        </ul>
    </nav>
</header>

<div class="content-wrapper">
    <h2 class="page-title">Relatório de Vendas Realizadas</h2>

    <div class="card-tabela">
        
        <form method="GET" action="/e-commerce/public/index.php" class="filtro-form">
            <input type="hidden" name="url" value="admin_vendas">
            
            <div class="input-group">
                <label>De:</label>
                <input type="date" name="data_inicio" value="<?= $_GET['data_inicio'] ?? '' ?>">
            </div>
            <div class="input-group">
                <label>Até:</label>
                <input type="date" name="data_fim" value="<?= $_GET['data_fim'] ?? '' ?>">
            </div>
            
            <button type="submit" class="cadastro-button" style="width: auto; margin: 0;">Filtrar</button>
        </form>

        <div style="overflow-x: auto;">
            <table>
                <thead>
                    <tr>
                        <th>ID Pedido</th>
                        <th>Cliente</th>
                        <th>Data</th>
                        <th>Status</th>
                        <th>Valor</th>
                    </tr>
                </thead>
                <tbody>
                    <?php if (!empty($vendas)): ?>
                        <?php foreach ($vendas as $v): ?>
                        <tr>
                            <td>#<?= $v['id']; ?></td>
                            <td><?= htmlspecialchars($v['nome_completo']); ?></td>
                            <td><?= date('d/m/Y', strtotime($v['data_pedido'])); ?></td>
                            <td><?= htmlspecialchars($v['status']); ?></td>
                            <td>R$ <?= number_format($v['valor_total'], 2, ',', '.'); ?></td>
                        </tr>
                        <?php endforeach; ?>
                    <?php else: ?>
                        <tr><td colspan="5" style="text-align:center;">Nenhuma venda encontrada no período.</td></tr>
                    <?php endif; ?>
                </tbody>
            </table>
        </div>

        <div class="total-box">
            <span style="font-weight:bold; color:#264653; display:block;">FATURAMENTO TOTAL</span>
            <span class="total-valor">R$ <?= number_format($totalPeriodo, 2, ',', '.'); ?></span>
        </div>
    </div>
</div>
</body>
</html>