package br.com.gbd.sudoku;

import java.util.logging.Level;
import java.util.logging.Logger;

/*
    Autor(es): José Carlos de Freitas
    Data: 01/02/2017 às 22:25:21
    Arquivo: SudokuThread
 */
public class SudokuThread extends Thread {

    private int[][][][] matriz;
    private int l;
    private int c;
    private double tempo;

    public SudokuThread(int [][][][]matriz, int l, int c, double tempo) {
        this.matriz = matriz;
        this.l = l;
        this.c = c;
        this.tempo = tempo;
    }

    @Override
    public void run() {
        try {
            combinaValores(matriz, 0, 0);
        } catch (InterruptedException ex) {
            Logger.getLogger(SudokuThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void combinaValores(int[][][][] matriz, int l, int c) throws InterruptedException {
        l++;
        if (l >= Sudoku.TAMANHO) {
            l = 0;
            c++;
        }

        if (c < Sudoku.TAMANHO) {
            if (matriz[l][c][0][0] == 0) { //Exclui quadrados preenchidos
                int aux[][] = matriz[l][c];
                for (int val = 0; val < Sudoku.subMatrizes.size(); val++) {
                    matriz[l][c] = Sudoku.subMatrizes.get(val);
                    if (this.verificaQuadrado(matriz, l, c)) {
                        combinaValores(matriz, l, c);
                    }
                }
                matriz[l][c] = aux;
            }
        } else {
            Sudoku.mutex.acquire();
            Sudoku.total++;
            Sudoku.mutex.release(); 
            if (Sudoku.total % 1000 == 0) {
                System.out.println(Sudoku.total + " CALCULADOS EM " + (System.nanoTime() - tempo) / 1000000000 + " SEGUNDOS");
            }
        }
    }

    public boolean verificaQuadrado(int matriz[][][][], int l, int c) {
        for (int i = 0; i < Sudoku.TAMANHO; i++) {
            for (int j = 0; j < Sudoku.TAMANHO; j++) {
                if (!Sudoku.verificaCelula(matriz, l, c, i, j)) {
                    return false;
                }
            }
        }

        return true;
    }
}
