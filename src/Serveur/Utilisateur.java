package Serveur;

import java.net.Socket;
import java.util.ArrayList;

// User class
class Utilisateur {
    private String pseudo;
    private String iconUtilisateur;

    private ArrayList<String> newMessagesNick = new ArrayList<String>();
    private ArrayList<String> newMessagesIcon = new ArrayList<String>();
    private ArrayList<String> newMessagesTime = new ArrayList<String>();

    private ArrayList<String> newMessages = new ArrayList<String>();
	protected Socket socket;

	// Constructors
	Utilisateur(Socket s){
		// Setting socket
		this.socket = s;
	}

    public String nickName(){
        return pseudo;
    }

    public void nickName(String nickName){
        this.pseudo = nickName;
    }

    public String getUserIconPic(){
        return iconUtilisateur;
    }

    public void setUserIconPic(String userIconPic){
        this.iconUtilisateur = userIconPic;
    }

    public ArrayList<String> getNewMessages(){
        return newMessages;
    }

    public ArrayList<String> getNewMessagesNick(){
        return newMessagesNick;
    }

    public ArrayList<String> getNewMessagesIcon(){
        return newMessagesIcon;
    }

    public ArrayList<String> getNewMessagesTime(){
        return newMessagesTime;
    }

    public String getNickName(){
        return pseudo;
    }
}
