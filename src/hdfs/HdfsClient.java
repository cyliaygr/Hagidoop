package hdfs;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

import interfaces.*;

import config.*;

public class HdfsClient extends Thread {
	static Config config = new Config();

	static int nbrFragment;
	public static final int FMT_TXT = 0;
	public static final int FMT_KV = 1;
	private String fname;

	
	
	private static void usage() {
		System.out.println("Usage: java HdfsClient read <file>");
		System.out.println("Usage: java HdfsClient write <txt|kv> <file>");
		System.out.println("Usage: java HdfsClient delete <file>");
	}
	
	// Permet de supprimer les fragments d'un fichier stocké dans HDFS
	public static void HdfsDelete(String fname) {
		File file = new File(fname);
		file.delete();
		System.out.println(fname +" a été supprimé.");
	}
	
	//  Permet d'écrire un fichier dans HDFS. Le fichier fname est lu sur le système de fichiers local,
	// découpé en fragments (autant que le nombre de machines) et les fragments sont envoyés pour
	// stockage sur les différentes machines. fmt est le format du fichier (FMT_TXT ou FMT_KV).
	public static void HdfsWrite(int fmt, String fname) {
		try {
			//Attend 1s pour être sur que les workers aient créé leurs SocketServeur
			try {
				
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// Creation des sockets pour envoyer les fragments
			nbrFragment = config.getNbWorker();
			Socket[] s               = new Socket[nbrFragment];
			ObjectOutputStream[] oos = new ObjectOutputStream[nbrFragment];
			for(int i=0; i<nbrFragment; i++){
				System.out.println(config.getNom(i+1));
				System.out.println(config.getPortSocket(i+1));
				s[i] = new Socket(config.getNom(i+1), config.getPortSocket(i+1)); //i+1 car la machine 0 est le client
				oos[i] = new ObjectOutputStream(s[i].getOutputStream());
			}

			// Fichier .txt à fragemnter
			if (fmt == FileReaderWriter.FMT_TXT){
				// Reader du fichier à fragmenter
				BufferedReader fileReader = new BufferedReader(new FileReader(fname));
				
				// Lecture et envoi des lignes du fichier une par une
				String line;
				int nbrFragmentSelectione = 0;
				int nbrFragment = config.getNbWorker();

				System.out.println("HDFS : fragmentation de " + fname + " en " + nbrFragment + " fragments.");
				while ((line = fileReader.readLine()) != null) {
					oos[nbrFragmentSelectione].writeObject(line);
					nbrFragmentSelectione = (nbrFragmentSelectione+1)%nbrFragment;
				}

				fileReader.close();
			}
			// Fichier KV à envoyer aux HdfsServeurs
			else if (fmt == FileReaderWriter.FMT_KV){

			}
			else{
				System.err.println("HdfsClient : args[1] n'est pas <txt|kv>, donc HdfsClient.write() ne peut pas s'exécuter");
			}

			// Fermeture des sockets avec les serveurs
			for(int i=0; i<nbrFragment; i++){
				oos[i].writeObject(null); //Indique la fin du fichier
				oos[i].close();
				s[i].close();
			}

        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	//  Permet de lire un fichier à partir de HDFS. Les fragments du fichier sont lus à partir des
    // différentes machines, concaténés et stockés localement dans un fichier de nom fname.
	public static void HdfsRead(String fname) {
		

	}

	public static void main(String[] args) {
		// java HdfsClient <read|write> <txt|kv> <file>
		// appel des méthodes précédentes depuis la ligne de commande

		switch (args[0]) {
			case "write":
				HdfsWrite(FileReaderWriter.FMT_TXT, "../data/"+args[2]);
				break;
			
			case "read":
				HdfsRead("../data/"+args[2]);
				break;
				
			default:
				System.err.println("HdfsClient : args[0] n'est pas <read|write>, donc HdfsClient ne peut pas s'exécuter");
				break;
		}

		// Envoie les fragments au serveur


	}
	}

