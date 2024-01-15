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
	//public static Worker listeWorker[];// liste des références aux workers dans le regsitre RMI
	public static List<Worker> listeWorker = new ArrayList<>();




	static private int nbWorker;

	public static void startJob (MapReduce mr, int format, String fname) {
		
		String pathData = Project.PATH + "/data/";
		String[] nomExt = fname.split("\\.");	//Sépare le nom et l'extention 
		nbWorker = config.getNbWorker();

		// récupérer les références des objets Daemon distants
		//listeWorker = new WorkerImpl[nbWorker];

		// ----- LANCE LES RUNMAPS -----
		// Connexion RMI avec les Workers déjà créé par le script
		try {
			//for (int i = 0 ; i < nbWorker ; i++) {
			//	System.out.println(config.getURL(i)); //(i+1) car la machine 0 est celle du client
			//	listeWorker[i]= (Worker) Naming.lookup(config.getURL(i));
			//}

			for (int i = 0 ; i < nbWorker ; i++) {
				System.out.println("Worker "+i+" en "+config.getURL(i+1));
				Worker worker = (Worker) Naming.lookup(config.getURL(i+1));
				listeWorker.add(worker);
			}
			
			
			//Writer sur le réseau vers reduce
			System.out.println("Nom : "+config.getNom(0)+" port "+config.getPortSocket(0));
			NetworkReaderWriterImpl networkRW = new NetworkReaderWriterImpl(config.getNom(0),FileReaderWriter.FMT_KV, config.getPortSocket(0));	  
			
			// Ouvrez le ServerSocket dans un thread séparé
			//new Thread(() -> {
			//	networkRW.openServer();
			//}).start();
			
			//for (int i = 0; i < nbWorker ; i ++){
				//Reader sur le fragment i
				//FileReaderWriter reader = new FileTxtReaderWriter(pathData + nomExt[0] + "-" + i + "." + nomExt[1]);	
				//System.out.println("lancement runmap");
				
				//listeWorker[i].runMap(mr, reader, networkRW);
				
			//}
			//TODO : c'est moche
			// Créer et démarrer un thread pour chaque worker
			List<Thread> workerThreads = new ArrayList<>();
			int i = 1;
			for (Worker worker : listeWorker) {
				FileReaderWriter reader = new FileTxtReaderWriter(pathData + nomExt[0] + "-" + i + "." + nomExt[1]);	
				System.out.println("lancement runmap");
				// Appeler runMap sur chaque Worker
				//worker.runMap(mr, reader, networkRW);
				Thread workerThread = new Thread(() -> {
					// Appeler runMap sur chaque Worker
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
			
			// ----- ENVOIE LES FRAGMENTS -----
			// Attend 0,1s, pour etre sur que les hdfsServeur.read() sont bien ouvert  
			
			// long t1 = System.currentTimeMillis();
			// Thread.sleep(100); 
			// HdfsClient hdfsC = new HdfsClient(); 
			// hdfsC.HdfsWrite(FileReaderWriter.FMT_TXT, fname);
			// long t2 = System.currentTimeMillis();
			// System.out.println("HDFS : temps de fragmentation = "+(t2-t1)+"ms");
			
			// ----- LANCE LE REDUCE -----
			FileKVReaderWriter writerRes = new FileKVReaderWriter(pathData + nomExt[0] + "-res.kv");	
			writerRes.open("W");
			networkRW.openServer();
			
			List<Thread> reduceThreads = new ArrayList<>(); // Liste pour stocker les threads de réduction
			for(int y=0; y<nbWorker; y++){
				NetworkReaderWriter connexionRecu = networkRW.accept();

				// Création et démarrage d'un thread pour chaque méthode reduce
				final int yFinal = y; // Copie finale de la variable y
				Thread reduceThread = new Thread(() -> {
					mr.reduce(connexionRecu, writerRes);
					System.out.println("Reduce fini pour le worker " + yFinal);
					connexionRecu.closeServer();
				});

				reduceThreads.add(reduceThread);
    			reduceThread.start();

				
			}
			
			// Attendre la fin de tous les threads de réduction
			for (Thread reduceT : reduceThreads) {
				try {
					reduceT.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			// try {
			// 	Thread.sleep(1000);
			// } catch (Exception e) {
			// 	e.printStackTrace();
			// }
			
			
			writerRes.close();
			networkRW.closeServer();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
