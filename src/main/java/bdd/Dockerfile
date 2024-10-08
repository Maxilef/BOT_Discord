# Utilisez l'image officielle PostgreSQL comme image de base
FROM postgres:latest

# Définissez des variables d'environnement pour le nom d'utilisateur et le mot de passe PostgreSQL
ENV POSTGRES_USER=myuser
ENV POSTGRES_PASSWORD=mypassword
ENV POSTGRES_DB=mydatabase

# Exposez le port PostgreSQL par défaut
EXPOSE 5432

# Créez un répertoire pour stocker les scripts d'initialisation
RUN mkdir -p /docker-entrypoint-initdb.d

# Copiez le script d'initialisation dans le conteneur
COPY init.sql /docker-entrypoint-initdb.d/

# Assurez-vous que le script est exécutable
RUN chmod +x /docker-entrypoint-initdb.d/init.sql

# Démarrez PostgreSQL en écoutant sur toutes les interfaces
CMD ["postgres"]
