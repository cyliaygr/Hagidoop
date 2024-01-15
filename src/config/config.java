package config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

//  Cette classe permet de lire le fichier config et évite aux autres classes et leurs
// méthodes d'avoir beaucoup de paramètres d'entrés, seul le numéro de la machine suffit
public class Config {
    private String fileConfig = Project.PATH + "/src/config/config_hagidoop.cfg";
    
    private int numMaxWorker = 15;  //Peut être augenter
    private int numWorker;
    private String[] noms        = new String[numMaxWorker];
    private String[] portsSocket = new String[numMaxWorker];
    private String[] portsRMI    = new String[numMaxWorker];
    private String[] urls        = new String[numMaxWorker];
    private String fname;

    // Le constructeur lit le fichier de config
    public Config(){
        BufferedReader br;
		try {
            // Lecture du fichier de config
			br = new BufferedReader(new FileReader(fileConfig));
			String st; 
            int cptLigne = 0; 
			while ((st = br.readLine()) != null) {
                // si la ligne n'est pas un commentaire
                if (!st.startsWith("#")) {
                    // 1ere ligne = noms des machines
                    if (cptLigne == 0) {
                        noms = st.split(",");
                    }
                    // 2eme ligne = ports socket
                    if (cptLigne == 1) {
                        portsSocket = st.split(",");
                    }
                    // 3eme ligne = ports RMI
                    if (cptLigne == 2) {
                        portsRMI = st.split(",");
                    }
                    // 4eme ligne = nom fichier
                    if (cptLigne == 3) {
                        fname = st;
                    }
                    // 5eme ligne = nbr de worker
                    if (cptLigne == 4) {
                        numWorker = Integer.parseInt(st);
                    }
                    cptLigne++;					  
                }
            }
		    br.close();

            numMaxWorker =  Math.min(Math.min(portsSocket.length, portsRMI.length), noms.length) - 1;
            if (numWorker > numMaxWorker){
                System.err.println("Fichier config mauvais :Plus de worker demandé qu'il y en a de disponible");
            }

            // URL
            // si le fichier de configuration est correct
            for (int i=0 ; i < numMaxWorker ; i++) {
                urls[i] = "//" + noms[i] + ":" + portsRMI[i] + "/Worker-" + i;
                // System.out.println(urls[i]);
            }
 
									
		} catch (Exception e) {
            System.out.println("Fichier config invalide");
			e.printStackTrace();
		}
    }

    // Vérifie que la machines existe avant de récupérer ces informations
    private void iValide(int i){
        if(i > (numWorker+1)){
            System.err.println("Machine n°" + (i) + " invalide, il y a " + numWorker + "machines.");
        }
    }

    //Récupère le nom//l'adresse de la machine ("localhost", carapuce, 174.168.31.128)
    public String getNom (int i){
        iValide(i);
        return noms[i];
    }

    //Récupère le port pour la connexion socket du HDFS
    public int getPortSocket(int i){
        iValide(i);
        return Integer.parseInt(portsSocket[i]);
    }

    //Récupère le port RMI pour les connexion des workers
    public int getPortRMI(int i){
        iValide(i);
        return Integer.parseInt(portsRMI[i]);
    }

    //Récupère l'URL pour les connexion des workers (//localhost:4000/Worker-2)
    public String getURL (int i){
        iValide(i);
        return urls[i];
    }

    //Récupère le nombre maximum de worker que l'on peut utiliser
    public int getNbMaxWorker (){
        return numMaxWorker;
    }

    //Récupère le nombre de worker que l'on veut utiliser
    public int getNbWorker (){
        return numWorker;
    }

    //Récupère le nom du fichier à traiter
    public String getFname(){
        return fname;
    }

}