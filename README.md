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

### Développement avec Maven

```bash
cd ClassFlowClientLourd
mvn javafx:run
```

### Compiler le projet

```bash
cd ClassFlowClientLourd
mvn clean package
```

Cela créera un JAR avec toutes les dépendances dans `target/classflow-client-lourd-1.1.0.jar`.

### Lancer le JAR compilé

Après compilation, vous pouvez lancer l'application avec :

**Windows :**
```bash
run.bat
```

Ou directement avec :
```bash
java -jar target\classflow-client-lourd-1.1.0.jar
```

**Linux/Mac :**
```bash
java -jar target/classflow-client-lourd-1.1.0.jar
```

## Fonctionnalités implémentées

### 1. Authentification
- Connexion avec Basic Auth
- Comptes de test : `demo1/demo1`, `demo2/demo2`, `demo3/demo3`

### 2. Gestion des classes
- Liste des classes avec compteur d'élèves
- Créer une nouvelle classe
- Supprimer une classe
- Ouvrir une classe pour voir ses détails
- Interface à onglets pour organiser les fonctionnalités

### 3. Gestion des élèves (onglet 👥 Élèves)
- Lister les élèves d'une classe
- Ajouter un élève (nom et prénom)
- Modifier un élève
- Supprimer un élève
- Visualiser les remarques d'un élève sélectionné
- Ajouter une remarque à un élève

### 4. Plan de classe interactif (onglet 🗺️ Plan de classe)
- Visualisation du plan de classe avec grille 8×6
- **Drag & drop** : Déplacer les tables en les glissant avec la souris
- **Positions persistées** : Les positions sont sauvegardées automatiquement
- **Échange d'élèves** : Cliquer sur 2 élèves pour les échanger de place
- Ajouter/supprimer des tables
- Les élèves suivent automatiquement leur table lors des déplacements

### 5. Roulette aléatoire (onglet 🎲 Roulette)
- Tirage au sort d'un élève avec animation type roulette
- Animation progressive avec ralentissement
- Effet visuel (pulse) sur l'élève sélectionné

### 6. Gestion des groupes (onglet 👨‍👩‍👧‍👦 Groupes)
- **Groupes aléatoires** : Créer des groupes automatiquement en spécifiant le nombre
- **Groupes manuels** : Créer des groupes personnalisés
- **Noms de groupes éditables** : Double-clic ou bouton ✏️ pour renommer
- **Drag & drop** : Glisser des élèves dans les groupes
- Ajouter/retirer des élèves d'un groupe
- Supprimer un groupe avec confirmation
- Affichage visuel avec compteur d'élèves par groupe

## Architecture technique

- **JavaFX 21** pour l'interface graphique
- **Java HttpClient** pour la communication REST avec le backend
- **Jackson** pour la sérialisation/désérialisation JSON
- **Animations JavaFX** : Timeline pour la roulette, transitions pour les effets visuels
- **Grid Layout** : Système de grille 8×6 pour le plan de classe
- **TabPane** : Organisation en onglets pour une navigation fluide
- DTOs optimisés pour le plan de classe (ClassRoomPlanDTO, TablePlanDTO)

## Structure du code

### Views (vues JavaFX)
- `LoginView` : Écran de connexion
- `ClassRoomListView` : Liste des classes
- `ClassRoomContainerView` : Conteneur avec onglets
- `ClassRoomDetailView` : Gestion des élèves et remarques
- `ClassRoomPlanView` : Plan de classe interactif
- `RandomStudentView` : Roulette de tirage au sort
- `GroupManagementView` : Gestion des groupes

### DTOs
- DTOs standards : `ClassRoomDTO`, `EleveDTO`, `RemarqueDTO`, `GroupeDTO`
- DTOs plan de classe : `ClassRoomPlanDTO`, `TablePlanDTO`
- DTOs requêtes : `TablePositionUpdateRequest`, `EleveSwapRequest`, `GroupeCreateRequest`

### Services
- `ClassFlowApiService` : Client HTTP pour toutes les opérations REST

## Captures d'écran

1. **Écran de login** avec authentification Basic Auth
2. **Liste des classes** avec actions CRUD
3. **Onglet Élèves** : Gestion des élèves et remarques
4. **Onglet Plan de classe** : Vue interactive avec drag & drop
5. **Onglet Roulette** : Tirage au sort animé
6. **Onglet Groupes** : Création et gestion de groupes
