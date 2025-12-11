<h2>Produtos Disponíveis</h2>

<div class="produtos">
<?php foreach ($produtos as $p): ?>
    <div class="produto">
        <h3><?= $p['nome'] ?></h3>
        <p>Preço: R$ <?= number_format($p['preco'],2,',','.') ?></p>

        <form method="POST" action="/public/index.php?route=adicionarCarrinho">
            <input type="hidden" name="id_produto" value="<?= $p['id'] ?>">
            <input type="number" name="qtd" value="1" min="1">
            <button type="submit">Adicionar ao Carrinho</button>
        </form>
    </div>
<?php endforeach; ?>
</div>
