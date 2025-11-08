package cliente;

import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        System.out.println("=== XBOLA ===\n");
        
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Digite o IP do servidor: ");
        String host = scanner.nextLine().trim();
        
        System.out.print("Digite a porta (padrao 12345): ");
        String portaStr = scanner.nextLine().trim();
        int porta = portaStr.isEmpty() ? 12345 : Integer.parseInt(portaStr);
        
        Jogador jogador = new Jogador();
        
        System.out.println("\nConectando ao servidor " + host + ":" + porta + "...");
        if (jogador.conectar(host, porta)) {
            System.out.println("Conectado com sucesso!");
            Gameplay gameplay = new Gameplay(jogador);
            gameplay.iniciar();
        } else {
            System.out.println("Falha ao conectar ao servidor.");
        }
        
        scanner.close();
    }
}