package br.com.gbd.sudoku;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
    Autor(es): José Carlos de Freitas
    Data: 31/01/2017 às 23:55:09
    Arquivo: Sudoku
 */
public class Sudoku {

    static Semaphore mutex = new Semaphore(1);
    public static final int TAMANHO = 3;
    public static final int TOTAL_THREAD = 6;
    public static List<int[][]> subMatrizes;
    public static int total = 0;

    public int[][] subMatrizUsuario;
    int i;
    int j;

    public Sudoku() {
        this.subMatrizes = new ArrayList<int[][]>();
        this.inicilizar();
    }

    public void inicilizar() {
        String jogo = "";
        try {
            jogo = new LerArquivo("sudoku.txt").ler();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        Pattern pattern = Pattern.compile("(\\d)\\s+(\\d)\\s+(\\d)\\s*\\n");
        Matcher matcher = pattern.matcher(jogo);

        this.subMatrizUsuario = new int[TAMANHO][TAMANHO];
        while (matcher.find()) {
            int val = Integer.parseInt(matcher.group(1));
            int coluna = Integer.parseInt(matcher.group(2));
            int linha = Integer.parseInt(matcher.group(3));

            this.i = linha / 3;
            this.j = coluna / 3;

            this.subMatrizUsuario[linha % TAMANHO][coluna % TAMANHO] = val;
        }
    }

    public void geraSubMatrizes() {
        int subMatriz[][] = new int[TAMANHO][TAMANHO];

        this.geraSubMatrizes(subMatriz, 0, -1);
    }

    public void geraSubMatrizes(int subMatriz[][], int i, int j) {
        j++;
        if (j >= TAMANHO) {
            j = 0;
            i++;
        }

        if (i < TAMANHO) {
            for (int val = 1; val <= (TAMANHO * TAMANHO); val++) {
                subMatriz[i][j] = val;
                if (this.verificaCelulaQuadrado(subMatriz, i, j)) {
                    this.geraSubMatrizes(subMatriz, i, j);
                }
            }
            subMatriz[i][j] = 0;
        } else {
            int aux[][] = new int[TAMANHO][TAMANHO];
            for (int l = 0; l < TAMANHO; l++) {
                for (int c = 0; c < TAMANHO; c++) {
                    aux[l][c] = subMatriz[l][c];
                }
            }
            this.subMatrizes.add(aux);
        }
    }

    public void printSubMatrizes() {
        System.out.println("TOTAL DE MATRIZES: " + this.subMatrizes.size());
        for (int[][] subMatriz : this.subMatrizes) {
            System.out.println("\n_________");
            for (int i = 0; i < TAMANHO; i++) {
                System.out.println("");
                for (int j = 0; j < TAMANHO; j++) {
                    System.out.print(subMatriz[i][j] + "|");
                }
            }
        }
    }

    public boolean verificaCelulaQuadrado(int subMatriz[][], int l, int c) {
        for (int i = 0; i < this.TAMANHO; i++) {
            for (int j = 0; j < this.TAMANHO; j++) {
                if (subMatriz[l][c] == subMatriz[i][j]) {
                    if (l != i || c != j) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public boolean verificaQuadrado(int matriz[][][][], int l, int c) {
        for (int i = 0; i < this.TAMANHO; i++) {
            for (int j = 0; j < this.TAMANHO; j++) {
                if (!this.verificaCelula(matriz, l, c, i, j)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean verificaCelula(int matriz[][][][], int l, int c, int i, int j) {
        for (int a = 0; a < TAMANHO; a++) {
            for (int b = 0; b < TAMANHO; b++) {
                //Quadrado
                if (matriz[l][c][i][j] == matriz[l][c][a][b]) {
                    if (!(i == a && j == b)) {
                        return false;
                    }
                }
                //Linha
                if (matriz[l][c][i][j] == matriz[l][a][i][b]) {
                    if (!(c == a && j == b)) {
                        return false;
                    }
                }
                //Coluna
                if (matriz[l][c][i][j] == matriz[a][c][b][j]) {
                    if (!(l == a && i == b)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void combinaValores() {
        double tempo = System.nanoTime();
        //Define um pool de execução de threads (máximo 100 executando simultaneas)  
        ExecutorService exec = Executors.newFixedThreadPool(TOTAL_THREAD);
        for (int p = 0; p < this.subMatrizes.size(); p++) {
            int[][][][] matriz = new int[TAMANHO][TAMANHO][TAMANHO][TAMANHO];

            matriz[this.i][this.j] = this.subMatrizUsuario;

            SudokuThread t = new SudokuThread(matriz, 0, 0, tempo);
            exec.submit(t);
        }
        exec.shutdown();
        try {
            exec.awaitTermination(10, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            System.out.println("erro");
        }
        System.out.println(this.total + " CALCULADOS EM " + (System.nanoTime() - tempo) / 1000000000 + " SEGUNDOS");
    }

    public void print(int matriz[][][][]) {
        System.out.print("\n__________________");
        for (int i1 = 0; i1 < TAMANHO; i1++) {
            for (int i2 = 0; i2 < TAMANHO; i2++) {
                System.out.println("");
                for (int j1 = 0; j1 < TAMANHO; j1++) {
                    System.out.print("  ");
                    for (int j2 = 0; j2 < TAMANHO; j2++) {
                        System.out.print(matriz[i1][j1][i2][j2] + "|");
                    }
                }
            }
            System.out.println("\n");
        }
    }

    public void combinaValores(int[][][][] matriz, int l, int c) {
        l++;
        if (l >= TAMANHO) {
            l = 0;
            c++;
        }

        if (c < TAMANHO) {
            if (matriz[l][c][0][0] == 0) { //Exclui quadrados preenchidos
                int aux[][] = matriz[l][c];
                for (int val = 0; val < this.subMatrizes.size(); val++) {
                    matriz[l][c] = this.subMatrizes.get(val);
                    if (this.verificaQuadrado(matriz, l, c)) {
                        combinaValores(matriz, l, c);
                    }
                }
                matriz[l][c] = aux;
            }
        } else {
            this.total++;
        }
    }
}
