<?php
// ver_compras.php (Na raiz)
require_once 'controllers/VendaController.php';

$controller = new VendaController();
$controller->minhasCompras();
?>