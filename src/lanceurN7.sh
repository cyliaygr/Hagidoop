# Chemin au dossier contenant le projet
PATH="/mnt/nosave/dbrochet/hagidoop/src"

# Fichier de configuration
CONFIG_FILE="/config/config_hagidoop.cfg"

# Liste des machines (C205)
#declare -a MACHINES=("bulbizarre" "carapuce" "chenipan" "hypotrempe" "magicarpe" "melofee" "nidoran" "piafabec" "pikachu" "psykokwak")
# Ports differents pour permettre à plusieurs worker d'être sur la même machine
#declare -a PORTS=("4000" "4001" "4002" "4003" "4004" "4005" "4006" "4007" "4008" "4009")
#NBRMACHINES = 10

# Lecture des machines et ports RMI dans le fichier config 
mapfile -t MACHINES < <(awk -F',' '!/^#/ {print $1}' "${PATH}${CONFIG_FILE}")
mapfile -t PORTS < <(awk -F',' '!/^#/ {print $5}' "${PATH}${CONFIG_FILE}")
FILENAME=$(awk -F',' 'NR==7 {print $1}' "$CONFIG_FILE")
NBRMACHINES=${#MACHINES[@]}

# Supprime le fichier "nohup.out" s'il existe (fichier de stockage 
# des commandes lancées par nohup)
rm nohup.out

# Lance les workers (cMachine 0 est les client donc on commence à 1)
for ((i = 1; i < $NBRMACHINES; i++)); do
    nohup ssh "${MACHINES[$i]}" "cd ${PATH} && java daemon.WorkerImpl ${MACHINES[$i]} ${PORTS[$i] $i $FILENAME}" &
done
echo "Lancement des $NBRMACHINES workers fini"

sleep 1

# Lance le ClientHagidoop sur la machine 0
echo "Lancement du ClientHagidoop"
nohup ssh "${MACHINES[0]}" "cd ${PATH} && java hdfs.HdfsClient write line ../data/data.txt && sleep 5 && java application.MyMapReduce data.txt"
