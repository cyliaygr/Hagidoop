#!/bin/bash

# Envoyer le signal Ctrl+C à tous les processus des terminaux
osascript -e 'tell app "Terminal" to do script "kill -2 \$(pgrep -f rmiregistry)"'
osascript -e 'tell app "Terminal" to do script "kill -2 \$(pgrep -f java daemon.WorkerImpl)"'
osascript -e 'tell app "Terminal" to do script "kill -2 \$(pgrep -f java daemon.ClientHagidoop)"'

# Attendre un court instant pour permettre aux processus de se terminer
sleep 1

# Fermer tous les terminaux
osascript -e 'tell app "Terminal" to quit'

# Afficher un message de confirmation
echo "Terminaux fermés."