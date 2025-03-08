#!/bin/bash

echo "🚀 Démarrage des services..."

mvn clean install -DskipTests
sleep 5

# 1️⃣ Lancer PostgreSQL avec Docker
echo "🐘 Démarrage de PostgreSQL..."
sudo docker-compose up -d
# lance la base de donné dans la console
# sudo docker exec -it container_red_psql psql -U postgres

# Attendre que PostgreSQL démarre (optionnel)
echo "⏳ Attente du démarrage de PostgreSQL..."
sleep 5

# 2️⃣ Nettoyer les tables
echo "🗑️ Suppression des données..."
sudo docker exec -it container_red_psql psql -U postgres -d mydatabase -c "TRUNCATE TABLE user_roles CASCADE;"
sudo docker exec -it container_red_psql psql -U postgres -d mydatabase -c "TRUNCATE TABLE role CASCADE;"


# 3️⃣ Générer le fichier WAR
echo "📦 Génération du WAR..."
mvn clean package -f Rest/pomforPAYARA.xml -DskipTests

# 4️⃣ Copier le WAR dans le répertoire de déploiement de Payara
echo "🚚 Déploiement du WAR sur Payara..."
cp /home/mxrsl/Bureau/TP1_I311/Rest/target/Rest-1.0-SNAPSHOT.war /home/mxrsl/Bureau/payara6/glassfish/domains/domain1/autodeploy/

# 5️⃣ Démarrer Payara
echo "🔥 Démarrage du serveur Payara..."
cd /home/mxrsl/Bureau/payara6/bin
./asadmin start-domain &

# 6️⃣ Attendre Payara avant de lancer le bot (optionnel).
echo "⏳ Attente du démarrage de Payara..."
sleep 20

# 7️⃣ Lancer le bot Discord
echo "🤖 Lancement du bot Discord..."
cd ~/Bureau/TP1_I311/BOT
mvn compile exec:java -Dexec.mainClass="com.example.bot.MainBot"


echo "✅ fin de tous les procesus"
cd /home/mxrsl/Bureau/payara6/bin
./asadmin stop-domain &