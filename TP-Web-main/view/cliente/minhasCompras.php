<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Minhas Compras</title>
    <link rel="stylesheet" href="stylesheet.css">
    
    <style>
        .card-tabela {
            background-color: white;
            padding: 30px;
            border-radius: 10px;    
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); 
            width: 100%;
            max-width: 900px;      
            margin: -30px auto 0;   
            position: relative;
            z-index: 10;
        }

        table { width: 100%; border-collapse: collapse; margin-top: 10px; }
        th { text-align: left; color: #264653; padding: 15px; border-bottom: 2px solid #ffe4e4; font-size: 1.1rem; }
        td { padding: 15px; border-bottom: 1px solid #eee; color: #333; }
        
        .status { 
            background: #ffe4e4; color: #fa8a99; 
            padding: 5px 10px; border-radius: 15px; font-weight: bold; font-size: 0.9rem;
        }
    </style>
</head>
<body>
    
    <header class="header-menu">
        <nav class="navbar">
            <div class="logo"><a href="index.php">Beleza Capilar</a></div>
            <ul class="nav-links">
                <li><a href="index.php">Início</a></li>
                <li><a href="logout.php">Sair</a></li>
            </ul>
        </nav>
    </header>

    <div class="content-wrapper">
        
        <section class="hero-section" style="padding-bottom: 60px;">
            <h1>Meus Pedidos</h1>
            <p>Histórico completo das suas compras.</p>
        </section>

        <div class="card-tabela">
            <?php if (empty($compras)): ?>
                <div style="text-align: center; padding: 40px;">
                    <h3 style="color: #999;">Nenhum pedido encontrado.</h3>
                    <br>
                    <a href="produtos.php" class="cadastro-button" style="text-decoration:none;">Ir às Compras</a>
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
                                <td style="font-weight:bold; color: #264653;">#<?= $compra['id']; ?></td>
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
                <a href="index.php" class="btn-secondary">Voltar</a>
            </div>
        </div>

    </div>
</body>
</html>