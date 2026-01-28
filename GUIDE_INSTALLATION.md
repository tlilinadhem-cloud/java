# Guide d'Installation et d'Exécution - Windows

## 🔧 Installation des Prérequis

### 1. Installer Java 21

1. Téléchargez Java 21 depuis: https://adoptium.net/temurin/releases/?version=21
2. Choisissez **Windows x64** → **JDK** → **Installer**
3. Vérifiez l'installation:
   ```powershell
   java -version
   ```
   Doit afficher: `openjdk version "21"` ou similaire

4. Configurez JAVA_HOME (optionnel mais recommandé):
   ```powershell
   # Dans PowerShell (en tant qu'administrateur)
   [System.Environment]::SetEnvironmentVariable("JAVA_HOME", "C:\Program Files\Eclipse Adoptium\jdk-21.x.x-hotspot", "Machine")
   ```

### 2. Installer Maven

#### Option A: Via Chocolatey (recommandé)
```powershell
# Dans PowerShell (en tant qu'administrateur)
choco install maven
```

#### Option B: Installation manuelle
1. Téléchargez Maven depuis: https://maven.apache.org/download.cgi
2. Extrayez l'archive dans `C:\Program Files\Apache\maven`
3. Ajoutez Maven au PATH:
   - Ouvrez "Variables d'environnement" (Win + R → `sysdm.cpl` → Avancé → Variables d'environnement)
   - Ajoutez `C:\Program Files\Apache\maven\bin` à la variable `Path`
4. Vérifiez l'installation:
   ```powershell
   mvn -version
   ```

### 3. Installer Lombok dans votre IDE

#### IntelliJ IDEA:
1. File → Settings → Plugins
2. Recherchez "Lombok"
3. Installez le plugin "Lombok"
4. Redémarrez IntelliJ
5. File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors
6. Cochez "Enable annotation processing"

#### Eclipse:
1. Téléchargez Lombok depuis: https://projectlombok.org/download
2. Double-cliquez sur `lombok.jar`
3. Sélectionnez votre installation Eclipse
4. Cliquez "Install/Update"
5. Redémarrez Eclipse

#### VS Code:
1. Installez l'extension "Extension Pack for Java" (Microsoft)
2. Lombok est inclus dans ce pack

## 🚀 Exécution du Projet

### Méthode 1: Via IntelliJ IDEA (RECOMMANDÉ)

1. **Ouvrir le projet:**
   - File → Open
   - Sélectionnez le dossier `Tunisian_Agricultural_Export_AI_System`
   - Choisissez "Open as Project"

2. **Configurer le SDK:**
   - File → Project Structure (Ctrl+Alt+Shift+S)
   - Project → SDK: Sélectionnez Java 21
   - Project → Language level: 21

3. **Synchroniser Maven:**
   - Clic droit sur `pom.xml` → Maven → Reload Project
   - Attendez que toutes les dépendances soient téléchargées

4. **Exécuter:**
   - Ouvrez `src/main/java/tn/isg/economics/App.java`
   - Clic droit → Run 'App.main()'
   - OU utilisez le bouton ▶ vert à côté de `main()`

### Méthode 2: Via Eclipse

1. **Ouvrir le projet:**
   - File → Import → Existing Maven Projects
   - Sélectionnez le dossier du projet
   - Cliquez Finish

2. **Configurer Java 21:**
   - Window → Preferences → Java → Installed JREs
   - Ajoutez Java 21 si nécessaire
   - Window → Preferences → Java → Compiler → Compiler compliance level: 21

3. **Exécuter:**
   - Ouvrez `src/main/java/tn/isg/economics/App.java`
   - Clic droit → Run As → Java Application

### Méthode 3: Via Ligne de Commande (PowerShell)

```powershell
# Naviguer vers le dossier du projet
cd "C:\Users\nadhe\Desktop\Tunisian_Agricultural_Export_AI_System"

# Nettoyer et compiler
mvn clean compile

# Exécuter l'application
mvn exec:java -Dexec.mainClass="tn.isg.economics.App"

# OU compiler en JAR et exécuter
mvn clean package
java -jar target\tunisian-export-ai-1.0-SNAPSHOT.jar
```

## ⚠️ Résolution des Problèmes Courants

### Problème: "mvn n'est pas reconnu"
**Solution:**
- Vérifiez que Maven est dans votre PATH
- Redémarrez PowerShell/CMD après l'installation
- Utilisez votre IDE à la place (IntelliJ/Eclipse)

### Problème: "Java version mismatch"
**Solution:**
```powershell
# Vérifier la version Java utilisée
java -version

# Si ce n'est pas Java 21, configurez JAVA_HOME
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-21.x.x-hotspot"
```

### Problème: "Lombok annotations not working"
**Solution:**
1. Vérifiez que le plugin Lombok est installé dans votre IDE
2. Activez l'annotation processing dans les paramètres de votre IDE
3. Redémarrez votre IDE
4. Nettoyez et recompilez: `mvn clean compile`

### Problème: "Cannot resolve symbol" pour les classes
**Solution:**
1. Dans IntelliJ: File → Invalidate Caches → Invalidate and Restart
2. Dans Eclipse: Project → Clean → Clean all projects
3. Recompilez: `mvn clean compile`

### Problème: "DJL model loading failed" ou "LangChain4j connection failed"
**Note:** C'est **NORMAL** ! L'application utilise des fallbacks et fonctionne toujours même sans modèles ML ou sans Ollama.

## 📋 Checklist de Vérification

Avant d'exécuter, vérifiez:

- [ ] Java 21 installé (`java -version`)
- [ ] Maven installé (`mvn -version`) OU IDE configuré
- [ ] Lombok plugin installé dans votre IDE
- [ ] Annotation processing activé dans votre IDE
- [ ] Projet ouvert dans votre IDE
- [ ] Maven a téléchargé les dépendances (vérifiez dans `target/` ou dans les logs Maven)

## 🎯 Test Rapide

Pour vérifier que tout fonctionne:

```powershell
# Dans le dossier du projet
mvn clean test
```

Si cette commande réussit, votre environnement est correctement configuré !

## 📞 Support

Si vous rencontrez des problèmes:
1. Vérifiez que tous les prérequis sont installés
2. Consultez les logs d'erreur dans votre IDE
3. Vérifiez que le projet compile: `mvn clean compile`
4. Assurez-vous que votre IDE utilise Java 21

---

**Bon développement !** 🚀
