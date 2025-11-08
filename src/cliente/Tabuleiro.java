package cliente;

public class Tabuleiro {
    private final char[] campo;
    private int totalPreenchido;

    public Tabuleiro() {
        campo = new char[9];
        limpar();
    }

    public void limpar() {
        for (int i = 0; i < 9; i++) {
            campo[i] = '-';
        }
        totalPreenchido = 0;
    }

    public boolean inserir(int pos, char simbolo) {
        if (!posValida(pos)) {
            return false;
        }
        campo[pos - 1] = simbolo;
        totalPreenchido++;
        return true;
    }

    public boolean posValida(int pos) {
        return pos >= 1 && pos <= 9 && campo[pos - 1] == '-';
    }

    public char getVencedor() {
        int[][] linhas = {
            {0,1,2}, {3,4,5}, {6,7,8},  // horizontais
            {0,3,6}, {1,4,7}, {2,5,8},  // verticais
            {0,4,8}, {2,4,6}             // diagonais
        };
        
        for (int[] linha : linhas) {
            char primeiro = campo[linha[0]];
            if (primeiro != '-' && 
                primeiro == campo[linha[1]] && 
                primeiro == campo[linha[2]]) {
                return primeiro;
            }
        }
        return '-';
    }

    public boolean isEmpate() {
        return totalPreenchido == 9 && getVencedor() == '-';
    }

    public boolean isFinalizado() {
        return getVencedor() != '-' || isEmpate();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\n");
        for (int i = 0; i < 9; i++) {
            sb.append(campo[i]);
            if ((i + 1) % 3 == 0) {
                sb.append("\n");
            } else {
                sb.append(" | ");
            }
        }
        return sb.toString();
    }
}