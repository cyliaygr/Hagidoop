# Terminal 1: Launch RMI registry
gnome-terminal --working-directory=/Users/yangourcylia/Documents/GitHub/Hagidoop/src -e "rmiregistry 1000"

# Terminal 2: Launch Worker 1
gnome-terminal --working-directory=/Users/yangourcylia/Documents/GitHub/Hagidoop/src -e "java daemon.WorkerImpl localhost 1000 1 filesample.txt"

# Terminal 3: Launch Worker 2
gnome-terminal --working-directory=/Users/yangourcylia/Documents/GitHub/Hagidoop/src -e "java daemon.WorkerImpl localhost 1000 2 filesample.txt"

# Terminal 4: Launch Worker 3
gnome-terminal --working-directory=/Users/yangourcylia/Documents/GitHub/Hagidoop/src -e "java daemon.WorkerImpl localhost 1000 3 filesample.txt"

# Terminal 5: Launch ClientHagidoop
gnome-terminal --working-directory=/Users/yangourcylia/Documents/GitHub/Hagidoop/src -e "java hdfs.HdfsClient write txt filesample.txt; java daemon.ClientHagidoop filesample.txt txt 3"
