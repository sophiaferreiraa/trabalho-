<?php
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}
?>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Produtos - Beleza Capilar</title>
    <link rel="stylesheet" href="/e-commerce/public/stylesheet.css">
</head>

<style>
.accordion-container {
    width: 100%;
    max-width: 300px;
    background: #fff;
    padding: 20px;
    border-radius: 12px;
    box-shadow: var(--sombra-leve);
    margin-bottom: 40px;
}
.accordion-item { border-bottom: 1px solid #f3c2cb; padding: 10px 0; }
.accordion-header {
    font-weight: bold;
    font-size: 1rem;
    color: var(--cor-secundaria);
    cursor: pointer;
    display: flex;
    justify-content: space-between;
    padding: 10px 5px;
    border-radius: 6px;
    transition: background 0.3s;
}
.accordion-header:hover { background-color: rgba(250, 138, 153, 0.15); }
.accordion-content { display: none; padding: 10px 10px 5px 15px; animation: fadeDown 0.3s ease; }
.filter-option { margin-bottom: 10px; }
.filter-option a {
    color: var(--cor-secundaria);
    background: #ffe9ee;
    padding: 8px 10px;
    display: block;
    border-radius: 6px;
    font-size: 0.9rem;
    transition: all 0.2s;
    border: 1px solid transparent;
}
.filter-option a:hover {
    background: var(--cor-principal);
    color: white !important;
    border-color: var(--cor-secundaria);
}
@keyframes fadeDown {
    from { opacity: 0; transform: translateY(-4px); }
    to { opacity: 1; transform: translateY(0); }
}
.content-wrapper {
    display: flex;
    justify-content: center;
    gap: 40px;
    width: 100%;
}
.product-grid { flex-grow: 1; }
</style>

<body>

<header class="header-menu">
    <nav class="navbar">
        <div class="logo">
            <a href="/e-commerce/public/index.php">Beleza Capilar</a>
        </div>
        <div class="menu-toggle" id="menu-toggle">&#9776;</div>

        <ul class="nav-links" id="nav-links">
            <?php if (isset($_SESSION['user_id'])): ?>
                <li><a href="#" style="color: #264653;">Minha Conta</a></li>
                <li><a href="/e-commerce/public/index.php?url=logout">Sair</a></li>
            <?php else: ?>
                <li><a href="/e-commerce/public/index.php?url=cadastro">Cadastre-se</a></li>
                <li><a href="/e-commerce/public/index.php?url=login">Login</a></li>
            <?php endif; ?>
            <li><a href="/e-commerce/public/index.php?url=produtos">Produtos</a></li>
        </ul>
    </nav>
</header>

<div class="content-wrapper">
    <h2 class="page-title">Nossos Produtos</h2>

    <div class="accordion-container">
        <div class="accordion-item filtro-wrapper">
            <div class="accordion-header filtro-principal">
                Filtro <span>+</span>
            </div>

            <div class="accordion-content filtro-content">

                <?php $filtrosAtuais = $_GET; ?>

                <div class="accordion-item">
                    <div class="accordion-header">Categorias <span>+</span></div>
                    <div class="accordion-content">
                        <div class="filter-option">
                            <a href="/e-commerce/public/index.php?url=produtos" style="color:#264653;">Todos</a>
                        </div>
                        <?php foreach ($categorias as $cat):
                            $filtrosAtuais['categoria'] = $cat['id'];
                            $link = '/e-commerce/public/index.php?url=produtos&' . http_build_query($filtrosAtuais);
                        ?>
                        <div class="filter-option">
                            <a href="<?= $link ?>"><?= htmlspecialchars($cat['nome']) ?></a>
                        </div>
                        <?php endforeach; ?>
                    </div>
                </div>

                <div class="accordion-item">
                    <div class="accordion-header">Tipos de Cabelo <span>+</span></div>
                    <div class="accordion-content">
                        <?php foreach ($hairTypes as $t):
                            $filtrosAtuais['hair_type'] = $t['id'];
                            $link = '/e-commerce/public/index.php?url=produtos&' . http_build_query($filtrosAtuais);
                        ?>
                        <div class="filter-option">
                            <a href="<?= $link ?>"><?= htmlspecialchars($t['tipo']) ?></a>
                        </div>
                        <?php endforeach; ?>
                    </div>
                </div>

                <div class="accordion-item">
                    <div class="accordion-header">Curvatura <span>+</span></div>
                    <div class="accordion-content">
                        <?php foreach ($curlPatterns as $c):
                            $filtrosAtuais['curvatura'] = $c['id'];
                            $link = '/e-commerce/public/index.php?url=produtos&' . http_build_query($filtrosAtuais);
                        ?>
                        <div class="filter-option">
                            <a href="<?= $link ?>"><?= htmlspecialchars($c['curvatura']) ?></a>
                        </div>
                        <?php endforeach; ?>
                    </div>
                </div>

                <div class="accordion-item">
                    <div class="accordion-header">Tipo de Produto <span>+</span></div>
                    <div class="accordion-content">
                        <?php foreach ($productTypes as $pt):
                            $filtrosAtuais['type'] = $pt['id'];
                            $link = '/e-commerce/public/index.php?url=produtos&' . http_build_query($filtrosAtuais);
                        ?>
                        <div class="filter-option">
                            <a href="<?= $link ?>"><?= htmlspecialchars($pt['nome']) ?></a>
                        </div>
                        <?php endforeach; ?>
                    </div>
                </div>

                <div class="accordion-item">
                    <div class="accordion-header">Tratamentos <span>+</span></div>
                    <div class="accordion-content">
                        <?php foreach ($treatments as $tr):
                            $filtrosAtuais['treatment'] = $tr['id'];
                            $link = '/e-commerce/public/index.php?url=produtos&' . http_build_query($filtrosAtuais);
                        ?>
                        <div class="filter-option">
                            <a href="<?= $link ?>"><?= htmlspecialchars($tr['tratamento']) ?></a>
                        </div>
                        <?php endforeach; ?>
                    </div>
                </div>

                <div class="accordion-item">
                    <div class="accordion-header">Marcas <span>+</span></div>
                    <div class="accordion-content">
                        <?php foreach ($brands as $b):
                            $filtrosAtuais['brand'] = $b['id'];
                            $link = '/e-commerce/public/index.php?url=produtos&' . http_build_query($filtrosAtuais);
                        ?>
                        <div class="filter-option">
                            <a href="<?= $link ?>"><?= htmlspecialchars($b['nome']) ?></a>
                        </div>
                        <?php endforeach; ?>
                    </div>
                </div>

                <div class="accordion-item">
                    <div class="accordion-header">Preço <span>+</span></div>
                    <div class="accordion-content">
                        <?php
                        $precos = [
                            1 => 'Até R$30',
                            2 => 'R$30 a R$70',
                            3 => 'R$70 a R$150',
                            4 => 'Acima de R$150'
                        ];
                        foreach ($precos as $key => $valor):
                            $filtrosAtuais['preco'] = $key;
                            $link = '/e-commerce/public/index.php?url=produtos&' . http_build_query($filtrosAtuais);
                        ?>
                        <div class="filter-option">
                            <a href="<?= $link ?>"><?= $valor ?></a>
                        </div>
                        <?php endforeach; ?>
                    </div>
                </div>

            </div>
        </div>
    </div>

<script>
document.addEventListener("DOMContentLoaded", function () {
    const headers = document.querySelectorAll(".accordion-header");
    headers.forEach(header => {
        header.addEventListener("click", (event) => {
            event.stopPropagation();
            const content = header.nextElementSibling;
            if (!content) return;
            const isVisible = content.style.display === "block";
            content.style.display = isVisible ? "none" : "block";
            header.querySelector("span").textContent = isVisible ? "+" : "-";
        });
    });
});
</script>

<div class="product-grid">
    <?php if (!empty($produtos)): ?>
        <?php foreach ($produtos as $p): ?>
        <div class="product-card">
            <div style="background:#eee; height:150px; margin-bottom:15px; display:flex; align-items:center; justify-content:center; color:#ccc;">Imagem</div>

            <h3><?= htmlspecialchars($p->getNome()) ?></h3>
            <p style="color: #666; font-size: 0.9rem; min-height: 40px;"><?= htmlspecialchars($p->getDescricao()) ?></p>

            <p style="color: #fa8a99; font-weight: bold; margin: 10px 0; font-size:0.85rem;">
                <?= htmlspecialchars($p->getProductType()) ?>
            </p>

            <div class="price">R$ <?= number_format($p->getPreco(), 2, ',', '.') ?></div>
          <a href="/e-commerce/public/index.php?url=addCarrinho&id=<?= $p->getId() ?>" 
   class="btn-pink">Adicionar ao carrinho</a>
        </div>
        <?php endforeach; ?>
    <?php else: ?>
        <p style="text-align: center; width: 100%;">Nenhum produto encontrado nesta categoria.</p>
    <?php endif; ?>
</div>

</body>
</html>
