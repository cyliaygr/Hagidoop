package daemon;

import java.util.List;
import java.util.ArrayList;
import interfaces.*;
import config.*;
import java.io.InputStream;
import java.io.ObjectInputStream;
import application.*;

import hdfs.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class JobLauncher {

	static Config config = new Config();
	public static List<Worker> listeWorker = new ArrayList<>();




	static private int nbWorker;

	public static void startJob (MapReduce mr, int format, String fname) {
		
		String pathData = Project.PATH + "/data/";
		String[] nomExt = fname.split("\\.");	//Sépare le nom et l'extention 
		nbWorker        = config.getNbWorker();

		// ----------------------------------------
		// 			 LANCE LES RUNMAPS 	
		//-----------------------------------------
		try {
			// Connexion RMI avec les Workers déjà créé par le script de lancement
			System.out.println("Connection avec les workers par RMI :");
			for (int i = 0 ; i < nbWorker ; i++) {
				System.out.println("| worker-"+i+" en "+config.getURL(i+1));
				Worker worker = (Worker) Naming.lookup(config.getURL(i+1));
				listeWorker.add(worker);
			}

			//Writer sur le réseau vers reduce
			System.out.println("| NetworkReaderWrite : nom="+config.getNom(0)+" port="+config.getPortSocket(0));
			NetworkReaderWriterImpl networkRW = new NetworkReaderWriterImpl(config.getNom(0),FileReaderWriter.FMT_KV, config.getPortSocket(0));	  
			
			// Créer et lance le runMap dans un thread pour chaque worker afin de les éxécuter en parallel
			List<Thread> workerThreads = new ArrayList<>();
			int i = 1;
			System.out.println("\nLancement des ruRmap() :");
			for (Worker worker : listeWorker) {
				FileReaderWriter reader = new FileTxtReaderWriter(pathData + nomExt[0] + "-" + i + "." + nomExt[1]);	
				
				System.out.println("| Worker-"+i+".runMap() lancé");
				Thread workerThread = new Thread(() -> {
					try {
						worker.runMap(mr, reader, networkRW);
					} catch (RemoteException e) {
						// Gérer l'exception RemoteException ici
						e.printStackTrace();
					}				
				});
			
				workerThreads.add(workerThread);
				workerThread.start();
				i++;
			}
			
			
		// ----------------------------------------
		// 			 LANCE LE REDUCE 	
		//-----------------------------------------
			//Ecrit dans les resultat dans un fichier cpntant des KV
			FileKVReaderWriter writerRes = new FileKVReaderWriter(pathData + nomExt[0] + "-res.kv");	
			writerRes.open("W");
			//Ouvre un serverSocket où les worker se connectent (pour envoyer leurs résultats)
			networkRW.openServer();
			
			// Lance les reduce dans des thread pour pouvoir executer le reduce sur un NetworkReaderWriter
			//avec un worker tout en acceptant les demandes des autres workers
			System.out.println("\nLancement des reduce() :");
			List<Thread> reduceThreads = new ArrayList<>();
			for(int y=0; y<nbWorker; y++){
				//Connection d'un worker via une socket
				NetworkReaderWriter connexionRecu = networkRW.accept();

				//Lance le reduce dans un thread
				final int yFinal = y;
				Thread reduceThread = new Thread(() -> {
					// Reader = NetworkReaderWriter sur la connection socket du worker
					// Writer = FileKVReaderWriter sur le fichier de résultats
					System.out.println("| + Worker-"+yFinal+".reduce() lancé");
					mr.reduce(connexionRecu, writerRes);	
					System.out.println("| - Worker-"+yFinal+".reduce() fini");
					connexionRecu.closeServer();
				});

				reduceThreads.add(reduceThread);
    			reduceThread.start();				
			}
			
			// Attend la fin de tout les reduce avant de fermer les connections et les reader/writer
			for (Thread reduceT : reduceThreads) {
				try {
					reduceT.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}			

			System.out.println("--- Fin des reduce()");
			
			writerRes.close();
			networkRW.closeServer();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
