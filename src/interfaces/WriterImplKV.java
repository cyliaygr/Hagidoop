
package interfaces;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class WriterImplKV implements Writer {

    protected boolean oLect = false;
    protected boolean oEcriture = false;
    protected FileWriter fichierEcriture;

    public WriterImplKV(FileWriter fwname){
        this.fichierEcriture = fwname;
    }

/* OVERRIDE : fn write
 * param : record de type KV
 * 
 */
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
