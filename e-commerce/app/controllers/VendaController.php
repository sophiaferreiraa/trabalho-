<?php
require_once __DIR__ . '/../models/Venda.php';
require_once __DIR__ . '/../helpers/AuthMiddleware.php'; 

class VendaController {

    public function minhasCompras() {
        AuthMiddleware::requireLogin();

        if (!isset($_SESSION['user_id'])) {
            header("Location: /e-commerce/public/index.php?url=login");
            exit;
        }

        $userId = $_SESSION['user_id'];
        $vendaModel = new Venda();
        $compras = $vendaModel->listarPorCliente($userId);

        require __DIR__ . '/../views/cliente/minhas_compras.php';
    }

    public function relatorioAdmin() {
        AuthMiddleware::requireAdmin();

        $dataInicio = $_GET['data_inicio'] ?? null;
        $dataFim = $_GET['data_fim'] ?? null;

        $vendaModel = new Venda();
        $vendas = $vendaModel->listarTodasVendas($dataInicio, $dataFim);

        $totalPeriodo = 0;
        foreach ($vendas as $v) { 
            $totalPeriodo += $v['valor_total']; 
        }

        require __DIR__ . '/../views/admin/relatorio_vendas.php';
    }

    public function simularCompra() {
        AuthMiddleware::requireLogin();

        require __DIR__ . '/../views/cliente/simular_compra.php';
    }

    public function finalizarSimulacao() {
        AuthMiddleware::requireLogin();

        if (!isset($_SESSION['user_id'])) {
            header("Location: /e-commerce/public/index.php?url=login");
            exit;
        }

        if ($_SERVER['REQUEST_METHOD'] === 'POST') {
            $vendaModel = new Venda();

            // Dados de teste
            $itens_fake = [
                ['id' => 1, 'nome' => 'Shampoo Hidratante Wella', 'preco' => 89.90, 'quantidade' => 1],
                ['id' => 2, 'nome' => 'Máscara Nutritiva LOréal', 'preco' => 149.90, 'quantidade' => 2]
            ];

            $total_fake = 389.70;

            if ($vendaModel->registrarVenda($_SESSION['user_id'], $total_fake, $itens_fake)) {
                header("Location: /e-commerce/public/index.php?url=minhas_compras");
                exit;
            } else {
                echo "Erro ao registrar venda.";
            }
        } else {
            echo "Método inválido.";
        }
    }
}
