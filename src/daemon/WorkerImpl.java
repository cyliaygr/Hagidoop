package daemon;

import java.io.File;
import java.io.FileWriter;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.Naming;
import java.net.InetAddress;


import interfaces.FileReaderWriter;
import interfaces.Map;
import interfaces.NetworkReaderWriter;
import interfaces.FileReaderWriter;
import interfaces.Map;
import interfaces.Reader;
import interfaces.Writer;
//import io.*;

public class WorkerImpl extends UnicastRemoteObject implements Worker, Runnable{

    Map mapp;
    FileReaderWriter reader;
    NetworkReaderWriter writer;
    Reader readerm;
    Writer writerm;
    
    String nomWorker = "Worker";

    // CONSTRUCTEUR : récupère le numéro du worker et le fichier à traiter
    public WorkerImpl(Map m, FileReaderWriter rw, Reader r, Writer w) throws RemoteException{
        this.reader = rw;
        this.mapp = m;
        this.readerm = r;
        this.writerm = w;
    }

    public String getNameWorker() throws RemoteException {
        return nomWorker; 
    }

    public void setNameWorker(String n) throws RemoteException {
        this.nomWorker = n;
    }

    public void runMap(Map m, FileReaderWriter reader, NetworkReaderWriter writer) throws RemoteException{
        // try{
        //     FileWriter filewriter = new FileWriter(new File(nomFichier));
        //     filewriter.write("Worker "+ numWorker+" bien lancé\n");
        // }catch(Exception e){

        // }


        // Création et lancement des Workers
        Thread t = new Thread(new WorkerImpl(mapp, reader, readerm, writerm));
        t.start();

        //************$$ SUREMENT ICI QUE FAUT OPENCLIENT OPENSERVER ETC (???)
    }

    public void run()  {
        writer.openClient();

        // LECTURE DE FRAGMENT  
        // Appel à la fonction open en précisant le mode (reading/writing)
        try {
            reader.setFname(reader.getFname());
            reader.open("R");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ECRITURE DES RESUTLATS (KV)
        try {
            reader.setFname(reader.getFname());
            reader.open("W");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //LANCE LE COUNT
        // lancement du map depuis une instanciation de Map.java
        mapp.map(readerm, writerm);


        //ENVOIE LES RESULTATS AU CLIENT


        writer.closeClient();
    }

    public void main(String[] args) {
        try {

            //On publie le worker sur le RMI, qu'on récupérera au niveau du client pour pouvoir lancer les runMap
            Registry registre = LocateRegistry.createRegistry(Integer.valueOf(args[0]));
            WorkerImpl serveurWork = new WorkerImpl(mapp, reader, readerm, writerm);

            String url = "//" + InetAddress.getLocalHost().getHostName() + ":" + args[0] + "/Worker";

            Naming.rebind(url, serveurWork);
            System.out.println("Serveur Worker" + serveurWork + " publié sur le RMI");


            } catch (Exception e) {
            e.printStackTrace();
        }
    }

  
    
}