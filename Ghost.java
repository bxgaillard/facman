/*
 * ---------------------------------------------------------------------------
 *
 * Facman -- Un clône de Pacman en Java
 * Copyright (c) 2004 Benjamin Gaillard & Lionel Imbs
 *
 * ---------------------------------------------------------------------------
 *
 * Fichier     : Ghost.java
 *
 * Description : La classe Ghost est dérivée de la classe Character. Elle gère
 *               tout ce qui est en rapport avec le déplacement,
 *               l'intelligence artificielle et l'affichage des fantômes.
 *
 * Commentaire : C'est dans ce fichier que se situe toute l'intelligence
 *               artificielle des fantômes, dans la méthode doMove().
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

/**
 * La classe Ghost, gérant les fantômes avec leur intelligence artificielle et
 * leur affichage
 */
class Ghost extends Character
{
    // Variables relatives aux sprites
    private static final int       IMAGES = 6, IMAGE_SIZE = 32;
    private static       Image[][] fearImages = null;
    private        final Image[][] images;
    private              int       image  = 0;

    // État du fantôme
    private static final int     MAX_FEAR_LENGTH = 300;
    private              boolean dead, afraid;
    private              int     fearLength;

    // Variables relatives à l'animation
    private static final int ANIM_FREEZE = 2;
    private              int anim        = 0;

    // Variables utilisées pour le calcul de la direction dans doMove()
    private static final int X1 = 0, X2 = 3, Y1 = 1, Y2 = 2;

    // Niveau de difficulté
    private int difficulty = 1;

    // Objets auxiliaires
    private final Pacman pacman;

    /**
     * Constructeur de la classe Ghost
     */
    public Ghost(final Board board, final ImageLoader loader, final int index,
		 final Pacman pacman)
    {
	// Appel du constructeur de la superclasse (classe parente)
	super(board, DEFAULT_ANIM_OFFSET / 2);

	// Initialisation des variables
	this.pacman = pacman;

	// Chargement des sprites
	images = loader.load("ghost" + index, IMAGES, NB_DIRS);
	if (fearImages == null)
	    fearImages = loader.load("ghosta", IMAGES, NB_DIRS);
    }

    /**
     * Initialise la position
     */
    public void initPosition()
    {
	initPosition(true);
    }

    /**
     * Définit le niveau de difficulté
     */
    public void setDifficulty(final int difficulty)
    {
	this.difficulty = difficulty;

	// Mise à jour de la vitesse du fantôme
	moves = Position.makeDirs(difficulty <= 1 ? DEFAULT_ANIM_OFFSET / 2 :
				  DEFAULT_ANIM_OFFSET);
    }

    /**
     * Dessine le fantôme
     */
    public void draw(final Graphics gc)
    {
	// N'affiche que s'il est "vivant"
	if (dead)
	    return;

	// Si la direction n'est pas encore définie, prend la première
	final int dir = lastDir >= 0 ? lastDir : 0;

	// Teste si on a assez attendu pour passer à l'image suivante
	if (++anim == ANIM_FREEZE) {
	    // Met à zéro le compteur
	    anim = 0;

	    // Sélectionne la prochaine image de l'animation
	    if (image < IMAGES - 1)
		image++;
	    else
		image = 0;
	}

	// Finalement, dessine l'image sélectionée
	board.drawImage(gc, ((afraid && (fearLength >= 64 ||
					 ((fearLength / 8) % 2) == 0)) ?
			     fearImages : images)[dir][image], pos);
    }

