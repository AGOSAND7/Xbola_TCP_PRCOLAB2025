package cliente;

import java.io.IOException;
import java.util.Scanner;

public class Gameplay {
    private Jogador jogador;
    private Scanner scanner;
    private boolean jogoAtivo;
    
    public Gameplay(Jogador jogador) {
        this.jogador = jogador;
        this.scanner = new Scanner(System.in);
        this.jogoAtivo = true;
    }
    
    public void iniciar() {
        try {
            receberDadosIniciais();
            exibirDadosJogador();
            
            if (escolherOpcaoMenu()) {
                aguardarInicioPartida();
                loopPrincipal();
            }
        } catch (IOException e) {
            System.err.println("Erro de comunicacao: " + e.getMessage());
        } finally {
            encerrar();
        }
    }
    
    private void receberDadosIniciais() throws IOException {
        String mensagem = jogador.receberMensagem();
        if (mensagem != null && mensagem.startsWith("DADOS:")) {
            String[] partes = mensagem.substring(6).split(",");
            int id = Integer.parseInt(partes[0]);
            char simbolo = partes[1].charAt(0);
            boolean comeca = Boolean.parseBoolean(partes[2]);
            jogador.configurar(id, simbolo, comeca);
        }
    }
    
    private void exibirDadosJogador() {
        System.out.println("\n=== DADOS DO JOGADOR ===");
        System.out.println("ID: " + jogador.getId());
        System.out.println("Simbolo: " + jogador.getSimbolo());
        System.out.println("Comeca jogando: " + (jogador.isMinhaVez() ? "SIM" : "NAO"));
        System.out.println("========================\n");
    }
    
    private boolean escolherOpcaoMenu() {
        System.out.println("=== MENU ===");
        System.out.println("1 - Jogar");
        System.out.println("2 - Sair");
        System.out.print("Escolha uma opcao: ");
        
        String opcao = scanner.nextLine().trim();
        
        if (opcao.equals("1")) {
            jogador.enviarMensagem("PRONTO");
            return true;
        } else if (opcao.equals("2")) {
            return false;
        } else {
            System.out.println("Opcao invalida!");
            return escolherOpcaoMenu();
        }
    }
    
    private void aguardarInicioPartida() throws IOException {
        System.out.println("\nAguardando outro jogador estar pronto para iniciar o jogo...\n");
        
        String mensagem = jogador.receberMensagem();
        if (mensagem != null && mensagem.equals("INICIAR")) {
            System.out.println(">>> PARTIDA INICIADA! <<<");
        }
    }
    
    private void loopPrincipal() throws IOException {
        while (jogoAtivo) {
            exibirTabuleiro();
            
            if (jogador.isMinhaVez()) {
                executarMinhaJogada();
            } else {
                aguardarJogadaAdversario();
            }
            
            if (verificarFimDeJogo()) {
                break;
            }
        }
    }
    
    private void exibirTabuleiro() {
        System.out.println(jogador.obterEstadoTabuleiro());
    }
    
    private void executarMinhaJogada() throws IOException {
        int posicao = solicitarPosicao();
        
        jogador.fazerJogada(posicao);
        jogador.enviarMensagem("JOGADA:" + posicao + "," + jogador.getSimbolo());
        jogador.alternarVez();
        
        System.out.println("Jogada enviada. Aguardando adversario...\n");
        
        // Aguardar confirmação
        String confirmacao = jogador.receberMensagem();
        if (confirmacao == null || !confirmacao.equals("PROSSEGUIR")) {
            jogoAtivo = false;
        }
    }
    
    private void aguardarJogadaAdversario() throws IOException {
        System.out.println("Aguardando jogada do outro jogador...\n");
        
        String mensagem = jogador.receberMensagem();
        if (mensagem != null && mensagem.startsWith("JOGADA:")) {
            processarJogadaAdversario(mensagem);
        } else {
            jogoAtivo = false;
        }
    }
    
    private void processarJogadaAdversario(String mensagem) throws IOException {
        String[] partes = mensagem.substring(7).split(",");
        int posicao = Integer.parseInt(partes[0]);
        char simbolo = partes[1].charAt(0);
        
        jogador.registrarJogadaAdversario(posicao, simbolo);
        jogador.alternarVez();
        jogador.enviarMensagem("OK");
        
        // Aguardar confirmação
        String confirmacao = jogador.receberMensagem();
        if (confirmacao == null || !confirmacao.equals("PROSSEGUIR")) {
            jogoAtivo = false;
        }
    }
    
    private int solicitarPosicao() {
        while (true) {
            System.out.print("Digite a posicao [1-9]: ");
            try {
                String entrada = scanner.nextLine().trim();
                int pos = Integer.parseInt(entrada);
                
                if (pos < 1 || pos > 9) {
                    System.out.println(">>> ERRO: Posicao invalida! Digite um numero entre 1 e 9.\n");
                    continue;
                }
                
                if (!jogador.isPosicaoValida(pos)) {
                    System.out.println(">>> ERRO: Posicao ocupada! Tente outra posicao.\n");
                    continue;
                }
                
                return pos;
            } catch (NumberFormatException e) {
                System.out.println(">>> ERRO: Entrada invalida! Digite um numero.\n");
            }
        }
    }
    
    private boolean verificarFimDeJogo() {
        if (!jogador.isJogoFinalizado()) {
            return false;
        }
        
        exibirTabuleiro();
        
        char vencedor = jogador.verificarVencedor();
        if (vencedor == jogador.getSimbolo()) {
            System.out.println(">>> VOCE VENCEU! <<<\n");
        } else if (vencedor != '-') {
            System.out.println(">>> VOCE PERDEU! <<<\n");
        } else {
            System.out.println(">>> EMPATE! <<<\n");
        }
        
        return true;
    }
    
    private void encerrar() {
        jogoAtivo = false;
        jogador.desconectar();
        scanner.close();
        System.out.println("Encerrando jogo...");
    }
}