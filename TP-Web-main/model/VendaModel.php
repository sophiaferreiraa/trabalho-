<?php

class VendaModel {
    private $id;
    private $userId;
    private $dataPedido;
    private $valorTotal;
    private $status;
    private $itens = [];

    // Getters e Setters
    public function getId() { return $this->id; }
    public function setId($id) { $this->id = $id; }

    public function getUserId() { return $this->userId; }
    public function setUserId($userId) { $this->userId = $userId; }

    public function getDataPedido() { return $this->dataPedido; }
    public function setDataPedido($dataPedido) { $this->dataPedido = $dataPedido; }

    public function getValorTotal() { return $this->valorTotal; }
    public function setValorTotal($valorTotal) { $this->valorTotal = $valorTotal; }

    public function getStatus() { return $this->status; }
    public function setStatus($status) { $this->status = $status; }

    public function getItens() { return $this->itens; }
    public function setItens($itens) { $this->itens = $itens; }
}
?>