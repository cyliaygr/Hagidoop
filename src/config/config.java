import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Config {
    private String fileConfig = Project.PATH + "/src/config/config_hagidoop.cfg";
    
    private String[] noms = new String[nbMachines];
    private String[] portsSocket = new String[nbMachines];
    private String[] portsRMI = new String[nbMachines];
    private String[] urls = new String[nbMachines];
    private int numWorker;
    private String fname;

    // Le constructeur lit le fichier de config
    public Config(){
        BufferedReader br;
		try {
            // Lecture du fichier de config
			br = new BufferedReader(new FileReader(fileConfig));
			String st; 
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
                    cptLigne++;					  
                }
            }
		    br.close();

            numWorker =  Math.min(Math.min(portsSocket.length, portsURL.length), noms.length) - 1;

            // URL
            // si le fichier de configuration est correct
            if (noms.length != 0 && ports.length == noms.length) {
                for (int i=0 ; i < nbMachines ; i++) {
                    urls[i] = "//" + noms[i] + ":" + portsRMI[i] + "/Worker";
                    // System.out.println(urls[i]);
                }
            } else {
                system.err.println("Fichier config invalide");
            }
									
		} catch (Exception e) {
            system.out.println("Fichier config invalide");
			e.printStackTrace();
		}
    }

    private void iValide(int i){
        if(i > numWorker){
            system.err.println("Machine n°" + (i) + " invalide, il y a " + numWorker + "machines.")
        }
    }

    public String getNom (int i){
        iValide(i);
        return noms[i]
    }

    public int getPortSocket(int i){
        iValide(i);
        return Integer.parseInt(portsSocket[i]);
    }

    public int getPortRMI(int i){
        iValide(i);
        return Integer.parseInt(portsRMI[i]);
    }

    public String getURL (int i){
        iValide(i);
        return urls[i]
    }

    public int getNbWorker (){
        return numWorker;
    }

    public String getFname(){
        return fname;
    }

}