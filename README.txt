------------------------------------------------------------------------------

Facman -- Un clône de Pacman en Java
Copyright (c) 2004 Benjamin Gaillard & Lionel Imbs

------------------------------------------------------------------------------

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le
modifier conformément aux dispositions de la Licence Publique Générale GNU,
telle que publiée par la Free Software Foundation ; version 2 de la
licence, ou encore (à votre convenance) toute version ultérieure.

Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS AUCUNE
GARANTIE ; sans même la garantie implicite de COMMERCIALISATION ou
D'ADAPTATION À UN OBJET PARTICULIER. Pour plus de détail, voir la Licence
Publique Générale GNU.

Vous devez avoir reçu un exemplaire de la Licence Publique Générale GNU en
même temps que ce programme ; si ce n'est pas le cas, écrivez à la Free
Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, États-Unis.

------------------------------------------------------------------------------


DESCRIPTION

Facman est un projet d'études. Il s'agit de créer un petit jeu en java.
Celui-ci est un clône du célèbre Pacman, avec un gameplay modifié et des
graphismes améliorés.

Facman peut être exécuté de deux façons différentes : soit en tant
qu'application autonome, auquel cas il faut le lancer au moyen de la commande
java du JRE :
    java -jar facman.jar
ou en tant qu'applet : dans ce cas, il suffit d'ouvrir facman.html avec un
navitateur comprenant le plugin Java <http://java.sun.com/>.


DOCUMENTS

Le rapport est en LaTeX et se trouve dans docs/report.tex. Pour créer un PDF
à partir du source LaTeX, taper `make' dans le répertoire `docs'.

L'organisation des sources est décrite dans docs/sources.txt.
Les règles du jeu et le gameplay sont expliqués dans docs/game.txt.
Le format des fichiers de niveau est spécifié dans docs/levels.txt.


COMPILATION

Pour compiler Facman, il vous suffit de disposer d'une distribution J2SDK. Il
suffit alors de taper `make' pour lancer la compilation automatique. Pour plus
d'informations sur les différents cibles pouvant être utilisées avec make,
lisez le commentaire au début du fichier `Makefile'.


HAVE FUN!
