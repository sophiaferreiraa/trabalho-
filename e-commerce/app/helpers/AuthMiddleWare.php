<?php
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}
class AuthMiddleware {

    public static function requireLogin() {
        if (session_status() === PHP_SESSION_NONE) {
            session_start();
        }

        if (!isset($_SESSION['user_id'])) {
            header("Location: /e-commerce/public/index.php?url=login");
            exit;
        }
    }

    public static function requireAdmin() {
        if (session_status() === PHP_SESSION_NONE) {
            session_start();
        }

        if (!isset($_SESSION['user_id']) || ($_SESSION['tipo_usuario'] ?? '') !== 'admin') {
            header("Location: /e-commerce/public/index.php?url=login");
            exit;
        }
    }
}
