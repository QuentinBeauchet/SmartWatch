# The Application
### Icon and Name
![image](https://user-images.githubusercontent.com/74865653/206781619-63024322-1223-4df7-8f79-1dff39ae638a.png)

### Main menu
![image](https://user-images.githubusercontent.com/74865653/206781682-71d2d99d-4f77-4af7-b841-531194db7f43.png)
![image](https://user-images.githubusercontent.com/74865653/206781737-ffb53dfe-59c2-452a-8d47-8045a8015a7b.png)

### Adding events manually
![image](https://user-images.githubusercontent.com/74865653/206781872-6c9bfa9b-bd9d-4e19-8596-36bc4ba29ad5.png)

### Setting the location requests interval
![image](https://user-images.githubusercontent.com/74865653/206781964-c4dc1f02-a026-48ae-a0c8-1b930c506838.png)

### Notification to keeps the application running in the background
![image](https://user-images.githubusercontent.com/74865653/206782296-08d16389-d885-4f01-88ca-0885de4093b9.png)

# The DashBoard

The DashBoard is hosted on Railway at https://smartwatch-production.up.railway.app/

![image](https://user-images.githubusercontent.com/74865653/206782557-fb0a53c8-e328-40f2-af84-2150a8916358.png)

# The API

The API is hosted on Railway at https://smartwatch-production.up.railway.app/api

```js
/**************Events**************/

app.get("/api/events", DB.getAllEvents);

app.post("/api/events/add", DB.addEvent);

app.get("/api/events/:id/delete", DB.deleteEvent);

/*************Users**************/

app.post("/api/connect", DB.connect);

app.get("/api/users", DB.getAllUsers);

app.get("/api/users/:id/delete", DB.deleteUser);

/**************Types**************/

app.get("/api/types", DB.getAllEventTypes);

app.post("/api/types/add", DB.addEventType);

app.get("/api/types/:id/delete", DB.deleteEventType);
```

# Rapport

Nous avons utiliser l'emulateur Wear Os Small Round API 30 sur Android Studio.

### Alimentation :
La montre fonctionne sur batterie et on la charge par induction. Notre application n’est pas prévue pour une utilisation prolongée sans recharger la montre car celle-ci envoi des données vers notre API et récupère sa position de manière constante ce qui réduit considérablement la durée de la batterie. Nous avons fait le choix de ne pas passer par le téléphone pour que la montre puisse être utilisée indépendamment de celui-ci.

### Mémoire :
Nous stockons seulement les layouts et quelques variables ce qui fait que l’application nécessite peu de ressources.

### CPU :
Notre application nécessite de rester active en fond pour envoyer des informations à l’api à intervalles régulières mais cela n’impacte pas négativement l’utilisation de la montre.

### Modules de Communications :
Nous n’utilisons pas de communications Bluetooth entre la montre et le téléphone car nous voulions que celle-ci reste indépendante et donc elle effectue elle-même les requêtes http GET et POST vers notre API.

### Capteurs : 
Le seul capteur de la montre que nous utilisons et la géolocalisation, celui-ci étant suffisant pour le type d’application que nous voulions créer.

### Actionneurs :
L’application permet à l’utilisateur de démarrer la géolocalisation et de la stopper, il peut aussi régler la fréquence de celle-ci. De plus il est aussi possible de d’envoyer des évènements manuellement ce qui permet à l’utilisateur de signaler des éléments intéressant lors de son trajet en envoyant leurs types, leurs positions et leurs dates.

### Environnement de Développement :
Nous avons utilisé Android Studio et nous avons testé notre application depuis l’émulateur Wear Os Small Round API 30 sous Windows/linux et mac.

### Limitations Logiciels :
Evidement notre application nécessite l’accès au réseau et une montre pouvant utiliser la géolocalisation.

### Objectif :
Le but de notre application est de permettre à un utilisateur de notifier les autres utilisateurs d’évènements qu’il considère importants lors de son trajet. Pour cela notre application envoi a 
Intervalle régulière sa position vers une API distance qui se charge d’agréger les données de tous
les utilisateurs dans une base de donnée et les affiche dans un Dashboard. Certains des types d’événements sont des bouchons, des travaux sur le bord de la route, des déchets …
L’utilisateur peut choisir la fréquence a laquelle sa position est envoyé automatiquement vers
l’API et de stopper cet envoi. L’application fonctionne en arrière-plan grâce a une notification 
permanente.

### Données collectées :
- La géolocalisation de la montre.
- L'id unique de la montre
- L’heure et la data de chaque évènement.

#### Commentaire :
L’utilisation d’un id unique peut poser un problème car si celui est récupéré par
un pirate il permettrais à celui-ci d’identifier la montre en permanence car ce
numéro est inchangeable. Nous avons opté pour cette approche qui nous évitais
de devoir réaliser un système d’identifiants de connexion avec pseudo et
mot de passe.

### Traitement des données collectées sur la cible :
- On affiche les évènements dans une carte interactive.
- On affiche les types évènements dans un histogramme.
- On affiche les types évènements dans un digramme circulaire.

#### Commentaire :
Toutes ces données sont visibles depuis notre Dashboard.

### Transmission des données collectées à un système distant :
- Ces données sont transmises en utilisant le protocole http vers l’API qui se charge de les stocker dans une base de données SQL.

### Stockage des données collectées sur un système distant :
- Elle sont stockées dans une base de données SQL dans 3 tables différentes, une pour les utilisateurs, une pour les types d’évènements et une pour les évènements eux même. Un utilisateur et un type peuvent avoir plusieurs événements. 

### Contraintes et Solutions :
Du fait que la fonction principale de notre application est la géolocalisation, on se doit d’envoyer des   informations vers l’API à intervalles réguliers. Notre application étant une démonstration nous n’avons pas pris en compte les contraintes de bande passante et de consommation d’Energie.

Pour améliorer ses deux points il serait possible de stocker les données en local dans la montre et de les envoyer de façon journalière.

# The DataBase
![image](https://user-images.githubusercontent.com/74865653/206780975-03287bad-83a3-4e1a-a1fb-87d662f74c5a.png)

# How to start the API Localy :

## Step 1

Start `Docker` and then run:

```bash
cd server
npm i
npm run docker:build  #Build images and containers
npm start
```

You should have the address of the api displayed in the console. Otherwise you will need to run `ipconfig` and look for your IPv4 address.

## Step 2

To be sure the server is running open the address you got in the previous step in your browser (it should be something like http://192.168.0.134:3000) and you should see the dashboard.

## Step 3

Open the folder `application` with `Android Studio` and then open `app/build.gradle`. You might need to change the address in the line:

> buildConfigField "String", "API_URL", "\\"https<span>://smartwatch-production.up.railway.app/api\\""

with the ip address you got in step 1.

## Step 4

You can run the application from `Android Studio` in the emulator and everything should be working as intented.

## TroubleShooting

> ERROR 2002 (HY000): Can't connect to local server through socket '/run/mysqld/mysqld.sock' (2)

This error mean that the mariadb container is not ready yet and you should wait about 10s.

#

> error during connect: this error may indicate that the docker daemon is not running: Get "http://%2F%2F.%2Fpipe%2Fdocker_engine/v1.24/containers/json?all=1&filters=%7B%22label%22%3A%7B%22com.docker.compose.project%3Dserver%22%3Atrue%7D%7D": open //./pipe/docker_engine: The system cannot find the file specified.

> Unable to connect to the database: SequelizeConnectionRefusedError: connect ECONNREFUSED 127.0.0.1:3306

These errors mean that docker in not running, to fix it simply start it.

#
