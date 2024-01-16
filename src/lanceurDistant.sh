#!/bin/bash

# Lire les informations depuis le fichier de configuration
machines=$(awk -F',' 'NR==2 {print $0}' config/config_hagidoopl.cfg)
hdfs_ports=$(awk -F',' 'NR==4 {print $0}' config/config_hagidoopl.cfg)
rmi_ports=$(awk -F',' 'NR==6 {print $0}' config/config_hagidoopl.cfg)
filename=$(awk -F',' 'NR==8 {print $0}' config/config_hagidoopl.cfg)
num_workers=$(awk -F',' 'NR==10 {print $0}' config/config_hagidoopl.cfg)
fragment_size=$(awk -F',' 'NR==12 {print $0}' config/config_hagidoopl.cfg)

# Séparer les informations en tableaux
IFS=',' read -ra machines_arr <<< "$machines"
IFS=',' read -ra hdfs_ports_arr <<< "$hdfs_ports"
IFS=',' read -ra rmi_ports_arr <<< "$rmi_ports"

username="cyangour"
chemin="/home/cyangour/2A/Hagidoop"


# Terminal 1: Launch RMI registry on the first machine
ssh ${username}@${machines_arr[0]} "cd ${chemin}/src && rmiregistry ${rmi_ports_arr[0]}"

# Launch Workers on respective machines
for ((i=1; i<=$num_workers; i++)); do
    worker_num=$((i+1))
    hdfs_port=${hdfs_ports_arr[i]}
    rmi_port=${rmi_ports_arr[i]}

    # Launch Worker on the corresponding machine
   
    ssh ${username}@${machines_arr[i]} "cd ${chemin}/src && java daemon.WorkerImpl localhost $rmi_port $worker_num $filename"
done

# Terminal 5: Launch ClientHagidoop on the first machine
ssh ${username}@${machines_arr[num_workers + 1]} "cd ${chemin}/src &&
                       java hdfs.HdfsClient write txt $filename &&
                       java daemon.ClientHagidoop $filename txt $num_workers"
