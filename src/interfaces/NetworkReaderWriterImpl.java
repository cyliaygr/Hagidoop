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



public class NetworkReaderWriterImpl implements NetworkReaderWriter {

    public Socket csock;
    public ServerSocket ssock;
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


	protected FileReader fichierLecture;
	protected BufferedReader buffer;

    private int index = 0;

    public NetworkReaderWriterImpl(String a, int f, int p) {
        this.adresse = a;
        this.format = f;
        this.port = p;
    }

    public NetworkReaderWriterImpl(int p) {
        this.port = p;
    }

    public NetworkReaderWriterImpl() {
        csockList = new ArrayList<Socket>();
        ObjectList = new ArrayList<ObjectInputStream>();
    }
    

    public void openServer() {
        try {
            // Reduce peut ouvrir une connexion pour récolter les resultats (lire des KV)
            this.ssock = new ServerSocket(this.port); 
            System.out.println("Server Socket crée au port" + this.port);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

	public void openClient() { 
        try {
            // Map peut ouvrir une connexion pour lire des KV depuis le fragment
            System.out.println(String.valueOf(this.port));
            this.csock = new Socket("localhost", this.port); 
            System.out.println("SOCKET CREE");
            reader = new BufferedReader(new InputStreamReader(csock.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

	public NetworkReaderWriter accept() {
        try {
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

	public void closeServer() { 
        try {
            if (!ssock.isClosed() && ssock != null) {
                ssock.close();
            } else  { 
                System.out.println("Socket Server déjà fermé");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public void closeClient() {
        try {
            if (!csock.isClosed() && csock != null) {
                csock.close();
            } else  { 
                System.out.println("Socket Client déjà fermé");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public KV read() {	
		try {
            ObjectInputStream objectInputStream = new ObjectInputStream(csock.getInputStream());
            return (KV) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
	}

    public void write(KV record) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(csock.getOutputStream());
            objectOutputStream.writeObject(record);
            objectOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

 
	
}


