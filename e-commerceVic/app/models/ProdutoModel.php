<?php

class ProdutoModel {
    private $id;
    private $nome;
    private $descricao;
    private $preco;
    private $estoque;
    private $categoriaId;
    private $nomeCategoria;

    public function getId() { return $this->id; }
    public function setId($id) { $this->id = $id; }

    public function getNome() { return $this->nome; }
    public function setNome($nome) { $this->nome = $nome; }

    public function getDescricao() { return $this->descricao; }
    public function setDescricao($descricao) { $this->descricao = $descricao; }

    public function getPreco() { return $this->preco; }
    public function setPreco($preco) { $this->preco = $preco; }

    public function getEstoque() { return $this->estoque; }
    public function setEstoque($estoque) { $this->estoque = $estoque; }

    public function getCategoriaId() { return $this->categoriaId; }
    public function setCategoriaId($categoriaId) { $this->categoriaId = $categoriaId; }

    public function getNomeCategoria() { return $this->nomeCategoria; }
    public function setNomeCategoria($nomeCategoria) { $this->nomeCategoria = $nomeCategoria; }
}