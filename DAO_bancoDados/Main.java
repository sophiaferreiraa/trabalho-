package DAO_bancoDados;

import java.util.List;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    
    // --- ESTA É A MUDANÇA PRINCIPAL ---
    // Nós criamos os DOIS proxies (ADMIN e GUEST) aqui
    private static InterfaceDao daoAdmin = new ImplementaDaoProxy("ADMIN");
    private static InterfaceDao daoGuest = new ImplementaDaoProxy("GUEST");
    // -------------------------------------
    
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        
        while (true) { 
            // 1. Chama o menu de login CORRIGIDO
            InterfaceDao daoAtual = loginMenu();
            
            // 2. Se o menu retornar null (usuário escolheu "3"), o loop quebra
            if (daoAtual == null) {
                break; 
            }
            
            // 3. O 'daoAtual' agora é ou o daoAdmin ou o daoGuest
            operacoesMenu(daoAtual);
        }
        
        System.out.println("Programa finalizado.");
        sc.close(); 
    }

    /**
     * Menu para "logar" como ADMIN ou GUEST.
     * Retorna o PROXY correto.
     */
    private static InterfaceDao loginMenu() {
        
        while (true) { 
            System.out.println("\n==================================");
            System.out.println("  SELECIONE O NÍVEL DE ACESSO");
            System.out.println("==================================");
            System.out.println("1. ADMIN (Permissão Total)");
            System.out.println("2. GUEST (Apenas Leitura)");
            System.out.println("3. Sair do Programa");
            System.out.print("Escolha (digite o número ou o nome): ");
            
            String escolha = sc.nextLine().trim().toUpperCase(); 
            
            switch (escolha) {
                case "1":
                case "ADMIN":
                    System.out.println(">>> Logado como ADMIN <<<");
                    return daoAdmin; // Retorna o PROXY de Admin
                
                case "2":
                case "GUEST":
                    System.out.println(">>> Logado como GUEST <<<");
                    return daoGuest; // Retorna o PROXY de Guest
                
                case "3":
                case "SAIR":
                    return null; // Retorna null (para Sair)
                
                default:
                    System.out.println("!!! Opção inválida. Tente novamente. !!!");
            }
        }
    }

    /**
     * Menu principal de operações CRUD.
     * Recebe o PROXY (daoAdmin ou daoGuest) como parâmetro.
     */
    private static void operacoesMenu(InterfaceDao dao) {
        while (true) {
            System.out.println("\n--- MENU DE OPERAÇÕES ---");
            System.out.println("1. Salvar Novo Contato");
            System.out.println("2. Buscar Contato por ID");
            System.out.println("3. Listar Todos os Contatos");
            System.out.println("4. Atualizar Contato");
            System.out.println("5. Deletar Contato");
            System.out.println("6. Trocar de Usuário (Voltar)");
            System.out.print("Escolha: ");
            
            String escolha = sc.nextLine();
            
            try {
                switch (escolha) {
                    case "1":
                        salvarContato(dao);
                        break;
                    case "2":
                        buscarContato(dao);
                        break;
                    case "3":
                        listarContatos(dao);
                        break;
                    case "4":
                        atualizarContato(dao);
                        break;
                    case "5":
                        deletarContato(dao);
                        break;
                    case "6":
                        return; // Volta ao menu de login
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (DAOException e) {
                // --- A MÁGICA ACONTECE AQUI ---
                // Se o GUEST tentar deletar, o DaoProxy lança a exceção,
                // e este 'catch' vai pegá-la.
                System.err.println("\n!!! ERRO DE OPERAÇÃO: " + e.getMessage() + " !!!");
            } catch (NumberFormatException e) {
                System.err.println("\n!!! ERRO: ID inválido. Por favor, digite apenas números. !!!");
            } catch (Exception e) {
                System.err.println("\n!!! ERRO INESPERADO: " + e.getMessage() + " !!!");
            }
        }
    }

    // --- Métodos de Ação (sem alterações) ---

    private static void salvarContato(InterfaceDao dao) throws DAOException {
        System.out.println("\n--- Salvar Novo Contato ---");
        System.out.print("Digite o nome: ");
        String nome = sc.nextLine();
        System.out.print("Digite o email: ");
        String email = sc.nextLine();
        
        Contato novoContato = new Contato(0, nome, email);
        dao.salva(novoContato); // 'dao' aqui é o PROXY
        
        System.out.println(">>> Contato salvo com sucesso! <<<");
    }

    private static void buscarContato(InterfaceDao dao) throws DAOException, NumberFormatException {
        System.out.println("\n--- Buscar Contato por ID ---");
        System.out.print("Digite o ID: ");
        int id = Integer.parseInt(sc.nextLine()); 
        
        Contato contato = dao.buscarPorID(id); // 'dao' aqui é o PROXY
        
        if (contato != null) {
            System.out.println("Resultado: " + contato);
        } else {
            System.out.println(">>> Contato com ID " + id + " não encontrado. <<<");
        }
    }

    private static void listarContatos(InterfaceDao dao) throws DAOException {
        System.out.println("\n--- Listar Todos os Contatos ---");
        List<Contato> lista = dao.buscarTodos(); // 'dao' aqui é o PROXY
        
        if (lista.isEmpty()) {
            System.out.println(">>> Nenhum contato cadastrado. <<<");
            return;
        }
        
        for (Contato c : lista) {
            System.out.println(c);
        }
    }

    private static void atualizarContato(InterfaceDao dao) throws DAOException, NumberFormatException {
        System.out.println("\n--- Atualizar Contato ---");
        System.out.print("Digite o ID do contato a atualizar: ");
        int id = Integer.parseInt(sc.nextLine());
        
        Contato contatoAtual = dao.buscarPorID(id);
        if (contatoAtual == null) {
            System.out.println(">>> Contato com ID " + id + " não encontrado. <<<");
            return;
        }
        
        System.out.println("Dados atuais: " + contatoAtual);
        System.out.print("Digite o NOVO nome (ou Enter para manter '"+contatoAtual.getNome()+"'): ");
        String nome = sc.nextLine();
        if (nome.isEmpty()) nome = contatoAtual.getNome();
        
        System.out.print("Digite o NOVO email (ou Enter para manter '"+contatoAtual.getEmail()+"'): ");
        String email = sc.nextLine();
        if (email.isEmpty()) email = contatoAtual.getEmail();
        
        Contato contatoAtualizado = new Contato(id, nome, email);
        dao.atualizar(contatoAtualizado); // 'dao' aqui é o PROXY
        
        System.out.println(">>> Contato atualizado com sucesso! <<<");
    }

    private static void deletarContato(InterfaceDao dao) throws DAOException, NumberFormatException {
        System.out.println("\n--- Deletar Contato ---");
        System.out.print("Digite o ID do contato a deletar: ");
        int id = Integer.parseInt(sc.nextLine());
        
        dao.deletar(id); // 'dao' aqui é o PROXY
        
        System.out.println(">>> Contato ID " + id + " deletado com sucesso! <<<");
    }
}