    /**
     * Méthode appelée régulièrement pour éventuellement effectuer un
     * mouvement et mettre à jour les variables de position ; c'est de cette
     * méthode que dépend l'intelligence artificielle
     */
    public void doMove()
    {
	// Ne déplace que s'il est "vivant"
	if (dead)
	    return;

	// S'aligne toujours sur une case
	if (offset.x != 0 || offset.y != 0)
	    move(lastDir);
	else {
	    final int oppDir = oppositeDir(lastDir);
	    final int[] dirs = new int[NB_DIRS];
	    int dir;

	    if (difficulty == 0) {
		int totalDirs = 0;

		// Sélectionne les directions possibles en évitant de
		// retourner en arrière (direction opposée)
		for (dir = 0; dir < NB_DIRS; dir++)
		    if (dir != oppDir && canMove(dir))
			dirs[totalDirs++] = dir;

		if (totalDirs == 1)
		    // Sélectionne la seule direction possible
		    dir = dirs[0];
		else if (totalDirs > 0)
		    // Sélectionne une direction au hasard si c'est possible
		    dir = dirs[(int) (Math.random() * totalDirs)];
		else if (canMove(oppDir))
		    // Sinon il faut retourner en arrière
		    dir = oppDir;
		else
		    // Aucun mouvement n'est possible
		    return;
	    } else {
		final Position pacmanPos = pacman.getPosition();

		// Déplacement en X
		if (pos.x < pacmanPos.x) {
		    dirs[X1] = RIGHT;
		    dirs[X2] = LEFT;
		} else if (pos.x >= pacmanPos.x) {
		    dirs[X1] = LEFT;
		    dirs[X2] = RIGHT;
		}

		// Déplacement en Y
		if (pos.y < pacmanPos.y) {
		    dirs[Y1] = DOWN;
		    dirs[Y2] = UP;
		} else if (pos.y >= pacmanPos.y) {
		    dirs[Y1] = UP;
		    dirs[Y2] = DOWN;
		}

		// Suivant la distance par rapport à Pacman et X et en Y,
		// inverse les déplacements en X ou en Y
		if (Math.abs(pos.x - pacmanPos.x) <
		    Math.abs(pos.y - pacmanPos.y)) {
		    dir = dirs[X1];
		    dirs[X1] = dirs[Y1];
		    dirs[Y1] = dir;

		    dir = dirs[X2];
		    dirs[X2] = dirs[Y2];
		    dirs[Y2] = dir;
		}

		// Si le fantôme est effrayé, inverse le sens de chaque
		// direction
		if (afraid) {
		    dir = dirs[X1];
		    dirs[X1] = dirs[X2];
		    dirs[X2] = dir;

		    dir = dirs[Y1];
		    dirs[Y1] = dirs[Y2];
		    dirs[Y2] = dir;
		}

		// Sélectionne la première direction possible en évitant de
		// revenir en arrière
		for (dir = 0; dir < NB_DIRS; dir++)
		    if (dirs[dir] != oppDir && canMove(dirs[dir])) {
			dir = dirs[dir];
			break;
		    }

		if (dir == NB_DIRS) {
		    if (canMove(lastDir))
			dir = lastDir;
		    else if (canMove(oppDir))
			// Il faut retourner en arrière
			dir = oppDir;
		    else
			// Aucune direction
			return;
		}
	    }

	    // Effectue le mouvement
	    move(dir);
	}

	// Réduction du délai de la peur
	if (fearLength > 0)
	    fearLength--;
	else {
	    afraid = false;
	}
    }

    /**
     * Fait "mourir" le fantôme
     */
    public void die()
    {
	dead = true;
    }

    /**
     * Fait "revivre" le fantôme
     */
    public void undie()
    {
	dead   = false;
	afraid = false;
	initPosition();
    }

    /**
     * Retourne l'état du fantôme ("mort" ou "vivant")
     */
    public boolean isDead()
    {
	return dead;
    }

    /**
     * Rend le fantôme appeuré (Pacman mange une super-pastille)
     */
    public void fear()
    {
	if (!dead) {
	    afraid = true;
	    fearLength = MAX_FEAR_LENGTH;
	}
    }

    /**
     * Permet de savoir si le fantôme est appeuré
     */
    public boolean isAfraid()
    {
	return afraid;
    }
}

// Fin de fichier
