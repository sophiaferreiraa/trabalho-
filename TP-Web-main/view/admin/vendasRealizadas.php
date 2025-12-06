<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Relatório Admin - Beleza Capilar</title>
    <link rel="stylesheet" href="../../stylesheet.css">
    <style>
        .card-conteudo {
            background-color: white;
            padding: 40px;
            border-radius: 12px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
            width: 100%;
            max-width: 1000px;
            margin: 0 auto;
        }

        /* Área de Filtros */
        .filtro-box {
            background-color: #fff0f3; /* Rosa bem clarinho para destacar */
            padding: 20px;
            border-radius: 8px;
            display: flex;
            gap: 15px;
            align-items: flex-end;
            margin-bottom: 30px;
            flex-wrap: wrap;
        }

        .input-group {
            display: flex;
            flex-direction: column;
        }
        
        .input-group label {
            font-size: 0.85rem;
            font-weight: bold;
            color: var(--cor-secundaria);
            margin-bottom: 5px;
        }

        .input-group input {
            padding: 10px;
            border: 1px solid #e0e0e0;
            border-radius: 5px;
            outline: none;
        }

        .table-custom {
            width: 100%;
            border-collapse: collapse;
        }

        .table-custom th {
            background-color: var(--cor-secundaria);
            color: white;
            padding: 12px 15px;
            text-align: left;
            border-radius: 4px;
        }
        /* Remove radius dos cantos internos para ficar grudado */
        .table-custom th:not(:first-child):not(:last-child) { border-radius: 0; }
        .table-custom th:first-child { border-radius: 4px 0 0 4px; }
        .table-custom th:last-child { border-radius: 0 4px 4px 0; }

        .table-custom td {
            padding: 15px;
            border-bottom: 1px solid #eee;
            color: #555;
        }

        /* Caixa de Total */
        .total-destaque {
            margin-top: 30px;
            text-align: right;
            padding-top: 20px;
            border-top: 2px dashed #eee;
        }
        
        .total-valor {
            font-size: 2.5rem;
            font-weight: 800;
            color: var(--cor-principal);
            line-height: 1;
        }
        
        .total-label {
            color: #888;
            font-size: 0.9rem;
            text-transform: uppercase;
            letter-spacing: 1px;
        }
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
        <h2 class="page-title">Relatório Financeiro</h2>

        <div class="card-conteudo">
            
            <form method="GET" action="" class="filtro-box">
                <input type="hidden" name="controller" value="venda">
                <input type="hidden" name="action" value="relatorio">
                
                <div class="input-group">
                    <label>De:</label>
                    <input type="date" name="data_inicio" value="<?= $_GET['data_inicio'] ?? '' ?>">
                </div>
                
                <div class="input-group">
                    <label>Até:</label>
                    <input type="date" name="data_fim" value="<?= $_GET['data_fim'] ?? '' ?>">
                </div>
                
                <button type="submit" class="cadastro-button" style="width: auto; margin: 0;">Filtrar Resultados</button>
                
                <?php if(!empty($_GET['data_inicio'])): ?>
                    <a href="admin_vendas.php" class="btn-secondary" style="border:none; margin:0;">Limpar</a>
                <?php endif; ?>
            </form>

            <div style="overflow-x: auto;">
                <table class="table-custom">
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
                        <?php if (!empty($vendas)): ?>
                            <?php foreach ($vendas as $v): ?>
                            <tr>
                                <td>#<?= $v['id']; ?></td>
                                <td style="font-weight:bold;"><?= htmlspecialchars($v['nome_completo']); ?></td>
                                <td><?= date('d/m/Y H:i', strtotime($v['data_pedido'])); ?></td>
                                <td><?= htmlspecialchars($v['status']); ?></td>
                                <td>R$ <?= number_format($v['valor_total'], 2, ',', '.'); ?></td>
                            </tr>
                            <?php endforeach; ?>
                        <?php else: ?>
                            <tr>
                                <td colspan="5" style="text-align:center; padding:30px; color:#999;">
                                    Nenhuma venda encontrada neste período.
                                </td>
                            </tr>
                        <?php endif; ?>
                    </tbody>
                </table>
            </div>

            <div class="total-destaque">
                <div class="total-label">Faturamento Total</div>
                <div class="total-valor">
                    R$ <?= number_format($totalPeriodo, 2, ',', '.'); ?>
                </div>
            </div>

        </div>
    </div>

</body>
</html>