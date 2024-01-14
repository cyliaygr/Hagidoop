package daemon;

import interfaces.*;
import java.io.InputStream;
import java.io.ObjectInputStream;
import application.*;



public class JobLauncher {

	Config config = new Config();

	int nbWorker;

	public static void startJob (MapReduce mr, int format, String fname) {
		
		String pathData = Project.PATH + "/data/";
		String[] nomExt = fname.split("\\.");	//Sépare le nom et l'extention 
		nbWorker = config.getNbWorker();

		// ----- LANCE LES RUNMAPS -----
		//Writer sur le réseau vers reduce
		NetworkReaderWriterImpl networkRW = new NetworkReaderWriterImpl(config.getHostName(0), config.getPortSocket(0));	  

		for (int i = 0; i < nbWorker ; i ++){
			//Reader sur le fragment i
			reader = new FileTxtReaderWriter(path + nomExt[0] + "-" + i + "." + nomExt[1]);	
			System.out.println("lancement runmap");

			listeWorker[i].runMap(mr, reader, networkRW);

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
		FileKVReaderWriter writerRes = new FileKVReaderWriter(path + nomExt[0] + "-res." + nomExt[1]);	
		writerRes.open("R");
		networkRW.openServeur();

		FileKVReaderWriter connexionRecu = networkRW.accept();
		connexionRecu.openServeur();
		mr.reduce(networkRW, writerRes);
		connexionRecu.closeServer();
		
		writerRes.closeServeur();
		networkRW.close();
			
	}
}
