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
	public static Worker listeWorker[];// liste des références aux workers dans le regsitre RMI
	
	private String fNameIn;
	private int nbMachines;
	private int nbWorker;

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
			String reduceDestFname = "data/" + nomExt[0] + "-red" + "." + nomExt[1];

			// Récupérer le format de fichier indiqué en argument
			if (args[1].equals("line")) {
				int ft = FileReaderWriter.FMT_TXT;
			} else {
				int ft = FileReaderWriter.FMT_KV;
			}

			// récupérer le nombre de machines
			nbWorker = Integer.parseInt(args[2]);

			// récupérer les références des objets Daemon distants
			listeWorker = new Worker[nbMachines];

			JobLauncher job = new JobLauncher(ft);
			MyMapReduce mr = new MyMapReduce();

		// ----------------------------------------------------
		//			LANCEMENT DES WORKERS
		// ----------------------------------------------------

			// Connexion RMI avec les Workers déjà créé par le script
			for (int i = 0 ; i < nbMachines ; i++) {
				System.out.println(config.getURL(i+1)); //(i+1) car la machine 0 est celle du client
				listeWorker[i]=(Worker) Naming.lookup(config.getURL(i+1));
			}

			// Lancement des tâches
			System.out.println("Lancement de startJob()");
			mapReduce.main(fNameIn);
			//job.startJob(mr,FileReaderWriter.FMT_TXT, fNameIn);


		} catch (Exception e) {
			e.printStackTrace();	
		}
	}
}
