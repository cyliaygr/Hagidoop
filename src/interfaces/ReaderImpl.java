package interfaces;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class ReaderImpl implements Reader {

    protected boolean oLect = true;
    protected boolean oEcriture = false;
    protected FileWriter fichierEcriture;


	protected FileReader fichierLecture;
	protected BufferedReader buffer;
	String fname;

    private int index = 0;

	public ReaderImpl(String fname) {
		try {
			this.fname = fname;
			this.buffer = new BufferedReader(new FileReader(fname));
		} catch (Exception e) {
			e.printStackTrace();
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
}