<?php
class Database {
    private static $instance = null;

    public static function getConnection() {
        if (!self::$instance) {
            $host = 'localhost';
            $dbname = 'loja_cabelo';
            $user = 'root';
            $pass = '';

            try {
                self::$instance = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8", $user, $pass);
                self::$instance->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
            } catch (PDOException $e) {
                die("Erro na conexão: " . $e->getMessage());
            }
        }
        return self::$instance;
    }
}
