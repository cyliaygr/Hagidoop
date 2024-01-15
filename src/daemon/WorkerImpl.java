package daemon;

import java.io.File;
import java.io.FileWriter;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.Naming;
import java.net.InetAddress;
import java.io.*;
import application.*;
import config.*;


import interfaces.*;
import hdfs.*;
import java.net.Socket;

//import io.*;

public class WorkerImpl extends UnicastRemoteObject implements Worker, Runnable{
    
    Config config = new Config();;
    NetworkReaderWriter networkRW;
    
    
    static private String fname;
    static private String workerName;
    static private int workerPort;
    static private int workerNum;
    
    
    public WorkerImpl(String fname, int num) throws RemoteException{
        this.fname     = fname; //Nom du fragment
        this.workerNum = num;   //Numéro du worker

        //----- Lecture du fragment -----
        HdfsServer hdfsS = new HdfsServer(workerNum);
        System.out.println("HDFS créé"); 
        hdfsS.HdfsRead(workerNum, fname.replace(".txt", "-"+workerNum+".txt"));
    }

    public void runMap(Map m, FileReaderWriter reader, NetworkReaderWriter writer) throws RemoteException{
        try {
            System.out.println("lancement du runMap");
            
            // ----- INIT -----
            // Initialise le reader sur le fragment
            reader.open("R");
            // Initialise le writer sur le NetworkRW connecté au reduce du startJob
            Thread.sleep(1000);//TEST
            writer.openClient();
            
            
            FileKVReaderWriter filewriter = new FileKVReaderWriter((Project.PATH+"/data/"+config.getFname().replace(".txt", "-res"+workerNum+".kv")));
            filewriter.open("W");
            
            
            // ----- RUNMAP -----
            m.map(reader, filewriter);
            System.out.println("runMap fini");
            
            // ----- FERMETURE -----
            filewriter.close();
            writer.closeClient();
            reader.close(); 
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void run()  {
        try {
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    //Parametres d'appel : Nom de la machine où s'execute le worker
    //                     Numéro du port RMI du worker
    //                     Numéro du worker
    //                     Nom du fragment
    public static void main(String[] args) {
        try {
            // ----------------------------------------------
            //      LECTURE DES ARGUMENTS
            // ----------------------------------------------
            workerName = args[0];                   //Nom de la machine où s'execute le worker
            workerPort = Integer.valueOf(args[1]);  //Numéro du port RMI du worker
            workerNum  = Integer.valueOf(args[2]);  //Numéro du worker
            fname      = args[3];                   //Nom du fragment
            
            //On publie le worker sur le RMI, qu'on récupérera au niveau du client pour pouvoir lancer les runMap
            try {
                Registry registre = LocateRegistry.createRegistry(workerNum);
                
                WorkerImpl serveurWork = new WorkerImpl(workerName,workerNum);
                
                //TODO              workerName ?                                     workerPort
                String url = "//" + workerName + ":" + workerPort + "/Worker-" + workerNum ;
                
                Naming.rebind(url, serveurWork);
                System.out.println("Serveur Worker" + serveurWork + " publié sur le RMI :"+ url);
                
                
            } catch (Exception e) {
                System.out.println("RMI déjà publié");
                e.printStackTrace();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
  
    
}