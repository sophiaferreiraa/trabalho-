<?php
require_once __DIR__ . '/../../config/database.php';

class User {
    private $pdo;

    public function __construct() {
        $this->pdo = Database::getConnection();
    }

   public function create($nome, $email, $senha, $nascimento, $tipo = 'usuario')
{
    try {
        $stmt = $this->pdo->prepare("SELECT email FROM Users WHERE email = ?");
        $stmt->execute([$email]);

        if ($stmt->rowCount() > 0) {
            return "duplicate";
        }
        $stmt = $this->pdo->prepare("
            INSERT INTO Users (
                nome_completo, data_nascimento, email, senha, tipo_usuario
            ) VALUES (?, ?, ?, ?, ?)
        ");
        return $stmt->execute([$nome, $nascimento, $email, $senha, $tipo]);
    } catch (PDOException $e) {
        return false;
    }
}


    public function findByEmail($email) {
        $stmt = $this->pdo->prepare("SELECT * FROM Users WHERE email = ?");
        $stmt->execute([$email]);
        return $stmt->fetch(PDO::FETCH_ASSOC);
    }
}
