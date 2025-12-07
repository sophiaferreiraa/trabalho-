<?php

require_once __DIR__ . '/../conexao.php';
require_once __DIR__ . '/../model/dao/VendaDAO.php';

class VendaController {
    private $vendaDAO;

    public function __construct() {
        global $pdo; 
        $this->vendaDAO = new VendaDAO($pdo);
    }

    public function minhasCompras() {
        if (session_status() == PHP_SESSION_NONE) session_start();

        if (!isset($_SESSION['user_id'])) {
            header('Location: login.php');
            exit;
        }

        $userId = $_SESSION['user_id'];
        $compras = $this->vendaDAO->listarPorCliente($userId);

        require __DIR__ . '/../view/cliente/minhasCompras.php';
    }

    public function relatorioVendas() {
        if (session_status() == PHP_SESSION_NONE) session_start();

        if (!isset($_SESSION['tipo_usuario']) || $_SESSION['tipo_usuario'] !== 'admin') {
            header("Location: index.php");
            exit;
        }

        $dataInicio = $_GET['data_inicio'] ?? null;
        $dataFim = $_GET['data_fim'] ?? null;

        $vendas = $this->vendaDAO->listarTodasVendas($dataInicio, $dataFim);
        
        $totalPeriodo = 0;
        foreach($vendas as $v) { 
            $totalPeriodo += $v['valor_total']; 
        }

        require __DIR__ . '/../view/admin/vendasRealizadas.php';
    }
}
?>