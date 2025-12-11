<?php

class ProdutoModel {
    private $id;
    private $nome;
    private $descricao;
    private $preco;
    private $estoque;

    private $brandId;
    private $brandName;

    private $hairTypeId;
    private $hairType;

    private $curlPatternId;
    private $curlPattern;

    private $productTypeId;
    private $productType;

    private $treatmentTypeId;
    private $treatmentType;

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


    public function getBrandId() { return $this->brandId; }
    public function setBrandId($brandId) { $this->brandId = $brandId; }

    public function getBrandName() { return $this->brandName; }
    public function setBrandName($brandName) { $this->brandName = $brandName; }


    public function getHairTypeId() { return $this->hairTypeId; }
    public function setHairTypeId($id) { $this->hairTypeId = $id; }

    public function getHairType() { return $this->hairType; }
    public function setHairType($v) { $this->hairType = $v; }


    public function getCurlPatternId() { return $this->curlPatternId; }
    public function setCurlPatternId($id) { $this->curlPatternId = $id; }

    public function getCurlPattern() { return $this->curlPattern; }
    public function setCurlPattern($v) { $this->curlPattern = $v; }


    public function getProductTypeId() { return $this->productTypeId; }
    public function setProductTypeId($id) { $this->productTypeId = $id; }

    public function getProductType() { return $this->productType; }
    public function setProductType($v) { $this->productType = $v; }


    public function getTreatmentTypeId() { return $this->treatmentTypeId; }
    public function setTreatmentTypeId($id) { $this->treatmentTypeId = $id; }

    public function getTreatmentType() { return $this->treatmentType; }
    public function setTreatmentType($v) { $this->treatmentType = $v; }
}
