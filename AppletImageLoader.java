/*
 * ---------------------------------------------------------------------------
 *
 * Facman -- Un cl�ne de Pacman en Java
 * Copyright (c) 2004 Benjamin Gaillard & Lionel Imbs
 *
 * ---------------------------------------------------------------------------
 *
 * Fichier     : AppletImageLoader.java
 *
 * Description : Classe ApplicationImageLoader d�rivant de ImageLoader et
 *               impl�mentant les m�thodes de chargement et de cr�ation
 *               d'images pour une applet.
 *
 * Commentaire : La version pour application autonome est la classe
 *               ApplicationImageLoader.
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
import java.awt.Image;
import java.awt.image.ImageProducer;
import java.applet.Applet;

/**
 * Classe AppletImageLoader permettant de charger des images : impl�mentation
 * pour une applet
 */
class AppletImageLoader extends ImageLoader
{
    // Objets utilis�s
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
     * Cr�e une nouvelle image
     */
    protected Image createImage(final ImageProducer producer)
    {
	return applet.createImage(producer);
    }
}

// Fin du fichier
