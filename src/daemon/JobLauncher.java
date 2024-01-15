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
				System.out.println(config.getURL(i));
				Worker worker = (Worker) Naming.lookup(config.getURL(i));
				listeWorker.add(worker);
			}
			
			
			//Writer sur le réseau vers reduce
			NetworkReaderWriterImpl networkRW = new NetworkReaderWriterImpl(config.getNom(0),FileReaderWriter.FMT_KV, config.getPortSocket(0));	  
			
			// Ouvrez le ServerSocket dans un thread séparé
			new Thread(() -> {
				networkRW.openServer();
			}).start();
			
			//for (int i = 0; i < nbWorker ; i ++){
				//Reader sur le fragment i
				//FileReaderWriter reader = new FileTxtReaderWriter(pathData + nomExt[0] + "-" + i + "." + nomExt[1]);	
				//System.out.println("lancement runmap");
				
				//listeWorker[i].runMap(mr, reader, networkRW);
				
			//}

			for (Worker worker : listeWorker) {
				FileReaderWriter reader = new FileTxtReaderWriter(pathData + nomExt[0] + "-" + worker.getNbWorker() + "." + nomExt[1]);	
				System.out.println("lancement runmap");
				// Appeler runMap sur chaque Worker
				worker.runMap(mr, reader, networkRW);
			}
			
			// ----- ENVOIE LES FRAGMENTS -----
			// Attend 0,1s, pour etre sur que les hdfsServeur.read() sont bien ouvert  
			//Thread.sleep(100); 
			
			long t1 = System.currentTimeMillis();
			HdfsClient hdfsC = new HdfsClient(); 
			hdfsC.HdfsWrite(FileReaderWriter.FMT_TXT, fname);
			long t2 = System.currentTimeMillis();
			System.out.println("HDFS : temps de fragmentation = "+(t2-t1)+"ms");
			
			// ----- LANCE LE REDUCE -----
			FileKVReaderWriter writerRes = new FileKVReaderWriter(pathData + nomExt[0] + "-res." + nomExt[1]);	
			writerRes.open("W");
			networkRW.openServer();
			
			NetworkReaderWriter connexionRecu = networkRW.accept();
			connexionRecu.openServer();
			mr.reduce(connexionRecu, writerRes);
			connexionRecu.closeServer();
			
			writerRes.close();
			networkRW.closeServer();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
