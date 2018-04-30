/*
 * ---------------------------------------------------------------------------
 *
 * Facman -- Un cl�ne de Pacman en Java
 * Copyright (c) 2004 Benjamin Gaillard & Lionel Imbs
 *
 * ---------------------------------------------------------------------------
 *
 * Fichier     : Facman.java
 *
 * Description : La classe Facman est la classe principale : elle d�rive de la
 *               classe Applet et utilise les autres classes du projet.
 *
 * Commentaire : Cette classe ne fait presque rien � part instancier les
 *               autres classes et utiliser leurs m�thodes.
 *
 * ---------------------------------------------------------------------------
 *
 * Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le
 * modifier conform�ment aux dispositions de la Licence Publique G�n�rale GNU,
 * telle que publi�e par la Free Software Foundation ; version 2 de la
 * licence, ou encore (� votre convenance) toute version ult�rieure.
 *
 * Ce programme est distribu� dans l'espoir qu'il sera utile, mais SANS AUCUNE
 * GARANTIE ; sans m�me la garantie implicite de COMMERCIALISATION ou
 * D'ADAPTATION � UN OBJET PARTICULIER. Pour plus de d�tail, voir la Licence
 * Publique G�n�rale GNU.
 *
 * Vous devez avoir re�u un exemplaire de la Licence Publique G�n�rale GNU en
 * m�me temps que ce programme ; si ce n'est pas le cas, �crivez � la Free
 * Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, �tats-Unis.
 *
 * ---------------------------------------------------------------------------
 */


// Imports
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Frame;
import java.awt.Dimension;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.applet.Applet;

/**
 * La classe Facman est la classe principale de l'applet, charg�e d'utiliser
 * les autres classes et contenant quelques primitives de dessin
 */
public class Facman extends Applet implements WindowListener
{
    // Variables utilis�es pour la gestion de la fen�tre quand Facman est
    // ex�cut� en tant qu'application autonome
    private static final Dimension SIZE = new Dimension(640, 480);
    private              Frame frame = null;

    // Image servant � impl�menter un tampon double (double buffer)
    private Image db;

    /**
     * M�thode appel�e lors de l'initialisation de l'applet ; on met ici tout
     * ce qu'on ne peut pas faire dans un constructeur. Les objets sont aussi
     * initialis�s ici car ils utilisent une instance de Facman pour effectuer
     * des op�rations graphiques (chargement des images, dessin...)
     */
    public void init()
    {
	// Autorise l'utilisation des touches de direction et de tabulation,
	// normalement affect�es au d�placement entre les contr�les graphiques
	setFocusable(true);
	setFocusTraversalKeysEnabled(false);
	requestFocus();

	// Cr�ation du tampon double
	db = frame != null ?
	    frame.createImage(frame.getWidth(), frame.getHeight()) :
	    createImage(getWidth(), getHeight());
	final Graphics db_gc = db.getGraphics();

	// Cr�ation du chargeur d'images
	final ImageLoader loader = frame != null ?
	    (ImageLoader) new ApplicationImageLoader(frame) :
	    (ImageLoader) new AppletImageLoader(this);

	// Cr�ation d'une instance de la classe HomeScreen
	final HomeScreen home = new HomeScreen(db_gc, loader, this);

	// Lancement de l'�cran d'accueil
	home.init();
	home.run();
    }

    /**
     * Surcharge de la m�thode update qui par d�faut effectue plus
     * d'op�rations : cela permet d'optimiser la vitesse de l'affichage
     */
    public void update(final Graphics gc)
    {
	// Effectue le dessin
	paint(gc);
    }

    /**
     * Redessine le cadre dans lequel est contenu l'applet
     */
    public void paint(final Graphics gc)
    {
	// Copie le tampon (buffer)
	gc.drawImage(db, 0, 0, this);
    }

    /**
     * M�thode appel�e lorsque Facman est ex�cut� en tant qu'application (en
     * opposition � l'applet)
     */
    public static void main(final String argv[])
    {
	// Cr�ation de la fen�tre et de l'applet
	Frame frame = new Frame("Facman");
	Facman applet = new Facman();
	applet.setFrame(frame);

	// D�finition des propri�t�s de la fen�tre
	frame.add("Center", applet);
	frame.pack();
	frame.setResizable(false);
	frame.addWindowListener(applet);

	// Ex�cution de l'applet
	applet.init();
	applet.start();
	frame.show();
    }

    /**
     * M�thode publique servant � d�finit la fen�tre de l'application autonome
     */
    public void setFrame(final Frame frame)
    {
	this.frame = frame;
    }

    /**
     * M�thode appel�e pour conna�tre la taille minimale de la fen�tre
     */
    public Dimension getMinimumSize()
    {
	return new Dimension(SIZE);
    }

    /**
     * M�thode appel�e pour conna�tre la taille maximale de la fen�tre
     */
    public Dimension getMaximumSize()
    {
	return new Dimension(SIZE);
    }

    /**
     * M�thode appel�e pour conna�tre la taille pr�f�r�e de la fen�tre
     */
    public Dimension getPreferredSize()
    {
	return new Dimension(SIZE);
    }

    /**
     * M�thode appel�e quand on tente de fermer la fen�tre
     */
    public void windowClosing(final WindowEvent e)
    {
	// Termine l'application
	System.exit(0);
    }

    /*
     * Autres m�thodes de l'interface WindowListener non impl�ment�es
     */
    public void windowActivated(final WindowEvent e) {}
    public void windowClosed(final WindowEvent e) {}
    public void windowDeactivated(final WindowEvent e) {}
    public void windowDeiconified(final WindowEvent e) {}
    public void windowIconified(final WindowEvent e) {}
    public void windowOpened(final WindowEvent e) {}
}

// Fin du fichier
