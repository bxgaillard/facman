/*
 * ---------------------------------------------------------------------------
 *
 * Facman -- Un clône de Pacman en Java
 * Copyright (c) 2004 Benjamin Gaillard & Lionel Imbs
 *
 * ---------------------------------------------------------------------------
 *
 * Fichier     : AppletImageLoader.java
 *
 * Description : Classe ApplicationImageLoader dérivant de ImageLoader et
 *               implémentant les méthodes de chargement et de création
 *               d'images pour une applet.
 *
 * Commentaire : La version pour application autonome est la classe
 *               ApplicationImageLoader.
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
import java.awt.Image;
import java.awt.image.ImageProducer;
import java.applet.Applet;

/**
 * Classe AppletImageLoader permettant de charger des images : implémentation
 * pour une applet
 */
class AppletImageLoader extends ImageLoader
{
    // Objets utilisés
    private final Applet applet;

    /**
     * Constructeur de la classe AppletImageLoader
     */
    public AppletImageLoader(final Applet applet)
    {
	// Appel du constructeur de la superclasse
	super(applet);

	// Initialisation des objets
	this.applet = applet;
    }

    /**
     * Charge une image depuis un fichier
     */
    protected Image getImage(final String filename)
    {
	return applet.getImage(applet.getCodeBase(), PATH + filename + EXT);
    }

    /**
     * Crée une nouvelle image
     */
    protected Image createImage(final ImageProducer producer)
    {
	return applet.createImage(producer);
    }
}

// Fin du fichier
