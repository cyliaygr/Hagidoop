

# Terminal 1: Launch RMI registry
osascript -e 'tell app "Terminal" to do script "cd /Users/yangourcylia/Documents/GitHub/Hagidoop/src; rmiregistry 1000"'

# Terminal 2: Launch Worker 1
osascript -e 'tell app "Terminal" to do script "cd /Users/yangourcylia/Documents/GitHub/Hagidoop/src; java daemon.WorkerImpl localhost 1000 1 filesample.txt"'

# Terminal 3: Launch Worker 2
osascript -e 'tell app "Terminal" to do script "cd /Users/yangourcylia/Documents/GitHub/Hagidoop/src; java daemon.WorkerImpl localhost 1000 2 filesample.txt"'

# Terminal 4: Launch Worker 3
osascript -e 'tell app "Terminal" to do script "cd /Users/yangourcylia/Documents/GitHub/Hagidoop/src; java daemon.WorkerImpl localhost 1000 3 filesample.txt"'

# Terminal 5: Launch ClientHagidoop
osascript -e 'tell app "Terminal" to do script "cd /Users/yangourcylia/Documents/GitHub/Hagidoop/src;
                                                java hdfs.HdfsClient write txt filesample.txt;
                                                java daemon.ClientHagidoop filesample.txt txt 3"'



