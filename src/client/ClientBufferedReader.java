package client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Lecteur du client
 * Lire et afficher les messages du serveur
 */
class ClientBufferedReader extends BufferedReader{
    public ClientBufferedReader(Reader in){
        super(in);
    }

    @Override
    public String readLine() throws IOException{
        String line = super.readLine();
        System.out.println("Serveur: " + line);      // read line
        return line;
    }
}