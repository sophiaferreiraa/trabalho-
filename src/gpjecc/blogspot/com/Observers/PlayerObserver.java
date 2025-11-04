package gpjecc.blogspot.com.Observers;

public interface PlayerObserver {
    void onPlayerDamaged(float newHealth);
    void onPlayerAttack();
    void onScoreChanged(int newScore);

    void onPlayerDied();  // Novo método para notificar morte
}
