<?php
// controllers/VendaController.php
require_once __DIR__ . '/../conexao.php';
require_once __DIR__ . '/../dao/VendaDAO.php';

class VendaController {
    private $vendaDAO;

    public function __construct() {
        global $pdo; // Pega a conexão do conexao.php
        $this->vendaDAO = new VendaDAO($pdo);
    }

    // Ação: Mostrar "Minhas Compras" (Cliente)
    public function minhasCompras() {
        if (session_status() == PHP_SESSION_NONE) session_start();

        // Verifica se está logado
        if (!isset($_SESSION['user_id'])) {
            header('Location: login.php');
            exit;
        }

        $userId = $_SESSION['user_id'];
        $compras = $this->vendaDAO->listarPorCliente($userId);

        // Carrega a View
        include __DIR__ . '/../views/cliente/minhasCompras.php';
    }

    // Ação: Mostrar Relatório de Vendas (Admin)
    public function relatorioVendas() {
        if (session_status() == PHP_SESSION_NONE) session_start();

        // Verifica se é Admin (ajuste a lógica conforme sua tabela Users)
        // No seu SQL tem o campo 'tipo_usuario'
        // if (!isset($_SESSION['tipo_usuario']) || $_SESSION['tipo_usuario'] !== 'admin') {
        //     echo "Acesso negado.";
        //     exit;
        // }

        $dataInicio = $_GET['data_inicio'] ?? null;
        $dataFim = $_GET['data_fim'] ?? null;

        $vendas = $this->vendaDAO->listarTodasVendas($dataInicio, $dataFim);
        $totalPeriodo = 0;
        foreach($vendas as $v) { $totalPeriodo += $v['valor_total']; }

        // Carrega a View
        include __DIR__ . '/../views/admin/vendasRealizadas.php';
    }
}
?>