package gpjecc.blogspot.com.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle; 
import gpjecc.blogspot.com.TartarugasJogo;

public class TelaMenu implements Screen {

    private final TartarugasJogo jogo;
    private BitmapFont font;
    private SpriteBatch batch; // Não cria um novo
    
    // 3 "Botões" falsos
    private Rectangle btnCriarSoloBounds;
    private Rectangle btnCriarDuoBounds;
    private Rectangle btnEntrarBounds;

    public TelaMenu(TartarugasJogo jogo) {
        this.jogo = jogo;
        
        // --- CORREÇÃO DO CRASH ---
        // Pega o batch principal em vez de criar um novo
        this.batch = jogo.getBatch(); 
        // -------------------------
        
        font = new BitmapFont();
        font.setScale(1.5f);
        
        btnCriarSoloBounds = new Rectangle(Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() / 2f + 60, 200, 40);
        btnCriarDuoBounds  = new Rectangle(Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() / 2f, 200, 40);
        btnEntrarBounds    = new Rectangle(Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() / 2f - 60, 200, 40);
    }

    private void checarCliques() {
        if (!Gdx.input.justTouched()) {
            return; 
        }
        
        float touchX = Gdx.input.getX();
        float touchY = Gdx.graphics.getHeight() - Gdx.input.getY(); // Inverte Y

        // --- Botão 1: Criar Jogo (SOLO) ---
        if (btnCriarSoloBounds.contains(touchX, touchY)) {
            Gdx.app.log("TelaMenu", "Botão 'Jogar Sozinho' clicado.");
            Gdx.input.getTextInput(new TextInputListener() {
                @Override
                public void input (String text) {
                    String nome = (text == null || text.trim().isEmpty()) ? "Jogador Solo" : text;
                    jogo.startHostAndPlaySolo(nome);
                }
                @Override
                public void canceled () { }
            }, "Digite seu nome", "Jogador Solo"); 
        }
        
        // --- Botão 2: Criar Jogo (DUPLA) ---
        else if (btnCriarDuoBounds.contains(touchX, touchY)) {
            Gdx.app.log("TelaMenu", "Botão 'Criar Jogo em Dupla' clicado.");
            Gdx.input.getTextInput(new TextInputListener() {
                @Override
                public void input (String text) {
                    String nome = (text == null || text.trim().isEmpty()) ? "Host" : text;
                    jogo.startHostAndGoToLobby(nome);
                }
                @Override
                public void canceled () { }
            }, "Digite seu nome de Host", "Host"); 
        }
        
        // --- Botão 3: Entrar no Jogo (JOIN) ---
        else if (btnEntrarBounds.contains(touchX, touchY)) {
            Gdx.app.log("TelaMenu", "Botão 'Entrar no Jogo' clicado.");
            Gdx.input.getTextInput(new TextInputListener() {
                @Override
                public void input (final String nome) { 
                    String nomeFinal = (nome == null || nome.trim().isEmpty()) ? "Jogador 2" : nome;
                    Gdx.input.getTextInput(new TextInputListener() {
                        @Override
                        public void input (String ip) { 
                            String ipFinal = (ip == null || ip.trim().isEmpty()) ? "127.0.0.1" : ip;
                            jogo.joinGameAndGoToLobby(ipFinal, nomeFinal);
                        }
                        @Override
                        public void canceled () { }
                    }, "Digite o IP da Sala (do Host)", "127.0.0.1"); 
                }
                @Override
                public void canceled () { }
            }, "Digite seu nome", "Jogador 2"); 
        }
    }

    @Override
    public void render(float delta) {
        checarCliques();
        
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        batch.begin();
        font.draw(batch, "Tartarugas Jogo - Multiplayer", Gdx.graphics.getWidth() / 2f - 140, Gdx.graphics.getHeight() - 20);
        font.draw(batch, "Jogar Sozinho", btnCriarSoloBounds.x + 30, btnCriarSoloBounds.y + 30);
        font.draw(batch, "Criar Jogo em Dupla", btnCriarDuoBounds.x + 5, btnCriarDuoBounds.y + 30);
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
        // Não damos dispose no batch, pois ele pertence ao TartarugasJogo
    }
}