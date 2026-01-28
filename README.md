# Tunisian Agricultural Export AI System

**Système d'Intelligence Économique Tunisien Alimenté par l'IA**  
Projet d'examen OOP - Java 21 + Maven + AI/ML Libraries

## 📋 Prérequis / Requirements

- **Java 21+** (LTS recommandé)
- **Maven 3.9+**
- **IDE** (IntelliJ IDEA, Eclipse, VS Code avec extensions Java)

### Vérification de l'installation / Check Installation

```bash
# Vérifier Java
java -version
# Doit afficher: openjdk version "21" ou supérieur

# Vérifier Maven
mvn -version
# Doit afficher: Apache Maven 3.9.x ou supérieur
```

## 🚀 Étapes pour exécuter le projet / Steps to Run the Project

### 1. Cloner/Télécharger le projet
Assurez-vous d'avoir tous les fichiers du projet dans le dossier `Tunisian_Agricultural_Export_AI_System`.

### 2. Ouvrir le projet dans votre IDE
- **IntelliJ IDEA**: File → Open → Sélectionner le dossier du projet
- **Eclipse**: File → Import → Existing Maven Projects
- **VS Code**: Ouvrir le dossier, installer l'extension "Extension Pack for Java"

### 3. Configurer Java 21 dans votre IDE

#### IntelliJ IDEA:
1. File → Project Structure → Project
2. SDK: Java 21
3. Language level: 21

#### Eclipse:
1. Window → Preferences → Java → Installed JREs
2. Ajouter Java 21 si nécessaire
3. Window → Preferences → Java → Compiler → Compiler compliance level: 21

### 4. Installer les dépendances Maven

```bash
# Dans le terminal, à la racine du projet
mvn clean install
```

Cette commande va:
- Télécharger toutes les dépendances (Lombok, DJL, LangChain4j, JUnit, etc.)
- Compiler le projet
- Exécuter les tests

### 5. Exécuter l'application

#### Option A: Via Maven (recommandé)
```bash
mvn exec:java -Dexec.mainClass="tn.isg.economics.App"
```

#### Option B: Compiler et exécuter le JAR
```bash
# Compiler le projet
mvn clean package

# Exécuter le JAR généré
java -jar target/tunisian-export-ai-1.0-SNAPSHOT.jar
```

#### Option C: Depuis votre IDE
- **IntelliJ**: Clic droit sur `App.java` → Run 'App.main()'
- **Eclipse**: Clic droit sur `App.java` → Run As → Java Application
- **VS Code**: Clic droit sur `App.java` → Run Java

### 6. Utiliser le Dashboard

Une fois l'application démarrée, vous verrez un menu interactif:

```
================================================================================
TUNISIAN AGRICULTURAL EXPORT AI SYSTEM - DASHBOARD
================================================================================

## STATISTICS
--------------------------------------------------------------------------------
Total Records: XXX
  ...

## MENU
--------------------------------------------------------------------------------
1. View Statistics
2. Filter Data
3. View Charts
4. Generate Prediction
5. Generate Report
6. Export Data
0. Exit

Enter choice:
```

## 🔧 Résolution des problèmes courants / Troubleshooting

### Erreur: "Lombok annotations not working"
**Solution:**
1. Vérifiez que Lombok est installé dans votre IDE:
   - IntelliJ: File → Settings → Plugins → Rechercher "Lombok" → Installer
   - Eclipse: Installer Lombok depuis https://projectlombok.org/setup/eclipse
2. Redémarrez votre IDE
3. Vérifiez que l'annotation processing est activé dans votre IDE

### Erreur: "Cannot resolve symbol" pour les classes Lombok
**Solution:**
```bash
# Nettoyer et recompiler
mvn clean compile
```

### Erreur: "Java version mismatch"
**Solution:**
1. Vérifiez que JAVA_HOME pointe vers Java 21:
   ```bash
   # Windows PowerShell
   $env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
   
   # Linux/Mac
   export JAVA_HOME=/usr/lib/jvm/java-21-openjdk
   ```
2. Vérifiez dans `pom.xml` que `<maven.compiler.release>21</maven.compiler.release>` est présent

### Erreur: "DJL model loading failed"
**Note:** C'est normal ! Le DJLPredictor utilise un fallback (BaselinePredictor) si aucun modèle n'est chargé. L'application fonctionne toujours.

