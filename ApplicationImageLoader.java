/*
 * ---------------------------------------------------------------------------
 *
 * Facman -- Un clône de Pacman en Java
 * Copyright (c) 2004 Benjamin Gaillard & Lionel Imbs
 *
 * ---------------------------------------------------------------------------
 *
 * Fichier     : ApplicationImageLoader.java
 *
 * Description : Classe ApplicationImageLoader dérivant de ImageLoader et
 *               implémentant les méthodes de chargement et de création
 *               d'images pour une application autonome.
 *
 * Commentaire : La version pour applet est la classe AppletImageLoader.
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
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.image.ImageProducer;

/**
 * Classe ApplicationImageLoader permettant de charger des images :
 * implémentation pour une application autonome
 */
class ApplicationImageLoader extends ImageLoader
{
    // Objets utilisés
    private final Component component;
    private final Toolkit toolkit;
    private final Class thisClass;

    /**
     * Constructeur de la classe ApplicationImageLoader
     */
    public ApplicationImageLoader(final Component component)
    {
	// Appel du constructeur de la superclasse
	super(component);

	// Initialisation des objets
	this.component = component;
	toolkit = Toolkit.getDefaultToolkit();
	thisClass = getClass();
    }

    /**
     * Charge une image depuis un fichier
     */
    protected Image getImage(final String filename)
    {
	return toolkit.getImage(thisClass.getResource(PATH + filename + EXT));
    }

    /**
     * Crée une nouvelle image
     */
    protected Image createImage(final ImageProducer producer)
    {
	return component.createImage(producer);
    }
}

// Fin du fichier
