/*
 * ---------------------------------------------------------------------------
 *
 * Facman -- Un clône de Pacman en Java
 * Copyright (c) 2004 Benjamin Gaillard & Lionel Imbs
 *
 * ---------------------------------------------------------------------------
 *
 * Fichier     : Facman.java
 *
 * Description : La classe Facman est la classe principale : elle dérive de la
 *               classe Applet et utilise les autres classes du projet.
 *
 * Commentaire : Cette classe ne fait presque rien à part instancier les
 *               autres classes et utiliser leurs méthodes.
 *
 * ---------------------------------------------------------------------------
 *
 * Ce programme est un logiciel libre ; vous pouvez le redistribuer et/ou le
 * modifier conformément aux dispositions de la Licence Publique Générale GNU,
 * telle que publiée par la Free Software Foundation ; version 2 de la
 * licence, ou encore (à votre convenance) toute version ultérieure.
 *
 * Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS AUCUNE
 * GARANTIE ; sans même la garantie implicite de COMMERCIALISATION ou
 * D'ADAPTATION À UN OBJET PARTICULIER. Pour plus de détail, voir la Licence
 * Publique Générale GNU.
 *
 * Vous devez avoir reçu un exemplaire de la Licence Publique Générale GNU en
 * même temps que ce programme ; si ce n'est pas le cas, écrivez à la Free
 * Software Foundation Inc., 675 Mass Ave, Cambridge, MA 02139, États-Unis.
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
 * La classe Facman est la classe principale de l'applet, chargée d'utiliser
 * les autres classes et contenant quelques primitives de dessin
 */
public class Facman extends Applet implements WindowListener
{
    // Variables utilisées pour la gestion de la fenêtre quand Facman est
    // exécuté en tant qu'application autonome
    private static final Dimension SIZE = new Dimension(640, 480);
    private              Frame frame = null;

    // Image servant à implémenter un tampon double (double buffer)
    private Image db;

    /**
     * Méthode appelée lors de l'initialisation de l'applet ; on met ici tout
     * ce qu'on ne peut pas faire dans un constructeur. Les objets sont aussi
     * initialisés ici car ils utilisent une instance de Facman pour effectuer
     * des opérations graphiques (chargement des images, dessin...)
     */
    public void init()
    {
	// Autorise l'utilisation des touches de direction et de tabulation,
	// normalement affectées au déplacement entre les contrôles graphiques
	setFocusable(true);
	setFocusTraversalKeysEnabled(false);
	requestFocus();

	// Création du tampon double
	db = frame != null ?
	    frame.createImage(frame.getWidth(), frame.getHeight()) :
	    createImage(getWidth(), getHeight());
	final Graphics db_gc = db.getGraphics();

	// Création du chargeur d'images
	final ImageLoader loader = frame != null ?
	    (ImageLoader) new ApplicationImageLoader(frame) :
	    (ImageLoader) new AppletImageLoader(this);

	// Création d'une instance de la classe HomeScreen
	final HomeScreen home = new HomeScreen(db_gc, loader, this);

	// Lancement de l'écran d'accueil
	home.init();
	home.run();
    }

    /**
     * Surcharge de la méthode update qui par défaut effectue plus
     * d'opérations : cela permet d'optimiser la vitesse de l'affichage
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
     * Méthode appelée lorsque Facman est exécuté en tant qu'application (en
     * opposition à l'applet)
     */
    public static void main(final String argv[])
    {
	// Création de la fenêtre et de l'applet
	Frame frame = new Frame("Facman");
	Facman applet = new Facman();
	applet.setFrame(frame);

	// Définition des propriétés de la fenêtre
	frame.add("Center", applet);
	frame.pack();
	frame.setResizable(false);
	frame.addWindowListener(applet);

	// Exécution de l'applet
	applet.init();
	applet.start();
	frame.show();
    }

    /**
     * Méthode publique servant à définit la fenêtre de l'application autonome
     */
    public void setFrame(final Frame frame)
    {
	this.frame = frame;
    }

    /**
     * Méthode appelée pour connaître la taille minimale de la fenêtre
     */
    public Dimension getMinimumSize()
    {
	return new Dimension(SIZE);
    }

    /**
     * Méthode appelée pour connaître la taille maximale de la fenêtre
     */
    public Dimension getMaximumSize()
    {
	return new Dimension(SIZE);
    }

    /**
     * Méthode appelée pour connaître la taille préférée de la fenêtre
     */
    public Dimension getPreferredSize()
    {
	return new Dimension(SIZE);
    }

    /**
     * Méthode appelée quand on tente de fermer la fenêtre
     */
    public void windowClosing(final WindowEvent e)
    {
	// Termine l'application
	System.exit(0);
    }

    /*
     * Autres méthodes de l'interface WindowListener non implémentées
     */
    public void windowActivated(final WindowEvent e) {}
    public void windowClosed(final WindowEvent e) {}
    public void windowDeactivated(final WindowEvent e) {}
    public void windowDeiconified(final WindowEvent e) {}
    public void windowIconified(final WindowEvent e) {}
    public void windowOpened(final WindowEvent e) {}
}

// Fin du fichier
