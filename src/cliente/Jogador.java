package cliente;

import java.io.*;
import java.net.*;

public class Jogador {
    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter saida;
    
    private int id;
    private char simbolo;
    private boolean minhaVez;
    private Tabuleiro tabuleiro;
    
    public Jogador() {
        tabuleiro = new Tabuleiro();
    }
    
    public boolean conectar(String host, int porta) {
        try {
            socket = new Socket(host, porta);
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            saida = new PrintWriter(socket.getOutputStream(), true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    public void configurar(int id, char simbolo, boolean comeca) {
        this.id = id;
        this.simbolo = simbolo;
        this.minhaVez = comeca;
    }
    
    public String receberMensagem() throws IOException {
        return entrada.readLine();
    }
    
    public void enviarMensagem(String mensagem) {
        saida.println(mensagem);
    }
    
    public boolean fazerJogada(int posicao) {
        return tabuleiro.inserir(posicao, simbolo);
    }
    
    public boolean registrarJogadaAdversario(int posicao, char simboloAdversario) {
        return tabuleiro.inserir(posicao, simboloAdversario);
    }
    
    public void alternarVez() {
        minhaVez = !minhaVez;
    }
    
    public String obterEstadoTabuleiro() {
        return tabuleiro.toString();
    }
    
    public boolean isPosicaoValida(int pos) {
        return tabuleiro.posValida(pos);
    }
    
    public char verificarVencedor() {
        return tabuleiro.getVencedor();
    }
    
    public boolean isJogoFinalizado() {
        return tabuleiro.isFinalizado();
    }
    
    public void desconectar() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    public int getId() { return id; }
    public char getSimbolo() { return simbolo; }
    public boolean isMinhaVez() { return minhaVez; }
}