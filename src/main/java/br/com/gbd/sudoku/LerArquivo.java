package br.com.gbd.sudoku;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/*
    Autor(es): José Carlos de Freitas
    Data: 29/01/2017 às 16:20:25
    Arquivo: LerArquivo
*/
public class LerArquivo {

    private String path;

    public LerArquivo(String path) {
        this.path = path;
    }
    
    public String ler() throws FileNotFoundException, IOException{
        String everything = "";
        BufferedReader br = new BufferedReader(new FileReader("sudoku.txt"));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            everything = sb.toString();
        } finally {
            br.close();
        }
        
        return everything;
    }
}
