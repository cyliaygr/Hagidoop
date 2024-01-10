package interfaces;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;



public class ReaderImplKV implements Reader {

    protected boolean oLect = false;
    protected boolean oEcriture = false;
    protected FileWriter fichierEcriture;


	protected FileReader fichierLecture;
	protected BufferedReader buffer;

    private int index = 0;

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
			String[] kv = line.split(KV.SEPARATOR);
			return new KV(kv[0],kv[1]);
		} else {
			return null;
		}
	}
}