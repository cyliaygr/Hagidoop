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
    FileReaderWriter readerwriter;
    Reader reader;
    Writer writer;
    
    String nomWorker = "Worker";
    //int numWorker, String nomFichier

    // CONSTRUCTEUR : récupère le numéro du worker et le fichier à traiter
    public WorkerImpl(Map m, FileReaderWriter rw, Reader r, Writer w) throws RemoteException{
        this.readerwriter = rw;
        this.mapp = m;
        this.reader = r;
        this.writer = w;
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

        Thread t = new Thread(new WorkerImpl(mapp, readerwriter, reader, writer));
        t.start();
    }

    public void run()  {
        try {
            readerwriter.setFname(readerwriter.getFname());
            readerwriter.open("R");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            readerwriter.setFname(readerwriter.getFname());
            readerwriter.open("W");
        } catch (Exception e) {
            e.printStackTrace();
        }

        
        mapp.map(reader, writer);

    }

    public void main(String[] args) {
        try {

            //On publie le worker sur le RMI, qu'on récupérera au niveau du client pour pouvoir faire les runMap
            Registry registre = LocateRegistry.createRegistry(Integer.valueOf(args[0]));
            WorkerImpl serveurWork = new WorkerImpl(mapp, readerwriter, reader, writer);

            String url = "//" + InetAddress.getLocalHost().getHostName() + ":" + args[0] + "/Worker";

            Naming.rebind(url, serveurWork);
            System.out.println("Serveur Worker" + serveurWork + " publié sur le RMI");


            } catch (Exception e) {
            e.printStackTrace();
        }
    }

  
    
}