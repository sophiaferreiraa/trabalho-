<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Relatório de Vendas - Admin</title>
    <link rel="stylesheet" href="../../stylesheet.css">
    <style>
        .filtro-box { background: white; padding: 20px; border-radius: 8px; box-shadow: var(--sombra-leve); display: flex; gap: 10px; align-items: flex-end; justify-content: center; margin-bottom: 30px; }
        .filtro-group { display: flex; flex-direction: column; }
        .total-box { font-size: 1.5rem; font-weight: bold; color: var(--cor-secundaria); text-align: right; margin-top: 20px; }
        .table-admin { width: 100%; background: white; border-radius: 8px; overflow: hidden; }
        .table-admin th { background-color: var(--cor-secundaria); color: white; padding: 15px; }
        .table-admin td { padding: 15px; border-bottom: 1px solid #eee; }
    </style>
</head>
<body>

    <header class="header-menu">
        <nav class="navbar">
            <div class="logo"><a href="#">Admin Panel</a></div>
            <ul class="nav-links">
                <li><a href="../../index.php">Voltar ao Site</a></li>
            </ul>
        </nav>
    </header>

    <div class="content-wrapper">
        <h2 class="page-title">Relatório de Vendas</h2>

        <form method="GET" action="" class="filtro-box">
            <input type="hidden" name="controller" value="venda">
            <input type="hidden" name="action" value="relatorio">
            
            <div class="filtro-group">
                <label>Data Início:</label>
                <input type="date" name="data_inicio" value="<?= $_GET['data_inicio'] ?? '' ?>">
            </div>
            <div class="filtro-group">
                <label>Data Fim:</label>
                <input type="date" name="data_fim" value="<?= $_GET['data_fim'] ?? '' ?>">
            </div>
            <button type="submit" class="cadastro-button" style="width: auto; margin:0;">Filtrar Relatório</button>
        </form>

        <table class="table-admin">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Cliente</th>
                    <th>Data</th>
                    <th>Status</th>
                    <th>Valor</th>
                </tr>
            </thead>
            <tbody>
                <?php foreach ($vendas as $v): ?>
                <tr>
                    <td><?= $v['id']; ?></td>
                    <td><?= htmlspecialchars($v['nome_completo']); ?></td>
                    <td><?= date('d/m/Y', strtotime($v['data_pedido'])); ?></td>
                    <td><?= $v['status']; ?></td>
                    <td style="color: var(--cor-acento); font-weight:bold;">
                        R$ <?= number_format($v['valor_total'], 2, ',', '.'); ?>
                    </td>
                </tr>
                <?php endforeach; ?>
            </tbody>
        </table>

        <div class="total-box">
            Total no Período: R$ <?= number_format($totalPeriodo, 2, ',', '.'); ?>
        </div>
    </div>
</body>
</html>