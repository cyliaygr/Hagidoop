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
     // liste des références aux workers dans le regsitre RMI
	public static Worker listeWorker[];
	

    private static void usage() {
		System.out.println("Utilisation : java HagidoopClient nomFichier format nbWorker");
	}

	// récupérer les emplacements indiqués dans le fichier de configuration
	private static String[] recupURL(int nbMachines) {
		String path = "/Users/yangourcylia/Documents/GitHub/Hagidoop/src/config/config_hagidoop.cfg";
		
		File file = new File(path);
		int cpt = 0;
		
		String[] ports = new String[nbMachines];
		String[] noms = new String[nbMachines];
		String[] urls = new String[nbMachines];
		  
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(file));
			String st; 
			while ((st = br.readLine()) != null) {
			  // si la ligne n'est pas un commentaire
			  if (!st.startsWith("#")) {
				  // noms des machines
				  if (cpt == 0) {
					  noms = st.split(",");
				  }
				  // ports RMI
				  if (cpt == 2) {
					  ports = st.split(",");
				  }
				  cpt++;					  
			  }
			}
			
			br.close();
			
			// si le fichier de configuration est correct
			if (noms.length != 0 && ports.length == noms.length) {
				for (int i=0 ; i < nbMachines ; i++) {
					urls[i] = "//" + noms[i] + ":" + ports[i] + "/Worker";
					// System.out.println(urls[i]);
				}
			} else {
				usage();
			}
									
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return urls;

	}

    public static void main (String args[]) {
		
		// Formats de fichiers utilisables
		String[] formats = {"txt","kv"};
		
		// Fichiers source / destination
		FileReaderWriter reader;
		FileReaderWriter writer;
        //Reader reader;
		//writer writer;
		
		// nombre de machines à utiliser
		int nbWorker;
		int nbMachines;
		
		// informations de format de fichier
		int ft;
		
		// liste des url correspondant aux démons du cluster
		String urlWorker[];
		
		try {
			// vérifier le bon usage du client
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
			String hdfsFname = args[0];
			nbMachines = Integer.parseInt(args[2]);
			
			// fichier HDFS destination  : ajout du suffixe "-res"
			// Nom du fichier traité avant application du reduce
			String[] nomExt = hdfsFname.split("\\.");
			String localFSDestFname = "data/" + nomExt[0] + "-res" + "." + nomExt[1];
			System.out.println(localFSDestFname);
			
			// fichier résultat du reduce : ajout du suffixe "-red"
			// Nom du fichier traité après application du reduce
			String reduceDestFname = "data/" + nomExt[0] + "-red" + "." + nomExt[1];
			System.out.println(reduceDestFname);

			// Récupérer le format de fichier indiqué en argument
			if (args[1].equals("line")) {
				ft = FileReaderWriter.FMT_TXT;
			} else {
				ft = FileReaderWriter.FMT_KV;
			}
			
			// récupérer le nombre de machines
			nbWorker = Integer.parseInt(args[2]);
			
			// récupérer les URLs depuis le fichier de configuration 
			urlWorker = recupURL(nbMachines);

			// récupérer les références des objets Daemon distants
			// à l'aide des url (déjà connues)
			listeWorker = new Worker[nbMachines];
			
			for (int i = 0 ; i < nbMachines ; i++) {
				System.out.println(urlWorker[i]);
				listeWorker[i]=(Worker) Naming.lookup(urlWorker[i]);
			}
			
			// création et définition des attributs de l'objet Job
			// on donne la liste des références aux Daemons à l'objet Job
			JobLauncher job = new JobLauncher(ft);
			
			// indiquer à job le nom et format du fichier à traiter
			//job.setInputFname(hdfsFname);  //FONCTION A RAJOUTER DANS JOBLAUNCHER
			//job.setInputFormat(ft);
			
			// création de l'objet MapReduce
			MyMapReduce mr = new MyMapReduce();
			
			// lancement des tâches
			System.out.println("Lancement du Job");
			job.startJob(mr,FileReaderWriter.FMT_TXT, hdfsFname);
			System.out.println("Fin du lancement du Job");
			
			// attendre que toutes les tâches soient terminées
			// via un sémaphore initialisé à 0
			//Semaphore attente = job.cb.getTachesFinies();
			//System.out.println("Attente du sémaphore");
			//attente.acquire();
			//System.out.println("Fin du sémaphore");
			
			// récupérer le fichier traité via HDFS
			//!!!!!!!!!!!!!!!!!!!!!!   HdfsClient.HdfsRead(hdfsFname, localFSDestFname, nbMachines);
			System.out.println("Lecture terminée");
			
			reader = new FileKVReaderWriter(localFSDestFname);
			writer = new FileKVReaderWriter(reduceDestFname);
			
		
			// TODO : A supprimer
			// appliquer reduce sur le résultat
			// reader : format kv ; writer : format kv
			System.out.println("Début du reduce");
			mr.reduce(reader, writer);
			System.out.println("Fin du reduce");
			
			reader.close();
			writer.close();
			//writer.close();
			
			System.exit(0);
						
	
		} catch (Exception e) {
			e.printStackTrace();	
	}
}
}
