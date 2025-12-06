<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Minhas Compras - Beleza Capilar</title>
    <link rel="stylesheet" href="../../stylesheet.css">
    <style>
        /* CSS Específico para tabela simples */
        .table-compras { width: 100%; border-collapse: collapse; margin-top: 20px; background: white; }
        .table-compras th, .table-compras td { padding: 12px; border: 1px solid #ddd; text-align: left; }
        .table-compras th { background-color: var(--cor-principal); color: white; }
        .status-badge { padding: 5px 10px; border-radius: 15px; font-size: 0.8em; font-weight: bold; }
        .status-Confirmado { background-color: #d4edda; color: #155724; }
    </style>
</head>
<body>
    
    <header class="header-menu">
        <nav class="navbar">
            <div class="logo"><a href="../../index.php">Beleza Capilar</a></div>
            <ul class="nav-links">
                <li><a href="../../index.php">Início</a></li>
                <li><a href="../../logout.php">Sair</a></li>
            </ul>
        </nav>
    </header>

    <div class="content-wrapper">
        <h2 class="page-title">Histórico de Pedidos</h2>

        <?php if (empty($compras)): ?>
            <div class="feedback-box">
                Você ainda não realizou nenhuma compra. <br>
                <a href="../../produtos.php" class="btn-secondary">Ir para a Loja</a>
            </div>
        <?php else: ?>
            <table class="table-compras">
                <thead>
                    <tr>
                        <th># Pedido</th>
                        <th>Data</th>
                        <th>Status</th>
                        <th>Total</th>
                        <th>Detalhes</th>
                    </tr>
                </thead>
                <tbody>
                    <?php foreach ($compras as $compra): ?>
                    <tr>
                        <td>#<?= $compra['id']; ?></td>
                        <td><?= date('d/m/Y H:i', strtotime($compra['data_pedido'])); ?></td>
                        <td>
                            <span class="status-badge status-<?= $compra['status']; ?>">
                                <?= $compra['status']; ?>
                            </span>
                        </td>
                        <td>R$ <?= number_format($compra['valor_total'], 2, ',', '.'); ?></td>
                        <td>
                            <button class="btn-secondary" style="padding: 5px 10px; margin:0; font-size: 0.8rem;">Ver Itens</button>
                        </td>
                    </tr>
                    <?php endforeach; ?>
                </tbody>
            </table>
        <?php endif; ?>
    </div>
</body>
</html>