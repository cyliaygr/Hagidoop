# Terminal 1: Launch RMI registry
mate-terminal --window -e "/bin/bash -c \"rmiregistry 1000; exec /bin/bash\"" \

# Terminal 2: Launch Worker 1
--tab -e "/bin/bash -c \"java daemons/WorkerImpl 1001; exec /bin/bash\"" \

# Terminal 3: Launch Worker 2
--tab -e "/bin/bash -c \"java daemons/WorkerImpl 1002; exec /bin/bash\"" \

# Terminal 4: Launch Worker 3
--tab -e "/bin/bash -c \"java daemons/WorkerImpl 1003; exec /bin/bash\"" \

# Terminal 5: Launch ClientHagidoop
--tab -e "/bin/bash -c \"java daemon/ClientHagidoop filesample.txt txt 3; exec /bin/bash\""
