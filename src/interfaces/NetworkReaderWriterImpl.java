package interfaces;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;






public class NetworkReaderWriterImpl implements NetworkReaderWriter {

    public Socket csock;
    public transient ServerSocket ssock;
    public Socket asock;
    private int port;
    private BufferedReader reader;
    private BufferedWriter writer;
    private int format;
    private String adresse;
    private ArrayList<Socket> csockList;
    private ArrayList<ObjectInputStream> ObjectList;

    protected boolean oLect = false;
    protected boolean oEcriture = false;
    protected FileWriter fichierEcriture;
    private BlockingQueue<Boolean> serverSocketReady = new LinkedBlockingQueue<>();



	protected FileReader fichierLecture;
	protected BufferedReader buffer;

    private int index = 0;

    // Constructeur qui utillise une adresse, un format et un port (pas utilisé en fin de compte)
    public NetworkReaderWriterImpl(String a, int f, int p) {
        this.adresse = a;
        this.format = f;
        this.port = p;
    }

    // Constructeur qui utilise un port
    public NetworkReaderWriterImpl(int p) {
        this.port = p;
    }

    // Constructeur sans paramètres qui crée une liste de Sockets
    public NetworkReaderWriterImpl() {
        csockList = new ArrayList<Socket>();
        ObjectList = new ArrayList<ObjectInputStream>();
    }
    
    // Fonction qui permet d'initialiser un serveur Socket
    public void openServer() {
        try {
            // Le Reduce peut ouvrir une connexion pour récolter les resultats (lire des KV) => openServer()
            this.ssock = new ServerSocket(this.port); 
            System.out.println("Server Socket crée au port" + this.port);

            // Utilisation du principe de blocking queues pour assurer la synchronisation de l'ouverture / fermeture des Sockets
            serverSocketReady.offer(true); // Signale au client que le serveur est prêt
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

	public void openClient() { 
        try {
            serverSocketReady.take(); // Attend que le serveur soit prêt (attente du signal du openServer)
            // Le Map peut ouvrir une connexion pour lire des KV depuis le fragment
            System.out.println(String.valueOf(this.port));
            this.csock = new Socket("localhost", this.port); 
            System.out.println("Socket Client crée au port : "+this.port);
            reader = new BufferedReader(new InputStreamReader(csock.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

	public NetworkReaderWriter accept() {
        try {
            // accepte une connexion sur le socket Serveur 
            Socket asock = ssock.accept();
            NetworkReaderWriterImpl newConnection = new NetworkReaderWriterImpl(port);
            newConnection.setSocket(asock);
            return newConnection;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void setSocket(Socket socket) {
        this.csock = socket;
    }

    // Méthode pour fermer le socket Server
	public void closeServer() { 
        try {
            if (ssock != null) {
                if(!ssock.isClosed()){ 
                    ssock.close();
                } else { 
                    System.out.println("Socket Server est déjà fermé");
                }
            } else  { 
                System.out.println("Socket Server est null, demande de fermture refusé");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour fermer le socket Client
	public void closeClient() {
        try {
            if (csock != null) {
                if(!csock.isClosed()){ 
                    csock.close();
                } else { 
                    System.out.println("Socket Client est déjà fermé");
                }
            } else  { 
                System.out.println("Socket Client est null, demande de fermture refusé");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Méthode qui initialise les entrées de la connexion réseau (lecture Input)
    public KV read() {	
		try {
            ObjectInputStream objectInputStream = new ObjectInputStream(csock.getInputStream());
            return (KV)objectInputStream.readObject();

            // // Si le KV lu est null, c'est un end of file donc on écrit null pour indiquer la fin de fichier
            // if (kvRead == new KV("EndOfFile","0")) { 
            //     return null;
            // } else {
            //     return kvRead;    
            // }

        } 
        // Si c'est le fin du fichier, on écrit null
        catch (EOFException e) {
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}

    // Méthode qui initialise les sorties de la connexion réseau (écriture Output)
    public void write(KV record) {
        try {
            // Si le KV lu est null, c'est un end of file donc on envoit un KV spéciale qui indique la fin de fichier
            if (record == null) { 
                record = new KV("EndOfFile","0");    
            }

            //Envoie du KV sur le network
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(csock.getOutputStream());
            objectOutputStream.writeObject(record);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

 
	
}


