package br.com.gbd.sudoku;


/*
    Autor(es): José Carlos de Freitas
    Data: 27/01/2017 às 20:27:24
    Arquivo: Main
 */
public class Main {

    public static void main(String[] args) {
        Sudoku sudoku = new Sudoku();
        
        sudoku.geraSubMatrizes();
        
        sudoku.combinaValores();
    }
}
