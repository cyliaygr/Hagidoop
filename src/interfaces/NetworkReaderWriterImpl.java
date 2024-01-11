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



public class NetworkReaderWriterImpl implements NetworkReaderWriter {

    private Socket csock;
    private ServerSocket ssock;
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

    public NetworkReaderWriterImpl() {
        csockList = new ArrayList<Socket>();
        ObjectList = new ArrayList<ObjectInputStream>();
    }

    public void openServer() {
        try {
            // Reduce peut ouvrir une connexion pour récolter les resultats (lire des KV)
            ssock = new ServerSocket(port); 
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

	public void openClient() { 
        try {
            // Map peut ouvrir une connexion pour lire des KV depuis le fragment
            csock = new Socket("localhost", port); 
            reader = new BufferedReader(new InputStreamReader(csock.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

	public NetworkReaderWriter accept() {
        NetworkReaderWriterImpl newConnection = new NetworkReaderWriterImpl(adresse, port, format);
        try {
            // Map peut ouvrir une connexion pour lire des KV depuis le fragment
            newConnection.asock = ssock.accept();
            return newConnection;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
          

       
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
		String line = null;
		if(oLect){
			try{
				line = buffer.readLine();
				this.index++;
				
			}catch (IOException e){
				e.printStackTrace();
			}
		} else {
			System.err.println("Message erreur de READ");
		}
		
		if(line != null){
			return new KV(""+index,line);
		} else {
			return null;
		}
	}

    public void write(KV record) {
        if (!oEcriture) {
            System.err.println("Opértation interdite");
            return;
        }
        try {
            fichierEcriture.write(record.v + "\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
}


