<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Gerenciar Produtos - Admin</title>
  <link rel="stylesheet" href="/TP-Web/TP-Web-2/e-commerce/public/stylesheet.css">
    <style>
        table { width: 100%; border-collapse: collapse; background: white; }
        th, td { padding: 15px; border-bottom: 1px solid #eee; text-align: left; }
        th { background-color: #264653; color: white; }
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
        </ul>
    </nav>
</header>

<div class="content-wrapper">
    <h2 class="page-title">Produtos Cadastrados</h2>
    
    <div style="overflow-x: auto; box-shadow: 0 2px 5px rgba(0,0,0,0.1); border-radius: 8px;">
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
            </tbody>
        </table>
    </div>
</div>
</body>
</html>