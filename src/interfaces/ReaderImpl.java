package interfaces;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class ReaderImpl implements Reader {

    protected boolean oLect = false;
    protected boolean oEcriture = false;
    protected FileWriter fichierEcriture;


	protected FileReader fichierLecture;
	protected BufferedReader buffer;

    private int index = 0;

	public ReaderImpl() {

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
}