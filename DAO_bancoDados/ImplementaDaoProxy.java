package DAO_bancoDados;

import java.util.List;

/**
 * Este Proxy combina os padrões VIRTUAL e PROTECTION.
 *
 * 1. Virtual (como VirtualImageProxy): 
 * - Não cria o 'ImplementaDao' (objeto real) no construtor.
 * - Ele só cria o 'ImplementaDao' na primeira vez que um método
 * (como salva, buscarPorID, etc.) é chamado.
 *
 * 2. Protection (como ProtectionProxyDocument):
 * - Recebe uma 'permissão' (String "role") no construtor.
 * - Antes de executar métodos "perigosos" (salva, atualizar, deletar),
 * ele verifica se o usuário tem a permissão "ADMIN".
 */
public class ImplementaDaoProxy implements InterfaceDao {

    // --- Lógica do VIRTUAL Proxy ---
    // Começa NULO, assim como 'realImage' no seu exemplo.
    // Só será criado "quando necessário" (lazy loading).
    private InterfaceDao realDao; 
    // --------------------------------

    // --- Lógica do PROTECTION Proxy ---
    // Armazena a permissão do usuário, assim como o 'User' no seu exemplo.
    private String acessador;
    // --------------------------------

    /**
     * Construtor do Proxy.
     * Note que ele NÃO CRIA o ImplementaDao, apenas salva a permissão.
     * @param acessador A permissão (ex: "ADMIN" ou "GUEST")
     */
    public ImplementaDaoProxy(String acessador) {
        this.acessador = acessador;
        this.realDao = null; // VIRTUAL: Começa nulo
        System.out.println(">>> Proxy criado para usuário '" + acessador + "'. DAO Real ainda NULO.");
    }

    /**
     * MÉTODO-CHAVE (Virtual Proxy)
     * Este método "acorda" o DAO real se ele estiver "dormindo" (nulo).
     * É o equivalente ao 'if (realImage == null)' no seu exemplo.
     */
    private InterfaceDao getRealDao() throws DAOException {
        // VIRTUAL: Se o DAO real ainda não foi instanciado...
        if (this.realDao == null) {
            System.out.println(">>> VIRTUAL PROXY: Carregando o DAO real AGORA (lazy loading)...");
            // Correto: Pega a instância única do Singleton
            this.realDao = ImplementaDao.getInstance();
        }
        // Se já foi, apenas o retorna
        return this.realDao;
    }

    /**
     * MÉTODO-CHAVE (Protection Proxy)
     * Este método verifica se o usuário tem permissão para operações "perigosas".
     * É o equivalente ao 'if (u.getRole()...)' no seu exemplo.
     */
    private void checkAdminAccess() throws DAOException {
        // PROTECTION: Verifica a permissão
        if (!"ADMIN".equals(this.acessador)) {
            // Se não for "ADMIN", bloqueia a operação
            System.err.println(">>> PROTECTION PROXY: Acesso negado! Usuário '" + this.acessador + "' não pode executar esta ação.");
            // Lança uma exceção para BLOQUEAR a chamada
            throw new DAOException("Permissão negada. Apenas ADMINS podem executar esta ação.", null);
        }
        // Se for ADM, não faz nada e deixa a execução continuar
        System.out.println(">>> PROTECTION PROXY: Acesso de ADM verificado.");
    }

    // --- Implementação dos Métodos da Interface ---
    // Agora, os métodos implementam a lógica do proxy

    @Override
    public void salva(Contato contato) throws DAOException {
        // 1. Checar permissão (Protection)
        checkAdminAccess();
        // 2. Acordar o DAO real (Virtual) e delegar a chamada
        // getRealDao() garante que o realDao seja criado se for nulo
        getRealDao().salva(contato);
    }

    @Override
    public void atualizar(Contato contato) throws DAOException {
        // 1. Checar permissão (Protection)
        checkAdminAccess();
        // 2. Acordar o DAO real (Virtual) e delegar a chamada
        getRealDao().atualizar(contato);
    }

    @Override
    public void deletar(int id) throws DAOException {
        // 1. Checar permissão (Protection)
        checkAdminAccess();
        // 2. Acordar o DAO real (Virtual) e delegar a chamada
        getRealDao().deletar(id);
    }

    // --- Métodos de Leitura (Permitidos para todos) ---
    // Estes métodos são como o 'view()' do seu exemplo de Documento.
    // Todos podem executar, então NÃO chamamos checkAdminAccess().

    @Override
    public Contato buscarPorID(int id) throws DAOException {
        // Apenas "acordamos" o DAO (Virtual) e delegamos a chamada.
        System.out.println(">>> PROXY: (Leitura permitida) Chamando buscarPorID...");
        // getRealDao() garante que o realDao seja criado se for nulo
        return getRealDao().buscarPorID(id);
    }

    @Override
    public List<Contato> buscarTodos() throws DAOException {
        // Igual ao buscarPorID, todos podem listar.
        System.out.println(">>> PROXY: (Leitura permitida) Chamando buscarTodos...");
        // getRealDao() garante que o realDao seja criado se for nulo
        return getRealDao().buscarTodos();
    }
}