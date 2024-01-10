package daemon;

import interfaces.MapReduce;
import interfaces.FileKVReaderWriter;
import interfaces.FileTxtReaderWriter;
import interfaces.FileReaderWriter;
import interfaces.Map;




public class JobLauncher {


	
	static int nbrWorker = 3;
	static int nbFragments = 3;
	
	
	// static String[] argsFragments = {"write", "txt", nomFichier};

	public static void startJob (MapReduce mr, int format, String fname) {

	String nomFichier = "filesample.txt";
	

	Worker[] listeWorker;
	Map m;


	// RECUPÉRER LES FRAGMENTS (FICHIERS)
	// 	hdfs.HdfsClient.main(argsFragments);

	// TRAITEMENT SUR CHAQUE FRAGMENT
	try {
		if (nbFragments == 1) {
			FileReaderWriter reader;
			NetworkReaderWriter writer;

			listeWorker[0].runMap(m, reader, writer); //initialiser la liste dans le client
		} else {
			for (int i = 0; i < nbFragments ; i ++){
				String fichierdst = "filesample-res.txt";

				FileReaderWriter reader;
				NetworkReaderWriter writer;

				listeWorker[i%nbrWorker].runMap(m, reader, writer); //initialiser la liste dans le client

			}
		}
	} catch (Exception e) {
		e.printStackTrace();
	}



		
	// CREER LEW WORKER
	 	try {
             Thread t[] = new Thread[nbrWorker];

             for (int i = 0; i < nbrWorker; i++) {
	 			String newName = nomFichier.replace(".txt", "-"+i+".txt");
                 t[i] = new WorkerImpl(i, newName);
                 t[i].start();
             }

         } catch (Exception e) {
             e.printStackTrace();
         }



	// 	LANCER LE REDUCE

	// 	LANCER LES RUNMAP SUR LES WORKER

			
	}
}
