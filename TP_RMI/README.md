# TP RMI

Etudiant : Wajdi Ben Abdeljelil  
Classe : IGL3

Travaux pratiques Java RMI couvrant :

- la fabrique d'objets distants
- les callbacks RMI
- le passage par copie avec `Serializable`
- le passage par reference avec `Remote`
- un gestionnaire de taches distribue integre

L'implementation se trouve dans `src/main/java` et est organisee en :

- `interfaces` : interfaces distantes partagees et DTOs serializables
- `server` : implementations des objets distants et demarrage du serveur
- `client` : clients de demonstration pour chaque partie du TP

## Prerequis

- Java 11 ou plus recent
- PowerShell ou un autre terminal

Le fichier `pom.xml` est inclus, mais le projet peut etre compile directement avec `javac`.  
L'implementation actuelle ne necessite pas de lancer `rmiregistry` manuellement, car le serveur cree ou reutilise lui-meme le registry sur le port `1099`.

## Points d'entree du projet

- Serveur : `server.MainServer`
- Lanceur client : `client.MainClient`

Le lanceur client peut executer :

- `all`
- `shapes`
- `vectors`
- `tasks`
- `game`

## Compilation

Depuis la racine du projet :

```powershell
cd C:\Users\MI\Desktop\TP_RMI
New-Item -ItemType Directory -Force target\classes | Out-Null
javac -d target/classes (Get-ChildItem -Path src/main/java -Recurse -Filter *.java | ForEach-Object { $_.FullName })
```

## Execution

Ouvrir deux terminaux.

### Terminal 1 : demarrer le serveur

```powershell
cd C:\Users\MI\Desktop\TP_RMI
java -cp target/classes server.MainServer
```

Sortie attendue au demarrage :

```text
RMI registry ready on port 1099
Bound services: ShapeFactory, GameServer, VectorService, CounterService, TaskManager
```

Laisser ce terminal ouvert.

### Terminal 2 : executer les demonstrations client

Executer toutes les parties :

```powershell
cd C:\Users\MI\Desktop\TP_RMI
java -cp target/classes client.MainClient localhost all
```

Executer une partie precise :

```powershell
java -cp target/classes client.MainClient localhost shapes
java -cp target/classes client.MainClient localhost vectors
java -cp target/classes client.MainClient localhost tasks
java -cp target/classes client.MainClient localhost game
```

## Tests realises par chaque demonstration

### `shapes`

Teste le patron de fabrique distante.

- fait un `lookup` sur `ShapeFactory`
- affiche les types de formes disponibles
- cree un cercle, un rectangle, un triangle et un pentagone
- affiche la description, l'aire et le perimetre de chaque forme

### `vectors`

Teste les deux modes de passage de parametres.

- `Vector2D` est passe par copie car il implemente `Serializable`
- `SharedCounter` est passe par reference car c'est un objet distant
- verifie que les mises a jour directes via le stub du compteur sont visibles immediatement

### `tasks`

Teste le gestionnaire de taches distribue.

- abonne un client callback
- cree des taches a partir de `TaskData` serialisable
- recoit les notifications de creation et de fin de tache
- met a jour la progression via des objets distants `TaskHandle`
- affiche la liste des taches en attente avant et apres completion

### `game`

Teste les callbacks RMI avec plusieurs clients.

- simule 3 joueurs dans des threads distincts
- chaque joueur enregistre un objet callback
- chaque joueur envoie 2 actions aleatoires
- les joueurs recoivent les notifications des autres
- la suppression des clients deconnectes est geree automatiquement

## Configuration IntelliJ

Pour executer le projet depuis IntelliJ :

1. Executer `server.MainServer`
2. Executer `client.MainClient`
3. Definir les arguments du programme client, par exemple :

```text
localhost all
```

Ou utiliser :

```text
localhost shapes
localhost vectors
localhost tasks
localhost game
```

## Problemes frequents

### `java.rmi.ConnectException: Connection refused`

Cause :

- le serveur n'est pas demarre
- le port `1099` n'est pas en ecoute

Correction :

1. Demarrer `server.MainServer` en premier
2. Le laisser actif pendant le lancement du client
3. Verifier le port si necessaire :

```powershell
netstat -ano | findstr :1099
```

Une entree `LISTENING` doit apparaitre.

### `java.rmi.NotBoundException`

Cause :

- le client a fait un `lookup` avant que le service soit enregistre par le serveur

Correction :

- redemarrer le serveur puis relancer le client

### `javac` not found

Cause :

- Java n'est pas installe correctement ou `PATH` n'est pas configure

Correction :

- verifier avec :

```powershell
java -version
javac -version
```

## Principaux fichiers sources

- `src/main/java/server/MainServer.java`
- `src/main/java/client/MainClient.java`
- `src/main/java/server/shapes/ShapeFactoryImpl.java`
- `src/main/java/server/game/GameServerImpl.java`
- `src/main/java/server/vector/VectorServiceImpl.java`
- `src/main/java/server/counter/CounterServiceImpl.java`
- `src/main/java/server/task/TaskManagerImpl.java`
