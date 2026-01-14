package TERMINAL_Completo_CSV.Cliente.View;

import java.util.Scanner;

public class PessoaView {

    Scanner sc = new Scanner(System.in);

    public int mostrarOpcoes(){
        System.out.println("\n--- SISTEMA DE GERENCIAMENTO ---");
        System.out.println("1 - Cadastrar (Create)");
        System.out.println("2 - Listar (Read)");
        System.out.println("3 - Atualizar (Update)");
        System.out.println("4 - Deletar (Delete)");
        System.out.println("0 - Sair");
        System.out.print("Escolha uma opção: ");

        try {
            int opcao = sc.nextInt();
            sc.nextInt(); //nn esquecer!!!
            return opcao;
        } catch (Exception e) {
            sc.nextLine(); //limpa a entrada invalida
            return -1; //retorna opção invalida
        }
    }

    public int getNum(){
        System.out.println("Digite um numero: ");
        int num = sc.nextInt();
        sc.nextLine();
        return num;
    }

    public long getMatricula(){
        System.out.println("Digite uma matricula");
        long matricula = sc.nextLong();
        sc.nextLong();
        return matricula;
    }

    public String getNome(){
        System.out.println("Digite um nome");
        String nome = sc.nextLine();
        return nome;
    }

    public void mostrarMensagem(String mensagem){
        System.out.println(mensagem);
    }
}
