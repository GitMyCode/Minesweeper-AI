# Minesweeper-AI   

Voici le code pour notre 3e projet du cours d'Intelligence Artificielle avec Éric Beaudry (INF4230). 
Nous avons réalisé un joueur artificiel de Minesweeper (Démineur) qui utilise différents algorithmes.

## Auteurs
+ Martin Bouchard
+ Frédéric Vachon
+ Louis-Bertrand Varin
+ Geneviève Lalonde
+ Nilovna Bascunan-Vasquez


## Exécution
Dans le dossier __Exécutable__, il y a un fichier jar qui permet de lancer le jeu. Il suffit d'inscrire dans une console supportant Java :
``` bash
$ java -jar Minesweeper-AI.jar
```


## Style de programmation
Le style de programmation devrait être uniforme à travers les fichiers sources du projet.
L'outil checkstyle peut être utilisé pour vérifier que le style du code est uniforme:
``` bash
$ checkstyle -c checkstyle.xml src/minesweeper/ui/WindowMinesweeper.java
```

## Installation et compilation du code (pour correction ou contribution)    
Téléchargez le code du jeu dans un dossier. Pour ce faire, vous pouvez cliquer sur le bouton __Download ZIP__ à droite de l'écran et décompresser le dossier, ou encore exécuter la commande `git clone https://github.com/GitMyCode/Minesweeper-AI.git`

Ce projet utilise l'IDE IntelliJ IDEA. Si vous ne l'avez pas, vous pouvez télécharger la dernière version gratuitement en suivant ce [lien](https://www.jetbrains.com/idea/download/). 

Une fois dans IntelliJ, créez un nouveau projet. Lorsque vous devez créer un dossier pour le nouveau projet, choisissez à la place le dossier Minesweeper-AI, et tous ses fichiers seront ajoutés.

Attendez que IntelliJ termine de compiler les modules. Ensuite, vous devriez avoir une arborescence de tous les fichiers du projet à gauche de l'écran.

Si le dossier src n'est pas en bleu (dossier source), sélectionnez le dossier MineSweeper dans Minesweeper-AI et appuyez sur F4 pour ouvrir la fenêtre des Module Settings. 

À l'aide du bouton __Mark as:__, marquez le dossier src comme étant un dossier __Sources__. 

Fermez la fenêtre des Settings.

Ouvrez maintenant la fenêtre des paramètres du projet dans File>Settings... 

Choisissez Editor>File Encodings. Assurez-vous que tous les encodages sont en UTF-8, y compris __Default encoding for properties files__ dans le bas de l'écran. 

Toujours dans les settings, choisissez ensuite Version Control>Github si vous désirez faire des mises à jour du dépôt. Inscrivez __https://github.com/GitMyCode/Minesweeper-AI.git__ dans le champ __Host:__ et entrez vos informations de login dans les champs appropriés, puis cliquez sur __Apply__.

Fermez la fenêtre des Settings.

Pour indiquer à IntelliJ à quel endroit le Main se trouve dans le projet (et pour exécuter le programme en même temps), ouvrez l'arborescence et cliquez avec le bouton droit de la souris sur : src>minesweeper>ui>WindowMinesweeper. Choisissez __Run WindowMineswee...main()__.

Voilà! Vous pouvez vous amuser à configurer votre Démineur et à le tester avec différents algorithmes.

## Structure du projet
Dans le dossier ui, les fichiers suivants sont utiles à connaître:
+ __BoardGameView__ contient tous les événements liés au jeu (création de la grille, options de la grille, etc.).
+ __GameRunner__ contient les méthodes nécessaires pour l'exécution du jeu.
+ __GLOBAL__ contient toutes les variables *hardcodées* du logiciel.

Le dossier __src__ dans __Minesweeper__ contient tout le code du projet. Il est composé de plusieurs sous-dossiers:
+ __ai__ contient les différents algorithmes pour le joueur artificiel (CSP, aléatoire, réseau bayésien...)
+ __ui__ > __design__ contient les éléments de l'interface graphique du jeu.
+ __util__ contient une classe permettant de charger toutes les classes dans le paquetage sans connaître leurs noms.

Dans le dossier ui, le fichier suivant est important:
+ __WindowMinesweeper__ contient toute l'interface du menu d'options qui permet de démarrer le jeu.


IntelliJ, lorsqu'un nouveau projet est créé, ajoute un dossier __IDEA__ à la racine, qui contient tous les fichiers nécessaires à la compilation. Il s'agit en grande partie des fichiers xml. Vous n'avez pas à vous préoccuper de ce dossier.

Lorsque le programme est exécuté au moins une fois, IntelliJ crée un dossier __out__ à la racine du projet dans lequel il insère tous les fichiers __.class__. Vous n'avez pas à vous préoccuper de ce dossier.


