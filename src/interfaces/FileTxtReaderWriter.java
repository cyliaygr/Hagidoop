
package interfaces;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


public class FileTxtReaderWriter implements FileReaderWriter {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected FileReader fichierLecture;
	protected BufferedReader buffer ;
	protected FileWriter fichierEcriture;
    private String nomFichier;
    private ArrayList<String> listeLigne = new ArrayList<>();
	public static final int FMT_TXT = 0;
	public static final int FMT_KV = 1;



    private int index = 0;
  
    
    // Gestion des threads : 
    protected boolean oLect = false;
    protected boolean oEcriture = false;
    
    public FileTxtReaderWriter(String name){
        nomFichier = name;
    	this.buffer = null;
    }

	public void open(String mode) {
		try {
            File fichier = new File(this.nomFichier);
            
            File parentDirs = fichier.getParentFile();
            if (parentDirs != null) {
                parentDirs.mkdirs();
            }
            
            if (mode == "R"){
            	System.out.print("Ouverture du fichier " + nomFichier + " en mode lecture -> ");
            	oLect = true;
                //On ouvre le fichie en lecture
                fichier.setReadable(true);
                fichierLecture = new FileReader(fichier);
                buffer = new BufferedReader(fichierLecture);

            } else {
            	System.out.println("Ouverture du fichier "+ nomFichier + " en mode ecriture.");
            	oEcriture = true;
            	fichier.setWritable(true);
            	fichierEcriture = new FileWriter(fichier, true);            	
            }
        } catch (Exception e) {
            System.err.println("Impossible d'ouvrir le fichier.");
            e.printStackTrace();
        }
	}

	public void close() {
		try {
			if(oEcriture){
				fichierEcriture.close();
				oEcriture = false;
			}
			if(oLect){
				//fichierLecture.close();
				oLect = false;
				buffer.close();
				index=0;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public long getIndex() {
		return index;
	}

	public String getFname() {
		return nomFichier;
	}

	public void setFname(String nom) {
		if(!oLect && !oEcriture){
			this.nomFichier = nom;
		}else {
			System.err.println("Fermer le fichier avant de modifier son nom.");
		}

	}
   
	public KV read() {	
		String line = null;
		if(oLect){
			try{
				line = buffer.readLine();
				this.index++;
				
			}catch (IOException e){
				e.printStackTrace();
			}
		} else {
			System.err.println("Message erreur de READ");
		}
		
		if(line != null){
			return new KV(""+index,line);
		} else {
			return null;
		}
	}

	public void write(KV record) {
		if (!oEcriture) {
			System.err.println("Opértation interdite");
			return;
		}
		try {
			fichierEcriture.write(record.v + "\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}
