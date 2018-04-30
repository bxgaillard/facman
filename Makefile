# ----------------------------------------------------------------------------
#
# Facman -- Un cl�ne de Pacman en Java
# Copyright (c) 2004 Benjamin Gaillard & Lionel Imbs
#
# ----------------------------------------------------------------------------
#
# Fichier     : Makefile
#
# Description : Le fichier permettant de compiler et d'ex�cuter simplement
#               Facman.
#
# Commentaire : Utiliser `make' pour compiler, `make final' pour compiler sans
#               les informations de d�bogage, `make debug' pour compiler avec,
#               `make run' pour ex�cuter directement Facman et `make applet'
#               pour ex�cuter l'applet Facman avec le programme `appletviewer'
#               apr�s s'�tre assur� que les fichiers de classe �taient � jour.
#               Pour supprimer les fichiers .class, utiliser `make cleantmp',
#               et `make clean' pour supprimer les fichiers .class ainsi que
#               le fichier .jar.
#
# ----------------------------------------------------------------------------
#
# Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le
# modifier conform�ment aux dispositions de la Licence Publique G�n�rale GNU,
# telle que publi�e par la Free Software Foundation ; version 2 de la
# licence, ou encore (� votre convenance) toute version ult�rieure.
#
# Ce programme est distribu� dans l'espoir qu'il sera utile, mais SANS AUCUNE
# GARANTIE ; sans m�me la garantie implicite de COMMERCIALISATION ou
# D'ADAPTATION � UN OBJET PARTICULIER. Pour plus de d�tail, voir la Licence
# Publique G�n�rale GNU.
#
# Vous devez avoir re�u un exemplaire de la Licence Publique G�n�rale GNU en
# m�me temps que ce programme ; si ce n'est pas le cas, �crivez � la Free
# Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, �tats-Unis.
#
# ----------------------------------------------------------------------------


# Les commandes et leurs param�tres
JC     := javac
JFLAGS := -encoding ISO-8859-1 -target 1.3
JAVA   := java
VIEWER := appletviewer
JAR    := $(dir $(shell which $(JC)))jar # On veut le jar du JDK par d�faut
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

# Les cibles sp�ciales
.SUFFIXES: .java .class
.PHONY:    default final debug all run applet clean cleantmp

# D�bogage ou non par d�faut ?
default: final
#default: debug

# Sans d�bogage
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

# Cr�ation du manifest
$(MANIFEST): $(MAKEFILE)
	echo 'Main-Class: $(MAIN)' > $(MANIFEST)

# Cr�ation du JAR
$(TARGET): $(MAIN).class $(MANIFEST) $(DATAFILES) $(MAKEFILE)
	$(JAR) cfm $@ $(MANIFEST) $(OBJ) $(DATADIRS)

# Ex�cution apr�s compilation
run: default
	$(JAVA) -jar $(TARGET)

# Ex�cution de l'applet apr�s compilation
applet: default
	$(VIEWER) $(HTML)

# Suppression des fichiers `.class' et du fichier `.jar'
clean:
	$(RM) $(OBJ) $(MANIFEST) $(TARGET)

# Suppression des fichiers `.class'
cleantmp:
	$(RM) $(OBJ) $(MANIFEST)

# Fin du fichier
