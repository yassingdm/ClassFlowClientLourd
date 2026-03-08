# ClassFlow - Client Lourd

Application de bureau JavaFX moderne pour la gestion de classes, d'élèves et de remarques, connectée au backend ClassFlow.

## 🚀 Utilisation rapide (Release)

**Pour utiliser l'application sans compilation :**

1. Téléchargez les fichiers de release :
   - `classflow-client-lourd-X.X.X.jar`
   - `ClassFlow.bat` (Windows)

2. Placez les deux fichiers dans le même dossier

3. Double-cliquez sur `ClassFlow.bat` ou exécutez-le depuis le terminal

**Prérequis :** Java 21 ou supérieur installé sur votre système

---

## 📋 Prérequis pour le développement

- Java 21 ou supérieur
- Maven 3.6+
- Le backend ClassFlow doit être accessible (localhost ou Render)

## Lancer le backend (développement)

Avant de lancer le client lourd, démarrez le backend :

```bash
cd ClassFlow-back
mvn spring-boot:run
```

Le backend démarre sur http://localhost:8080

## 💻 Développement

### Lancer avec Maven

```bash
cd ClassFlowClientLourd
mvn javafx:run
```

### Compiler le projet

```bash
cd ClassFlowClientLourd
mvn clean package
```

Cela créera un JAR avec toutes les dépendances dans `target/classflow-client-lourd-X.X.X.jar`.

### Lancer le JAR compilé

**Windows :**
```bash
ClassFlow.bat
```

**Ou directement :**
```bash
java -jar target\classflow-client-lourd-X.X.X.jar
```

**Linux/Mac :**
```bash
java -jar target/classflow-client-lourd-X.X.X.jar
```

## ✨ Fonctionnalités

### 1. Authentification 🔐
- Connexion avec Basic Auth
- Interface moderne avec gradient et effets visuels
- Comptes de test : `demo1/demo1`, `demo2/demo2`, `demo3/demo3`

### 2. Gestion des classes 📚
- Liste des classes avec compteur d'élèves
- Créer une nouvelle classe
- Supprimer une classe (avec confirmation)
- Ouvrir une classe pour accéder à ses fonctionnalités
- Design moderne avec boutons colorés et icônes

### 3. Gestion des élèves (onglet 👥 Élèves)
- Lister tous les élèves d'une classe
- Ajouter un élève (nom et prénom)
- Modifier les informations d'un élève
- Supprimer un élève
- **Remarques** : Visualiser et ajouter des remarques par élève
- Interface avec panneau latéral pour les remarques

### 4. Roulette aléatoire (onglet 🎲 Roulette)
- Tirage au sort d'un élève avec animation
- Animation progressive avec effet de défilement
- Grand affichage du nom de l'élève sélectionné
- Design coloré et attractif

### 5. Gestion des groupes (onglet 👨‍👩‍👧‍👦 Groupes)
- **Groupes aléatoires** : Créer des groupes automatiquement en spécifiant le nombre
- **Groupes manuels** : Créer des groupes personnalisés vides
- **Noms de groupes éditables** : Double-clic ou bouton ✏️ pour renommer
- **Drag & drop** : Glisser des élèves depuis la liste vers les groupes
- **⭐ Multi-groupes** : Un élève peut appartenir à plusieurs groupes différents
- Ajouter/retirer des élèves d'un groupe individuellement
- Supprimer un groupe avec confirmation
- Interface visuelle moderne avec cartes colorées

## 🎨 Design moderne

L'application dispose d'une interface professionnelle avec :
- **Dégradés colorés** pour les en-têtes (différentes couleurs par section)
- **Effets d'ombre** (dropshadow) pour la profondeur
- **Boutons interactifs** avec effets hover
- **Police moderne** : Segoe UI
- **Icônes emoji** pour une meilleure identification visuelle
- **Palette de couleurs cohérente** : bleus, verts, oranges, violets
- **Espacement et padding optimisés** pour une meilleure lisibilité

## 🛠️ Architecture technique

- **JavaFX 21** pour l'interface graphique moderne
- **Java HttpClient** pour la communication REST avec le backend
- **Jackson** pour la sérialisation/désérialisation JSON
- **Animations JavaFX** : Timeline pour la roulette et transitions fluides
- **TabPane** : Organisation en onglets pour une navigation intuitive
- **Gestion de l'état fullscreen** : Préservation du mode plein écran lors des transitions
- DTOs optimisés pour la communication avec le backend

## 📁 Structure du code

### Views (vues JavaFX)
- `LoginView` : Écran de connexion moderne
- `MainView` : Liste des classes avec actions CRUD
- `ClassRoomContainerView` : Conteneur avec onglets (Élèves, Roulette, Groupes)
- `ClassRoomDetailView` : Gestion des élèves et remarques
- `RandomStudentView` : Roulette de tirage au sort
- `GroupManagementView` : Gestion des groupes avec drag & drop

### DTOs
- DTOs standards : `ClassRoomDTO`, `EleveDTO`, `RemarqueDTO`, `GroupeDTO`
- DTOs requêtes : `GroupeCreateRequest`

### Services
- `ClassFlowApiService` : Client HTTP pour toutes les opérations REST
- Détection automatique du backend (distant ou local)

### Application
- `ClassFlowClientApplication` : Point d'entrée principal avec gestion des scènes
- `Main` : Wrapper pour le lancement (requis par JavaFX avec les JARs)

## 🌐 Configuration

Le backend est configuré dans `src/main/resources/application.properties` :

```properties
classflow.backend.url=https://classflow-back.onrender.com/
```

L'application détecte automatiquement :
1. Le backend distant configuré
2. Le backend local (localhost:8080) en fallback

## 📸 Interface

1. **Écran de login** : Design moderne avec carte blanche sur fond gradient
2. **Liste des classes** : En-tête coloré avec actions rapides
3. **Onglet Élèves** : Vue double panneau (liste + remarques)
4. **Onglet Roulette** : Animation fluide avec grand affichage
5. **Onglet Groupes** : Drag & drop intuitif avec support multi-groupes
