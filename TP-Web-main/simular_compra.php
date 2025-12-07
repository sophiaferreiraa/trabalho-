<?php
// mudar esse arquivo pro codigo real das compras
require_once 'conexao.php';
require_once 'model/dao/VendaDAO.php';

session_start();

if (!isset($_SESSION['user_id'])) {
    header("Location: login.php");
    exit;
}

$check = $pdo->query("SELECT COUNT(*) FROM Products")->fetchColumn();
if ($check == 0) {
    die("<div style='text-align:center; padding:50px;'>ERRO: Não há produtos cadastrados. <br>Por favor, rode o script SQL novamente para inserir os produtos de teste.</div>");
}

$mensagem = '';
$sucesso = false;

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $vendaDAO = new VendaDAO($pdo);
    
    $itens_fake = [
        ['id' => 1, 'nome' => 'Shampoo Hidratante Wella', 'preco' => 89.90, 'quantidade' => 1],
        ['id' => 2, 'nome' => 'Máscara Nutritiva LOréal', 'preco' => 149.90, 'quantidade' => 2]
    ];
    $total_fake = 389.70;

    if ($vendaDAO->registrarVenda($_SESSION['user_id'], $total_fake, $itens_fake)) {
        $mensagem = "Compra realizada com sucesso! O histórico foi atualizado.";
        $sucesso = true;
    } else {
        $mensagem = "Erro ao registrar venda. Verifique o banco de dados.";
    }
}
?>

<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <title>Simular Compra - Teste</title>
    <link rel="stylesheet" href="stylesheet.css">
</head>
<body>
    <div class="content-wrapper">
        <h2 class="page-title">Simulador de Checkout</h2>
        
        <?php if($mensagem): ?>
            <div class="feedback-box <?= $sucesso ? 'success' : 'error' ?>">
                <?= $mensagem ?>
            </div>
            <?php if($sucesso): ?>
                <div style="text-align:center;">
                    <a href="ver_compras.php" class="cadastro-button" style="text-decoration:none;">Ver Histórico (Cliente)</a>
                    <br><br>
                    <a href="index.php" class="link-secondary">Voltar ao Início</a>
                </div>
            <?php endif; ?>
        <?php endif; ?>
        
        <?php if(!$sucesso): ?>
            <div style="background:white; padding:30px; border-radius:8px; max-width:500px; margin:0 auto; text-align:center; box-shadow:0 2px 5px rgba(0,0,0,0.1);">
                <h3>Resumo do Carrinho</h3>
                <ul style="list-style:none; padding:0; margin:20px 0; color:#555; text-align:left;">
                    <li style="padding:5px 0; border-bottom:1px solid #eee;">1x Shampoo Wella - R$ 89,90</li>
                    <li style="padding:5px 0; border-bottom:1px solid #eee;">2x Máscara L'Oréal - R$ 149,90</li>
                    <li style="padding:15px 0; font-weight:bold; font-size:1.2rem;">Total: R$ 389,70</li>
                </ul>
                <form method="POST">
                    <button type="submit" class="cadastro-button">Finalizar Compra</button>
                </form>
                <br>
                <a href="index.php" class="btn-secondary">Cancelar</a>
            </div>
        <?php endif; ?>
    </div>
</body>
</html>