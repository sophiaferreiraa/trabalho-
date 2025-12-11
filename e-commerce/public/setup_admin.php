<?php
require_once __DIR__ . '/../config/database.php';

try {
    $pdo = Database::getConnection();

    $nome = "Administrador";
    $email = "admin@gmail.com"; 
    $senha = "admin";         
    
    $hash = password_hash($senha, PASSWORD_DEFAULT);

    $sql = "INSERT INTO Users (nome_completo, email, senha, data_nascimento, tipo_usuario) 
            VALUES (?, ?, ?, '2000-01-01', 'admin') 
            ON DUPLICATE KEY UPDATE senha = ?, tipo_usuario = 'admin'";

    $stmt = $pdo->prepare($sql);
    $stmt->execute([$nome, $email, $hash, $hash]);

    echo "<div style='font-family:Arial; padding:20px; background:#d4edda; color:#155724; border:1px solid #c3e6cb; border-radius:5px;'>";
    echo "<h3>✅ Administrador Criado com Sucesso!</h3>";
    echo "<p><strong>Login:</strong> admin@gmail.com</p>";
    echo "<p><strong>Senha:</strong> admin</p>";
    echo "<br><a href='index.php?url=login'>Clique aqui para fazer Login</a>";
    echo "</div>";

} catch (Exception $e) {
    echo "Erro: " . $e->getMessage();
}