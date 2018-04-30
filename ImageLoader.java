/*
 * ---------------------------------------------------------------------------
 *
 * Facman -- Un cl�ne de Pacman en Java
 * Copyright (c) 2004 Benjamin Gaillard & Lionel Imbs
 *
 * ---------------------------------------------------------------------------
 *
 * Fichier     : ImageLoader.java
 *
 * Description : Classe ImageLoader permettant de r�cup�rer des images et
 *               d'attendre la fin de leur chargement.
 *
 * Commentaire : Cette classe ne peut pas �tre utilis�e telle quelle : il faut
 *               utiliser une classe en d�rivant et impl�mentant les m�thodes
 *               permettant de r�cuperer et de cr�er des images.
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
import java.awt.MediaTracker;
import java.awt.Component;
import java.awt.image.ImageProducer;
import java.awt.image.FilteredImageSource;
import java.awt.image.CropImageFilter;

/**
 * Classe ImageLoader permettant de charger des images et de s'assurer
 * qu'elles le sont compl�tement, pour �viter l'effet de clignottement du au
 * chargement diff�r� des images
 */
abstract class ImageLoader
{
    // R�pertoire contenant les images, et leur extension
    protected static final String PATH = "images/";
    protected static final String EXT  = ".png";

    // Taille par d�faut des sprites
    private static final int DEF_SPRT_WIDTH = 32, DEF_SPRT_HEIGHT = 32;

    // Tracker d'images
    private final MediaTracker tracker;

    // M�thodes abstraites que les classes d�riv�es devront impl�menter

    // Charge une image depuis un fichier
    abstract protected Image getImage(final String filename);

    // Cr�e une nouvelle image
    abstract protected Image createImage(final ImageProducer producer);

    /**
     * Constructeur de la classe ImageLoader
     */
    public ImageLoader(Component component)
    {
	// Cr�ation du tracker
	tracker = new MediaTracker(component);
    }

    /**
     * Charge une image simple
     */
    public Image load(final String filename)
    {
	// Image � charger
	final Image image = getImage(filename);

	// Ajout de l'image au tracker
	tracker.addImage(image, 0);

	// Retourne l'image en cours de chargement
	return image;
    }

    /**
     * Cr�e un tableau d'images (2 dimensions) � partir d'une image contenant
     * en fait une grille d'images
     */
    public Image[][] load(final String filename, final int cols,
			  final int rows)
    {
	return load(filename, cols, rows, DEF_SPRT_WIDTH, DEF_SPRT_HEIGHT);
    }

    /**
     * Cr�e un tableau d'images (2 dimensions) � partir d'une image contenant
     * en fait une grille d'images avec une taille de sprite personnalis�e
     */
    public Image[][] load(final String filename,
			  final int cols, final int rows,
			  final int spriteWidth, final int spriteHeight)
    {
	// Cr�ation du tableau d'images
	final Image[][] images = new Image[rows][cols];

	// Charge la grille d'images
	load(filename, images, spriteWidth, spriteHeight);

	// Retourne le tableau d'images en cours de chargement
	return images;
    }

    /**
     * Charge dans un tableau d'images (2 dimensions) une grille d'images
     */
    public void load(final String filename, final Image[][] images)
    {
	load(filename, images, DEF_SPRT_WIDTH, DEF_SPRT_HEIGHT);
    }

    /**
     * Charge dans un tableau d'images (2 dimensions) une grille d'images avec
     * une taile de sprite personnalis�e
     */
    public void load(final String filename, final Image[][] images,
		     final int spriteWidth, final int spriteHeight)
    {
	// Image contenant une grille d'images plus petites
	final Image array = getImage(filename);

	// Si le param�tre est valide
	if (images != null && images.length != 0) {
	    // Objets servant � la d�coupe de l'image
	    CropImageFilter     filter;
	    FilteredImageSource source;

	    // Cr�ation des images contenues dans la grille d'images
	    for (int y = 0; y < images.length; y++)
		for (int x = 0; x < images[y].length; x++) {
		    // D�coupe l'image
		    filter = new CropImageFilter(x * spriteWidth,
						 y * spriteHeight,
						 spriteWidth, spriteHeight);
		    source = new FilteredImageSource(array.getSource(),
						     filter);

		    // Stocke le r�sultat dans le tableau
		    images[y][x] = createImage(source);

		    // Ajout de l'image au tracker
		    tracker.addImage(images[y][x], 0);
		}
	}
    }

    /**
     * Attend que toutes les images soient charg�es
     */
    public void waitLoading()
    {
	// Gestion des exceptions
	try {
	    // Attend le chargement des images
	    tracker.waitForAll();

	    // Gestion des erreurs
	    if (tracker.isErrorAny())
		throw new IllegalArgumentException("Sprites non charg�s");
	} catch (Exception e) { /* Ne r�agit pas aux erreurs �ventuelles */ }
    }
}

// Fin du fichier
