package daemon;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;

import application.MyMapReduce;
import interfaces.*;
import config.*;
import hdfs.HdfsClient;
import java.util.concurrent.Semaphore;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class ClientHagidoop {
    Config config = new Config();
	
	static private String fNameIn;
	static private int nbMachines;
	static private int nbWorker;

    private static void usage() {
		System.out.println("Utilisation : java HagidoopClient nomFichier format nbWorker");
	}

	

	// Liste des arguments : 0) nom du fichier and data à traiter (filesample.txt)
	//                       1) type du fichier (txt ou KV)
	//						 2) nbr de worker	
    public static void main (String args[]) {
		try {
		// ----------------------------------------------
		//      LECTURE DES ARGUMENTS
		// ----------------------------------------------
			// Verification du format des arguments
			String[] formats = {"txt", "kv" };
			if (args.length < 3) {
				usage();
				System.exit(1);
			} else {
				if (!Arrays.asList(formats).contains(args[1])) {
					usage();
					System.exit(1);
				}
			}

			// Nom du fichier sur lequel appliquer le traitement
			fNameIn = args[0];
			nbMachines = Integer.parseInt(args[2]);

			// fichier résultat du reduce : ajout du suffixe "-red"
			String[] nomExt = fNameIn.split("\\.");
			String reduceDestFname = "data/" + nomExt[0] + "-red" + "." + nomExt[1];

			// Récupérer le format de fichier indiqué en argument
			int ft;
			if (args[1].equals("line")) {
				ft = FileReaderWriter.FMT_TXT;
			} else {
				ft = FileReaderWriter.FMT_KV;
			}

			// récupérer le nombre de machines
			nbWorker = Integer.parseInt(args[2]);

			

			JobLauncher job = new JobLauncher();
			MyMapReduce mr = new MyMapReduce();

		// ----------------------------------------------------
		//			LANCEMENT DES WORKERS
		// ----------------------------------------------------

			

			// Lancement des tâches
			System.out.println("Lancement de startJob()");
			String[] mrArgs = new String[1];
			mrArgs[0] = fNameIn;
			mr.main(mrArgs);
			//job.startJob(mr,FileReaderWriter.FMT_TXT, fNameIn);


		} catch (Exception e) {
			e.printStackTrace();	
		}
	}
}
