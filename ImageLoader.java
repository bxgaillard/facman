/*
 * ---------------------------------------------------------------------------
 *
 * Facman -- Un clône de Pacman en Java
 * Copyright (c) 2004 Benjamin Gaillard & Lionel Imbs
 *
 * ---------------------------------------------------------------------------
 *
 * Fichier     : ImageLoader.java
 *
 * Description : Classe ImageLoader permettant de récupérer des images et
 *               d'attendre la fin de leur chargement.
 *
 * Commentaire : Cette classe ne peut pas être utilisée telle quelle : il faut
 *               utiliser une classe en dérivant et implémentant les méthodes
 *               permettant de récuperer et de créer des images.
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
import java.awt.MediaTracker;
import java.awt.Component;
import java.awt.image.ImageProducer;
import java.awt.image.FilteredImageSource;
import java.awt.image.CropImageFilter;

/**
 * Classe ImageLoader permettant de charger des images et de s'assurer
 * qu'elles le sont complètement, pour éviter l'effet de clignottement du au
 * chargement différé des images
 */
abstract class ImageLoader
{
    // Répertoire contenant les images, et leur extension
    protected static final String PATH = "images/";
    protected static final String EXT  = ".png";

    // Taille par défaut des sprites
    private static final int DEF_SPRT_WIDTH = 32, DEF_SPRT_HEIGHT = 32;

    // Tracker d'images
    private final MediaTracker tracker;

    // Méthodes abstraites que les classes dérivées devront implémenter

    // Charge une image depuis un fichier
    abstract protected Image getImage(final String filename);

    // Crée une nouvelle image
    abstract protected Image createImage(final ImageProducer producer);

    /**
     * Constructeur de la classe ImageLoader
     */
    public ImageLoader(Component component)
    {
	// Création du tracker
	tracker = new MediaTracker(component);
    }

    /**
     * Charge une image simple
     */
    public Image load(final String filename)
    {
	// Image à charger
	final Image image = getImage(filename);

	// Ajout de l'image au tracker
	tracker.addImage(image, 0);

	// Retourne l'image en cours de chargement
	return image;
    }

    /**
     * Crée un tableau d'images (2 dimensions) à partir d'une image contenant
     * en fait une grille d'images
     */
    public Image[][] load(final String filename, final int cols,
			  final int rows)
    {
	return load(filename, cols, rows, DEF_SPRT_WIDTH, DEF_SPRT_HEIGHT);
    }

    /**
     * Crée un tableau d'images (2 dimensions) à partir d'une image contenant
     * en fait une grille d'images avec une taille de sprite personnalisée
     */
    public Image[][] load(final String filename,
			  final int cols, final int rows,
			  final int spriteWidth, final int spriteHeight)
    {
	// Création du tableau d'images
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
     * une taile de sprite personnalisée
     */
    public void load(final String filename, final Image[][] images,
		     final int spriteWidth, final int spriteHeight)
    {
	// Image contenant une grille d'images plus petites
	final Image array = getImage(filename);

	// Si le paramètre est valide
	if (images != null && images.length != 0) {
	    // Objets servant à la découpe de l'image
	    CropImageFilter     filter;
	    FilteredImageSource source;

	    // Création des images contenues dans la grille d'images
	    for (int y = 0; y < images.length; y++)
		for (int x = 0; x < images[y].length; x++) {
		    // Découpe l'image
		    filter = new CropImageFilter(x * spriteWidth,
						 y * spriteHeight,
						 spriteWidth, spriteHeight);
		    source = new FilteredImageSource(array.getSource(),
						     filter);

		    // Stocke le résultat dans le tableau
		    images[y][x] = createImage(source);

		    // Ajout de l'image au tracker
		    tracker.addImage(images[y][x], 0);
		}
	}
    }

    /**
     * Attend que toutes les images soient chargées
     */
    public void waitLoading()
    {
	// Gestion des exceptions
	try {
	    // Attend le chargement des images
	    tracker.waitForAll();

	    // Gestion des erreurs
	    if (tracker.isErrorAny())
		throw new IllegalArgumentException("Sprites non chargés");
	} catch (Exception e) { /* Ne réagit pas aux erreurs éventuelles */ }
    }
}

// Fin du fichier
