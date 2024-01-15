package hdfs;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import application.MyMapReduce;
import interfaces.FileReaderWriter;
import daemon.JobLauncher;

import config.*;

public class HdfsServer {
    static Config config = new Config();
    int id;

    public HdfsServer(int i) {
        this.id = i;
    }


    //  Permet de lire un fichier à partir de HDFS. Les fragments du fichier sont lus à partir des
    // différentes machines, concaténés et stockés localement dans un fichier de nom fname.
	public static void HdfsRead(int id, String fname) {

        try {
            // Socket de lecture
            ServerSocket ssock = new ServerSocket(config.getPortSocket(id));
            System.out.println("HDFS : ServerSocket créé en attente de connection");
            Socket s = ssock.accept();
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            System.out.println("HDFS : ServerSocket connecté");
            

            // Lecture du fragment en entier
            FileWriter filewriter = new FileWriter(new File(Project.PATH+"/data/"+config.getFname().replace(".txt", "-"+id+".txt")));
            
            String ligneFragment = (String)ois.readObject(); //Lecture de la premiere ligne
            while(ligneFragment != null){   // null = fin de fichier
                filewriter.write(ligneFragment + "\n");

                ligneFragment = (String)ois.readObject();
            }

            System.out.println("HDFS : fragment " + config.getFname().replace(".txt", "-"+id+".txt") + " reçu.");
            filewriter.close();
            ois.close();
            ssock.close();



        } catch (Exception e) {
            e.printStackTrace();
        }


 
	}

    // java HdfsServer <read|write> <txt|kv> <file> <id>
    // appel des méthodes précédentes depuis la ligne de commande
    public static void main(String[] args) {
        //int type       = Integer.parseInt(args[1]); //FMT_TXT ou FMT_KV 
        int id         = Integer.parseInt(args[3]);
        String fname   =  args[2];
        String newName = fname.replace(".txt", "-"+id+".txt");
        
        // Lecture du frament de fichier envoyé par le HdfsClient
        HdfsRead(id, newName);

    }


}
