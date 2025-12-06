<?php
// admin_vendas.php
require_once 'controller/VendaController.php';

$controller = new VendaController();
$controller->relatorioVendas();
?>