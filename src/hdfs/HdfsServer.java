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

public class HdfsServer {

    int id;

    public HdfsServer(int i) {
        this.id = i;
    }


    //  Permet de lire un fichier à partir de HDFS. Les fragments du fichier sont lus à partir des
    // différentes machines, concaténés et stockés localement dans un fichier de nom fname.
	public static void HdfsRead(int id, String fname) {

        try {
            // Socket de lecture
            ServerSocket ssock = new ServerSocket(8000+id);
            Socket s = ssock.accept();
            InputStream is = s.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            
            // Lecture du fragment en entier
            FileWriter filewriter = new FileWriter(new File(fname));
            
            String ligneFragment = (String)ois.readObject();
            while(!(ligneFragment.equals("HDFS : fin de fichier"))){
                filewriter.write(ligneFragment + "\n");

                ligneFragment = (String)ois.readObject();
            }

            filewriter.close();
            ois.close();
            is.close();
            ssock.close();



        } catch (Exception e) {
            e.printStackTrace();
        }


 
	}

    public static void main(String[] args) {
    // java HdfsServer <read|write> <txt|kv> <file> <id>
    // appel des méthodes précédentes depuis la ligne de commande
        //int type = Integer.parseInt(args[1]); //FMT_TXT ou FMT_KV 
        int id = Integer.parseInt(args[3]);
        String newName = args[2].replace(".txt", "-"+id+".txt");


        System.out.println("Je suis HdfsServer_" + args[3] + " et j'écris le fragment " + newName);
        
        // Lecture du frament de fichier envoyé par le HdfsClient
        HdfsRead(id, newName);

        // // Traitement 
        // JobLauncher jl = new JobLauncher();
        // MyMapReduce mmr = new MyMapReduce();
        // jl.startJob(new MyMapReduce(), type, newName);


        // Envoie des résultat au HdfsClient



    }


}
