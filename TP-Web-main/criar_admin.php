<?php
require_once 'conexao.php';

$nome = "Administrador";
$email = "admin@gmail.com";
$senha = "admin"; 
$hash = password_hash($senha, PASSWORD_DEFAULT);

try {
    $sql = "INSERT INTO Users (nome_completo, email, senha, tipo_usuario) 
            VALUES (?, ?, ?, 'admin') 
            ON DUPLICATE KEY UPDATE senha = ?, tipo_usuario = 'admin'";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([$nome, $email, $hash, $hash]);

    echo "<h1>Sucesso!</h1>Admin criado.<br>Login: admin@gmail.com<br>Senha: admin";
} catch (PDOException $e) {
    echo "Erro: " . $e->getMessage();
}
?>