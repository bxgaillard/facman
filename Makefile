# ----------------------------------------------------------------------------
#
# Facman -- Un clône de Pacman en Java
# Copyright (c) 2004 Benjamin Gaillard & Lionel Imbs
#
# ----------------------------------------------------------------------------
#
# Fichier     : Makefile
#
# Description : Le fichier permettant de compiler et d'exécuter simplement
#               Facman.
#
# Commentaire : Utiliser `make' pour compiler, `make final' pour compiler sans
#               les informations de débogage, `make debug' pour compiler avec,
#               `make run' pour exécuter directement Facman et `make applet'
#               pour exécuter l'applet Facman avec le programme `appletviewer'
#               après s'être assuré que les fichiers de classe étaient à jour.
#               Pour supprimer les fichiers .class, utiliser `make cleantmp',
#               et `make clean' pour supprimer les fichiers .class ainsi que
#               le fichier .jar.
#
# ----------------------------------------------------------------------------
#
# Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le
# modifier conformément aux dispositions de la Licence Publique Générale GNU,
# telle que publiée par la Free Software Foundation ; version 2 de la
# licence, ou encore (à votre convenance) toute version ultérieure.
#
# Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS AUCUNE
# GARANTIE ; sans même la garantie implicite de COMMERCIALISATION ou
# D'ADAPTATION À UN OBJET PARTICULIER. Pour plus de détail, voir la Licence
# Publique Générale GNU.
#
# Vous devez avoir reçu un exemplaire de la Licence Publique Générale GNU en
# même temps que ce programme ; si ce n'est pas le cas, écrivez à la Free
# Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, États-Unis.
#
# ----------------------------------------------------------------------------


# Les commandes et leurs paramètres
JC     := javac
JFLAGS := -encoding ISO-8859-1 -target 1.3
JAVA   := java
VIEWER := appletviewer
JAR    := $(dir $(shell which $(JC)))jar # On veut le jar du JDK par défaut
RM     := rm -f

# Les fichiers
HTML      := $(wildcard *.html)
TARGET    := $(HTML:.html=.jar)
SRC       := $(wildcard *.java)
OBJ       := $(SRC:.java=.class)
MAIN      := Facman
MANIFEST  := main.mf
DATADIRS  := images levels LICENSE.txt
DATAFILES := $(foreach dir,$(DATADIRS),$(wildcard $(dir)/*))
MAKEFILE  := Makefile

# Les cibles spéciales
.SUFFIXES: .java .class
.PHONY:    default final debug all run applet clean cleantmp

# Débogage ou non par défaut ?
default: final
#default: debug

# Sans débogage
final: DEBUG = -O
final: all

# Avec debogage
debug: DEBUG = -g
debug: all

# Tout compiler
all: $(TARGET)

# Compilation des .java en .class
$(MAIN).class: $(SRC) $(MAKEFILE)
	$(JC) $(JFLAGS) $(DEBUG) $(SRC)

# Création du manifest
$(MANIFEST): $(MAKEFILE)
	echo 'Main-Class: $(MAIN)' > $(MANIFEST)

# Création du JAR
$(TARGET): $(MAIN).class $(MANIFEST) $(DATAFILES) $(MAKEFILE)
	$(JAR) cfm $@ $(MANIFEST) $(OBJ) $(DATADIRS)

# Exécution après compilation
run: default
	$(JAVA) -jar $(TARGET)

# Exécution de l'applet après compilation
applet: default
	$(VIEWER) $(HTML)

# Suppression des fichiers `.class' et du fichier `.jar'
clean:
	$(RM) $(OBJ) $(MANIFEST) $(TARGET)

# Suppression des fichiers `.class'
cleantmp:
	$(RM) $(OBJ) $(MANIFEST)

# Fin du fichier
