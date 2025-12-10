<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Novo Produto - Admin</title>
    <link rel="stylesheet" href="/e-commerce/public/stylesheet.css">
    <style>
        .btn-novo-menu {
            margin-left: 20px; 
            background-color: #fa8a99; 
            color: white !important; 
            padding: 8px 15px; 
            border-radius: 20px; 
            font-size: 0.9rem;
            text-transform: uppercase;
            font-weight: bold;
        }
    </style>
</head>
<body>
<header class="header-menu">
    <nav class="navbar">
        <div class="logo">
            <a href="/e-commerce/public/index.php">Painel Admin</a>
            <a href="/e-commerce/public/index.php?url=produtos" class="btn-novo-menu">PRODUTOS</a>
        </div>
    </nav>
</header>

<div class="content-wrapper">
    <h2 class="page-title">Cadastrar Produto</h2>
    
    <form class="cadastro-form" method="POST" action="/e-commerce/public/index.php?url=salvar_produto">
        <label>Nome do Produto:</label>
        <input type="text" name="nome" required>

        <label>Descrição Curta:</label>
        <input type="text" name="descricao" required>

        <label>Preço (R$):</label>
        <input type="number" step="0.01" name="preco" required>

        <label>Estoque (Qtd):</label>
        <input type="number" name="estoque" required>

        <label>Categoria:</label>
        <select name="categoria_id" required style="width: 100%; padding: 12px; margin-bottom: 20px; border: 1px solid #fce8e8; border-radius: 5px; background:white;">
            <option value="">Selecione...</option>
            <?php foreach ($categorias as $c): ?>
                <option value="<?= $c['id'] ?>"><?= htmlspecialchars($c['nome']) ?></option>
            <?php endforeach; ?>
        </select>

        <button type="submit" class="cadastro-button">Salvar Produto</button>
        <a href="/e-commerce/public/index.php?url=admin_produtos" class="btn-secondary" style="display:block; margin-top:10px;">Voltar</a>
    </form>
</div>
</body>
</html>