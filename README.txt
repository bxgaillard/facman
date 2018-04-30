------------------------------------------------------------------------------

Facman -- Un cl�ne de Pacman en Java
Copyright (c) 2004 Benjamin Gaillard & Lionel Imbs

------------------------------------------------------------------------------

Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le
modifier conform�ment aux dispositions de la Licence Publique G�n�rale GNU,
telle que publi�e par la Free Software Foundation ; version 2 de la
licence, ou encore (� votre convenance) toute version ult�rieure.

Ce programme est distribu� dans l'espoir qu'il sera utile, mais SANS AUCUNE
GARANTIE ; sans m�me la garantie implicite de COMMERCIALISATION ou
D'ADAPTATION � UN OBJET PARTICULIER. Pour plus de d�tail, voir la Licence
Publique G�n�rale GNU.

Vous devez avoir re�u un exemplaire de la Licence Publique G�n�rale GNU en
m�me temps que ce programme ; si ce n'est pas le cas, �crivez � la Free
Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, �tats-Unis.

------------------------------------------------------------------------------


DESCRIPTION

Facman est un projet d'�tudes. Il s'agit de cr�er un petit jeu en java.
Celui-ci est un cl�ne du c�l�bre Pacman, avec un gameplay modifi� et des
graphismes am�lior�s.

Facman peut �tre ex�cut� de deux fa�ons diff�rentes : soit en tant
qu'application autonome, auquel cas il faut le lancer au moyen de la commande
java du JRE :
    java -jar facman.jar
ou en tant qu'applet : dans ce cas, il suffit d'ouvrir facman.html avec un
navitateur comprenant le plugin Java <http://java.sun.com/>.


DOCUMENTS

Le rapport est en LaTeX et se trouve dans docs/report.tex. Pour cr�er un PDF
� partir du source LaTeX, taper `make' dans le r�pertoire `docs'.

L'organisation des sources est d�crite dans docs/sources.txt.
Les r�gles du jeu et le gameplay sont expliqu�s dans docs/game.txt.
Le format des fichiers de niveau est sp�cifi� dans docs/levels.txt.


COMPILATION

Pour compiler Facman, il vous suffit de disposer d'une distribution J2SDK. Il
suffit alors de taper `make' pour lancer la compilation automatique. Pour plus
d'informations sur les diff�rents cibles pouvant �tre utilis�es avec make,
lisez le commentaire au d�but du fichier `Makefile'.


HAVE FUN!
