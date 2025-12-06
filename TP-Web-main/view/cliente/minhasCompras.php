<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Minhas Compras - Beleza Capilar</title>
    <link rel="stylesheet" href="../../stylesheet.css">
    <style>
        /* Estilo do Cartão Branco (Igual ao da Home) */
        .card-conteudo {
            background-color: white;
            padding: 40px;
            border-radius: 12px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
            width: 100%;
            max-width: 900px;
            margin: 0 auto; /* Centraliza */
        }

        /* Tabela Limpa */
        .table-custom {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }
        
        .table-custom th {
            text-align: left;
            padding: 15px;
            color: var(--cor-secundaria);
            border-bottom: 2px solid #f0f0f0;
            font-size: 0.95rem;
        }

        .table-custom td {
            padding: 15px;
            border-bottom: 1px solid #f9f9f9;
            color: #555;
            vertical-align: middle;
        }

        /* Status Colorido */
        .status-badge {
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: bold;
            text-transform: uppercase;
            background-color: #e3f2fd;
            color: #1565c0;
        }
        
        /* Botãozinho de Detalhes */
        .btn-detalhes {
            padding: 8px 15px;
            background-color: var(--cor-principal);
            color: white;
            border-radius: 6px;
            font-size: 0.85rem;
            font-weight: bold;
            transition: 0.3s;
        }
        .btn-detalhes:hover {
            background-color: var(--cor-acento);
            color: white;
        }
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
        <h2 class="page-title">Meus Pedidos</h2>
        
        <div class="card-conteudo">
            <?php if (empty($compras)): ?>
                <div style="text-align: center; padding: 30px;">
                    <p style="font-size: 1.1rem; color: #777; margin-bottom: 20px;">Você ainda não fez nenhuma comprinha.</p>
                    <a href="../../produtos.php" class="cadastro-button" style="text-decoration:none;">Ir para a Loja</a>
                </div>
            <?php else: ?>
                <div style="overflow-x: auto;">
                    <table class="table-custom">
                        <thead>
                            <tr>
                                <th>Pedido</th>
                                <th>Data</th>
                                <th>Status</th>
                                <th>Total</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <?php foreach ($compras as $compra): ?>
                            <tr>
                                <td style="font-weight:bold; color: var(--cor-secundaria);">#<?= $compra['id']; ?></td>
                                <td><?= date('d/m/Y', strtotime($compra['data_pedido'])); ?></td>
                                <td>
                                    <span class="status-badge">
                                        <?= htmlspecialchars($compra['status']); ?>
                                    </span>
                                </td>
                                <td style="color: var(--cor-principal); font-weight: bold; font-size: 1.1rem;">
                                    R$ <?= number_format($compra['valor_total'], 2, ',', '.'); ?>
                                </td>
                                <td style="text-align: right;">
                                    <a href="#" class="btn-detalhes">Detalhes</a>
                                </td>
                            </tr>
                            <?php endforeach; ?>
                        </tbody>
                    </table>
                </div>
            <?php endif; ?>
        </div>
        
        <div style="margin-top: 30px;">
            <a href="../../index.php" class="btn-secondary">← Voltar</a>
        </div>
    </div>
</body>
</html>