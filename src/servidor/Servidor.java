package servidor;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Servidor {
    private static final int PORTA = 12345;
    private static final int MAX_JOGADORES = 2;
    
    private final Map<Integer, DadosJogador> dadosDisponiveis;
    private final List<ManipuladorCliente> jogadores;
    private final Object lockConexao;
    
    public Servidor() {
        dadosDisponiveis = new ConcurrentHashMap<>();
        jogadores = new CopyOnWriteArrayList<>();
        lockConexao = new Object();
        inicializarDados();
    }
    
    private void inicializarDados() {
        dadosDisponiveis.put(1, new DadosJogador(1, 'X', true));
        dadosDisponiveis.put(2, new DadosJogador(2, 'O', false));
    }
    
    public void iniciar() {
        exibirInformacoes();
        
        try (ServerSocket servidor = new ServerSocket(PORTA)) {
            while (true) {
                Socket clienteSocket = servidor.accept();
                processarNovaConexao(clienteSocket);
            }
        } catch (IOException e) {
            System.err.println("Erro no servidor: " + e.getMessage());
        }
    }
    
    private void exibirInformacoes() {
        System.out.println("=== SERVIDOR XBOLA ===");
        System.out.println("Porta: " + PORTA);
        System.out.println("Aguardando jogadores...");
        
        try {
            String ip = InetAddress.getLocalHost().getHostAddress();
            System.out.println("IP do servidor: " + ip);
        } catch (UnknownHostException e) {
            System.out.println("IP do servidor: <indisponivel>");
        }
        
        System.out.println("Conexoes maximas: " + MAX_JOGADORES);
        System.out.println("Conectados agora: 0\n");
    }
    
    private void processarNovaConexao(Socket clienteSocket) {
        synchronized (lockConexao) {
            if (jogadores.size() >= MAX_JOGADORES) {
                rejeitarConexao(clienteSocket);
                return;
            }
            
            if (dadosDisponiveis.isEmpty()) {
                rejeitarConexao(clienteSocket);
                return;
            }
            
            aceitarConexao(clienteSocket);
        }
    }
    
    private void rejeitarConexao(Socket socket) {
        try {
            PrintWriter saida = new PrintWriter(socket.getOutputStream(), true);
            saida.println("ERRO:Servidor cheio");
            socket.close();
            System.out.println("[SERVIDOR] Conexao rejeitada - servidor cheio");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void aceitarConexao(Socket socket) {
        Integer idDisponivel = dadosDisponiveis.keySet().iterator().next();
        DadosJogador dados = dadosDisponiveis.remove(idDisponivel);
        
        ManipuladorCliente cliente = new ManipuladorCliente(socket, dados, this);
        jogadores.add(cliente);
        cliente.start();
        
        System.out.println("Conectados agora: " + jogadores.size());
        System.out.println("[J" + dados.id + "] Conectado (simbolo=" + dados.simbolo + ")");
    }
    
    void verificarInicioPartida() {
        synchronized (lockConexao) {
            if (jogadores.size() == MAX_JOGADORES) {
                boolean todosListos = jogadores.stream().allMatch(ManipuladorCliente::isPronto);
                if (todosListos) {
                    System.out.println("\n>>> Ambos jogadores prontos - INICIANDO PARTIDA <<<\n");
                    broadcast("INICIAR");
                }
            }
        }
    }
    
    void repassarMensagem(ManipuladorCliente remetente, String mensagem) {
        for (ManipuladorCliente cliente : jogadores) {
            if (cliente != remetente) {
                cliente.enviarMensagem(mensagem);
            }
        }
    }
    
    void broadcast(String mensagem) {
        for (ManipuladorCliente cliente : jogadores) {
            cliente.enviarMensagem(mensagem);
        }
    }
    
    void removerJogador(ManipuladorCliente cliente) {
        synchronized (lockConexao) {
            jogadores.remove(cliente);
            DadosJogador dados = cliente.getDados();
            if (dados != null) {
                dadosDisponiveis.put(dados.id, dados);
            }
            System.out.println("Conectados agora: " + jogadores.size());
        }
    }
    
    static class DadosJogador {
        final int id;
        final char simbolo;
        final boolean comeca;
        
        DadosJogador(int id, char simbolo, boolean comeca) {
            this.id = id;
            this.simbolo = simbolo;
            this.comeca = comeca;
        }
    }
    
    static class ManipuladorCliente extends Thread {
        private final Socket socket;
        private final DadosJogador dados;
        private final Servidor servidor;
        private BufferedReader entrada;
        private PrintWriter saida;
        private boolean pronto;
        
        ManipuladorCliente(Socket socket, DadosJogador dados, Servidor servidor) {
            this.socket = socket;
            this.dados = dados;
            this.servidor = servidor;
            this.pronto = false;
        }
        
        @Override
        public void run() {
            try {
                inicializarStreams();
                enviarDadosIniciais();
                processarMensagens();
            } catch (IOException e) {
                System.out.println("[J" + dados.id + "] Desconectado");
            } finally {
                desconectar();
            }
        }
        
        private void inicializarStreams() throws IOException {
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            saida = new PrintWriter(socket.getOutputStream(), true);
        }
        
        private void enviarDadosIniciais() {
            enviarMensagem("DADOS:" + dados.id + "," + dados.simbolo + "," + dados.comeca);
        }
        
        private void processarMensagens() throws IOException {
            String mensagem;
            while ((mensagem = entrada.readLine()) != null) {
                processarMensagem(mensagem);
            }
        }
        
        private void processarMensagem(String mensagem) {
            if (mensagem.equals("PRONTO")) {
                pronto = true;
                System.out.println("[J" + dados.id + "] Esta pronto");
                servidor.verificarInicioPartida();
                
            } else if (mensagem.startsWith("JOGADA:")) {
                servidor.repassarMensagem(this, mensagem);
                
            } else if (mensagem.equals("OK")) {
                servidor.broadcast("PROSSEGUIR");
            }
        }
        
        void enviarMensagem(String mensagem) {
            if (saida != null) {
                saida.println(mensagem);
            }
        }
        
        private void desconectar() {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            servidor.removerJogador(this);
        }
        
        boolean isPronto() {
            return pronto;
        }
        
        DadosJogador getDados() {
            return dados;
        }
    }
}