### Erreur: "LangChain4j/Ollama connection failed"
**Note:** C'est normal ! Le générateur de rapports utilise un template si Ollama n'est pas disponible. L'application fonctionne toujours.

## 📦 Structure du Projet / Project Structure

```
src/
├── main/java/tn/isg/economics/
│   ├── model/          # Records, Enums (ProductType, MarketIndicator, etc.)
│   ├── annotations/    # Custom annotations (@Audit, @ExperimentalFeature)
│   ├── exceptions/     # Custom exception classes
│   ├── repository/     # Data access interfaces and implementations
│   ├── service/        # Business logic (analytics, filtering)
│   ├── ai/             # AI/ML integration (Predictor, ReportGenerator)
│   ├── dashboard/      # MVC dashboard (model, view, controller)
│   └── App.java        # Main entry point
└── test/java/          # Unit tests
```

## ✨ Fonctionnalités Implémentées / Implemented Features

### ✅ OOP Requirements
- ✅ **Packages**: 5+ packages organisés logiquement
- ✅ **Classes**: 10+ classes démontrant encapsulation, héritage, polymorphisme
- ✅ **Records**: ExportRecord, PredictionResult (DTOs immutables)
- ✅ **Enums**: ProductType, MarketIndicator, PredictionStatus
- ✅ **Annotations**: @Audit, @ExperimentalFeature (custom)
- ✅ **Interfaces**: Predictor, ReportGenerator, ExportRecordRepository, ChartStrategy, Command
- ✅ **Functional Interfaces**: RecordFilter (@FunctionalInterface)
- ✅ **Inheritance**: Abstract classes et implémentations concrètes
- ✅ **Collections Framework**: List, Set, Map, Queue, Stream API extensivement utilisé
- ✅ **Lombok**: @Getter, @Setter, @Slf4j, @Data utilisé dans plusieurs classes
- ✅ **Exceptions**: Custom exceptions avec hiérarchie

### ✅ AI/ML Integration
- ✅ **DJL (Deep Java Library)**: DJLPredictor avec fallback
- ✅ **LangChain4j**: ReportGenerator pour génération de rapports
- ✅ **Baseline Predictor**: Prédicteur simple utilisant moyenne mobile et tendance

### ✅ Dashboard Features
- ✅ **Statistics Display**: Statistiques en temps réel (moyenne, min, max, écart-type)
- ✅ **Interactive Filtering**: Filtrage par produit, date, destination
- ✅ **Chart Visualization**: Graphiques ASCII (barres, lignes) avec Strategy pattern
- ✅ **Predictive Analytics**: Interface de prédiction avec formulaire interactif
- ✅ **Report Generation**: Génération de rapports markdown avec export
- ✅ **Data Export**: Export CSV et JSON

### ✅ Design Patterns
- ✅ **MVC**: Model-View-Controller pour le dashboard
- ✅ **Observer**: DashboardModelListener pour les mises à jour en temps réel
- ✅ **Strategy**: ChartStrategy pour différents types de graphiques
- ✅ **Factory**: ChartFactory pour créer des stratégies de graphiques
- ✅ **Command**: Command pattern pour undo/redo des filtres

## 🧪 Tests

```bash
# Exécuter tous les tests
mvn test

# Exécuter un test spécifique
mvn test -Dtest=AppTest
```

## 📝 Notes Importantes

1. **Compatibilité Lombok/JDK**: Le projet utilise Lombok 1.18.32 qui est compatible avec Java 21. Si vous avez des problèmes, assurez-vous que:
   - Votre IDE a le plugin Lombok installé
   - L'annotation processing est activé
   - Vous utilisez Java 21

2. **Données de démonstration**: L'application génère automatiquement des données d'exemple au démarrage (24 mois de données pour tous les produits).

3. **AI Models**: Les modèles DJL et LangChain4j sont configurés avec des fallbacks, donc l'application fonctionne même sans modèles pré-entraînés ou sans Ollama.

4. **Console Dashboard**: Le dashboard est basé sur la console (pas de GUI Swing/JavaFX) pour rester simple et fonctionnel.

## 📚 Documentation

- JavaDoc disponible dans le code source
- Architecture MVC documentée dans les classes du package `dashboard`
- Patterns de conception documentés dans les interfaces et classes correspondantes

## 👨‍💻 Auteur / Author

Projet développé pour l'examen OOP - Sesame University  
Préparé par: Chaouki Bayoudhi  
Année académique: 2025-2026

---

**Bon courage pour votre projet ! / Good luck with your project!** 🚀
