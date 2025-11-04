package gpjecc.blogspot.com.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.TextInputListener; // Importa o listener nativo
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle; 
import gpjecc.blogspot.com.TartarugasJogo;

public class TelaMenu implements Screen {

    private final TartarugasJogo jogo;
    private BitmapFont font;
    private SpriteBatch batch;
    
    // 3 "Botões" falsos
    private Rectangle btnCriarSoloBounds;
    private Rectangle btnCriarDuoBounds;
    private Rectangle btnEntrarBounds;

    public TelaMenu(TartarugasJogo jogo) {
        this.jogo = jogo;
        
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setScale(1.5f);
        
        // Define as áreas clicáveis
        btnCriarSoloBounds = new Rectangle(Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() / 2f + 60, 200, 40);
        btnCriarDuoBounds  = new Rectangle(Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() / 2f, 200, 40);
        btnEntrarBounds    = new Rectangle(Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() / 2f - 60, 200, 40);
    }

    private void checarCliques() {
        if (!Gdx.input.justTouched()) {
            return; // Sai se não houver clique
        }
        
        float touchX = Gdx.input.getX();
        float touchY = Gdx.graphics.getHeight() - Gdx.input.getY(); // Inverte Y

        // --- Botão 1: Criar Jogo (SOLO) ---
        if (btnCriarSoloBounds.contains(touchX, touchY)) {
            Gdx.app.log("TelaMenu", "Botão 'Criar Jogo (Solo)' clicado.");
            Gdx.input.getTextInput(new TextInputListener() {
                @Override
                public void input (String text) {
                    String nome = (text == null || text.trim().isEmpty()) ? "Host-Solo" : text;
                    jogo.startHostAndPlaySolo(nome);
                }
                @Override
                public void canceled () { }
            }, "Digite seu nome", "Host-Solo"); // 3 argumentos
        }
        
        // --- Botão 2: Criar Jogo (DUO) ---
        else if (btnCriarDuoBounds.contains(touchX, touchY)) {
            Gdx.app.log("TelaMenu", "Botão 'Criar Jogo (Duo)' clicado.");
            Gdx.input.getTextInput(new TextInputListener() {
                @Override
                public void input (String text) {
                    String nome = (text == null || text.trim().isEmpty()) ? "Host-Duo" : text;
                    jogo.startHostAndGoToLobby(nome);
                }
                @Override
                public void canceled () { }
            }, "Digite seu nome", "Host-Duo"); // 3 argumentos
        }
        
        // --- Botão 3: Entrar no Jogo (JOIN) ---
        else if (btnEntrarBounds.contains(touchX, touchY)) {
            Gdx.app.log("TelaMenu", "Botão 'Entrar no Jogo' clicado.");
            Gdx.input.getTextInput(new TextInputListener() {
                @Override
                public void input (final String nome) { 
                    String nomeFinal = (nome == null || nome.trim().isEmpty()) ? "Jogador" : nome;
                    // Pede o IP
                    Gdx.input.getTextInput(new TextInputListener() {
                        @Override
                        public void input (String ip) { 
                            String ipFinal = (ip == null || ip.trim().isEmpty()) ? "127.0.0.1" : ip;
                            jogo.joinGameAndGoToLobby(ipFinal, nomeFinal);
                        }
                        @Override
                        public void canceled () { }
                    }, "Digite o IP do Host", "127.0.0.1"); // 3 argumentos
                }
                @Override
                public void canceled () { }
            }, "Digite seu nome", "Jogador"); // 3 argumentos
        }
    }

    @Override
    public void render(float delta) {
        checarCliques();
        
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        batch.begin();
        font.draw(batch, "Tartarugas Jogo - Multiplayer", Gdx.graphics.getWidth() / 2f - 140, Gdx.graphics.getHeight() - 20);
        
        // Desenha os 3 "botões"
        font.draw(batch, "Criar Jogo (Solo)", btnCriarSoloBounds.x + 20, btnCriarSoloBounds.y + 30);
        font.draw(batch, "Criar Jogo (Duo)", btnCriarDuoBounds.x + 20, btnCriarDuoBounds.y + 30);
        font.draw(batch, "Entrar no Jogo", btnEntrarBounds.x + 25, btnEntrarBounds.y + 30);
        
        batch.end();
    }

    @Override public void show() { }
    @Override public void resize(int width, int height) { }
    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }
    
    @Override public void dispose() { 
        font.dispose();
        batch.dispose();
    }
}