# ClassFlow - Client Lourd

Application de bureau JavaFX pour la gestion de classes, d'élèves et de remarques, connectée au backend ClassFlow.

## Prérequis

- Java 21 ou supérieur
- Maven 3.6+
- Le backend ClassFlow doit être démarré sur `http://localhost:8080`

## Lancer le backend

Avant de lancer le client lourd, démarrez le backend :

```bash
cd ClassFlow-back
mvn spring-boot:run
```

Le backend démarre sur http://localhost:8080

## Lancer le client lourd

### Option 1 : Avec Maven (recommandé pour JavaFX)

```bash
cd ClassFlowClientLourd
mvn javafx:run
```

### Option 2 : Compiler et lancer le JAR

```bash
cd ClassFlowClientLourd
mvn clean package
java -jar target/classflow-client-lourd-1.0.0-SNAPSHOT.jar
```

⚠️ **Note** : L'option 2 nécessite que JavaFX soit installé séparément sur votre système.

## Fonctionnalités implémentées

### 1. Authentification
- Connexion avec Basic Auth
- Comptes de test : `demo1/demo1`, `demo2/demo2`, `demo3/demo3`

### 2. Gestion des classes
- Liste des classes avec compteur d'élèves
- Créer une nouvelle classe
- Supprimer une classe
- Ouvrir une classe pour voir ses détails

### 3. Gestion des élèves
- Lister les élèves d'une classe
- Ajouter un élève (nom et prénom)
- Modifier un élève
- Supprimer un élève

### 4. Gestion des remarques
- Visualiser les remarques d'un élève sélectionné
- Ajouter une remarque à un élève

## Architecture technique

- **JavaFX 21** pour l'interface graphique
- **Java HttpClient** pour la communication REST avec le backend
- **Jackson** pour la sérialisation/désérialisation JSON
- DTOs alignés sur ceux du ClassFlowCore

## Captures d'écran

1. **Écran de login** avec authentification Basic Auth
2. **Liste des classes** avec actions CRUD
3. **Détail d'une classe** avec gestion des élèves et remarques
