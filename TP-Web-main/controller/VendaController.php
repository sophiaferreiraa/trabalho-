<?php
// controller/VendaController.php

require_once __DIR__ . '/../conexao.php';
// CORREÇÃO: Caminho apontando para model/dao/ (singular)
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

        // CORREÇÃO: Caminho apontando para view/cliente/ (singular)
        require __DIR__ . '/../view/cliente/minhasCompras.php';
    }

    public function relatorioVendas() {
        if (session_status() == PHP_SESSION_NONE) session_start();

        $dataInicio = $_GET['data_inicio'] ?? null;
        $dataFim = $_GET['data_fim'] ?? null;

        $vendas = $this->vendaDAO->listarTodasVendas($dataInicio, $dataFim);
        
        $totalPeriodo = 0;
        foreach($vendas as $v) { 
            $totalPeriodo += $v['valor_total']; 
        }

        // CORREÇÃO: Caminho apontando para view/admin/ (singular)
        require __DIR__ . '/../view/admin/vendasRealizadas.php';
    }
}
?>