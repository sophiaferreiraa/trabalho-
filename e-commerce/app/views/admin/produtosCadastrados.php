<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Gerenciar Produtos - Admin</title>
    <link rel="stylesheet" href="/e-commerce/public/stylesheet.css">
    <style>
        .card-tabela {
            background-color: white; 
            padding: 30px; 
            border-radius: 10px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1); 
            width: 100%; 
            max-width: 1000px; 
            margin: 0 auto;
        }
        .filtro-form {
            display: flex; 
            gap: 15px; 
            align-items: flex-end; 
            flex-wrap: wrap;
            background: #fff0f3; 
            padding: 20px; 
            border-radius: 8px; 
            margin-bottom: 25px;
        }
        .input-group { display: flex; flex-direction: column; }
        table { width: 100%; border-collapse: collapse; }
        th { background-color: #264653; color: white; padding: 15px; text-align: left; }
        td { padding: 15px; border-bottom: 1px solid #eee; }
        .btn-delete { color: red; font-weight: bold; font-size: 0.9rem; cursor: pointer; }
    </style>
</head>
<body>
<header class="header-menu">
    <nav class="navbar">
        <div class="logo"><a href="/e-commerce/public/index.php">Painel Admin</a></div>
        <ul class="nav-links">
            <li><a href="/e-commerce/public/index.php?url=novo_produto" style="color:#fa8a99; font-weight:bold;">+ Novo Produto</a></li>
            <li><a href="/e-commerce/public/index.php">Voltar ao Site</a></li>
            <li><a href="/e-commerce/public/index.php?url=logout">Sair</a></li>
        </ul>
    </nav>
</header>

<div class="content-wrapper">
    <h2 class="page-title">Produtos Cadastrados</h2>

    <div class="card-tabela">
        <div style="overflow-x: auto;">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Nome</th>
                        <th>Categoria</th>
                        <th>Preço</th>
                        <th>Ações</th>
                    </tr>
                </thead>
                <tbody>
                    <?php if (!empty($produtos)): ?>
                        <?php foreach ($produtos as $p): ?>
                        <tr>
                            <td><?= $p->getId() ?></td>
                            <td><?= htmlspecialchars($p->getNome()) ?></td>
                            <td><?= htmlspecialchars($p->getNomeCategoria()) ?></td>
                            <td>R$ <?= number_format($p->getPreco(), 2, ',', '.') ?></td>
                            <td>
                                <a href="/e-commerce/public/index.php?url=excluir_produto&id=<?= $p->getId() ?>" 
                                   onclick="return confirm('Tem certeza que deseja excluir?')" class="btn-delete">Excluir</a>
                            </td>
                        </tr>
                        <?php endforeach; ?>
                    <?php else: ?>
                        <tr><td colspan="5" style="text-align:center;">Nenhum produto cadastrado.</td></tr>
                    <?php endif; ?>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>
