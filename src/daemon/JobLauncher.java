package daemon;

import interfaces.*;
import java.io.InputStream;
import java.io.ObjectInputStream;
import application.*;



public class JobLauncher {

	static MapReduce mapp;
	// infos sur le fichier à traiter
	public int format;
	public static String fname;

	// Nombre de worker pour cette application
	public static int nbrWorker;

	// liste des workers de l'application 
	private static Worker[] listeWorker;
	
	// CONSTRUCTEUR (utilisée dans le ClientHagidoop)
	public JobLauncher (int f) {
		this.format = f;
		this.fname = null;
		this.listeWorker = ClientHagidoop.listeWorker;		
		this.nbrWorker = listeWorker.length;
	
	}

	public static void startJob (MapReduce mr, int format, String fname) {

	// chemin vers les fragments à traiter
	String path = "/Users/yangourcylia/Documents/GitHub/Hagidoop/data/";

	// fragment src = fname?????????????? <------- erreur peut etre la 
	//String fichiersrc;
	FileReaderWriter reader = null;	    // initiailisation en dehors du try pour assurer 
										//une valeur au reader même en dehors du try
	
	// fragment destination
	String fichierdest;
	NetworkReaderWriter writer = null;

	Reader readerm = null;
	Writer writerm = null;

	String[] nomExt = fname.split("\\.");

	int nbFragments = 3;
	
	

	// RECUPÉRER LES FRAGMENTS (FICHIERS)
	// hdfs.HdfsClient.main(argsFragments)
	fichierdest = path + nomExt[0] + "-res" + "." + nomExt[1];
	writer = new NetworkReaderWriterImpl(4003);
	writer.openServer();
	writerm = new WriterImpl(fichierdest);
	readerm = new ReaderImpl(fname);

	// lancement du reduce
	mr.reduce(writer, writerm);
	
	// TRAITEMENT SUR CHAQUE FRAGMENT
	try {
		// CREE ET ACTIVE LES WORKERS
		if (nbFragments == 1) {
			
			// On rajoute _1 au nom puisqu'un seul fragment
			fname = path + nomExt[0] + "-1" + "." + nomExt[1];
			nomExt = fname.split("\\.");

			// le fichier dest reprend le nom du fichier src en ajoutant -res
			fichierdest = fname + "-res";
	
			writer = new NetworkReaderWriterImpl(4003);
			System.out.println("lancement runmap");
			listeWorker[0].runMap(mr, reader, writer); //initialiser la liste dans le client
			
		} else {
			for (int i = 0; i < nbFragments ; i ++){

				// format des noms de fragments : "<nom fichier HDFS>_< n° fragment >"
				fname = path + nomExt[0] + "-" + i + "." + nomExt[1];

				// le fichier dest reprend le nom du fichier src en ajoutant -res
				fichierdest = path + nomExt[0] + "_" + i + "-res" + "." + nomExt[1];

				reader = new FileTxtReaderWriter(fname);
				writer = new NetworkReaderWriterImpl(4003);
				System.out.println("lancement runmap");

				listeWorker[i%nbrWorker].runMap(mr, reader, writer); //initialiser la liste dans le client

			}
		}

	} catch (Exception e) {
		e.printStackTrace();
	}

	// lancer le reduce qui récupère les résultats des map (sur une connexion réseau avec les map) et les traite.

	
	// sujet : startjob lance les run en appelant runmap
	// startjob lance le reduce ==> comment? en appelant quoi? 

	// 2 map et 2 reduce ou modifier ceux de MyMapReduce ?? 

	// où openclient openserver dcp? (quel fichier, quel méthode)


	// attendre terminaison des map (cmt?) pour que reduce envoie resultat sur fichierdest.

	
		

	//writer = new NetworkReaderWriterImpl();
	//writer.openServer();


	

	//recoit de tous
	System.out.println(" avant accept");
	NetworkReaderWriterImpl networkRW = (NetworkReaderWriterImpl) writer.accept();

	try {
	InputStream is = networkRW.asock.getInputStream();
	ObjectInputStream ois = new ObjectInputStream(is);
	
	

	int cptWorkerFini = 0;
	KV kvRecu = null;

	fname =  path + nomExt[0] + "-res.kv";
	FileKVReaderWriter writerKV = new FileKVReaderWriter(fname);
	writerKV.open("W");
				
	while(cptWorkerFini < nbrWorker){
		kvRecu = (KV)ois.readObject();
		
		if(kvRecu.equals("fin de resultat")){
			cptWorkerFini += 1;
		}
		else{
			//Ajoute au fichier final
			// reduce recoit tous les fichier res (de chaque fragment) et les écrit dans un unique fichier
			writerKV.write(kvRecu);
			
		}
	}
	System.out.println("fermture seveur");
	writerKV.close();
	ois.close();
	is.close();
	writer.closeServer();
	//regroupe

} catch (Exception e) {
	System.out.println("ERREURRRRRRR");
	e.printStackTrace();
} 



	
			
	}
}
