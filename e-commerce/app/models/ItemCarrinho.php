<?php
class ItemCarrinho {
    private $id;
    private $qtd;
    private $preco;

    public function __construct($id, $qtd, $preco) {
        $this->id = $id;
        $this->qtd = $qtd;
        $this->preco = $preco;
    }

    public function getId() {
        return $this->id;
    }

    public function getQtd() {
        return $this->qtd;
    }

    public function setQtd($qtd) {
        $this->qtd = $qtd;
    }

    public function getPreco() {
        return $this->preco;
    }

    public function getSubtotal() {
        return $this->preco * $this->qtd;
    }
}
