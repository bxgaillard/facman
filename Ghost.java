/*
 * ---------------------------------------------------------------------------
 *
 * Facman -- Un cl�ne de Pacman en Java
 * Copyright (c) 2004 Benjamin Gaillard & Lionel Imbs
 *
 * ---------------------------------------------------------------------------
 *
 * Fichier     : Ghost.java
 *
 * Description : La classe Ghost est d�riv�e de la classe Character. Elle g�re
 *               tout ce qui est en rapport avec le d�placement,
 *               l'intelligence artificielle et l'affichage des fant�mes.
 *
 * Commentaire : C'est dans ce fichier que se situe toute l'intelligence
 *               artificielle des fant�mes, dans la m�thode doMove().
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

/**
 * La classe Ghost, g�rant les fant�mes avec leur intelligence artificielle et
 * leur affichage
 */
class Ghost extends Character
{
    // Variables relatives aux sprites
    private static final int       IMAGES = 6, IMAGE_SIZE = 32;
    private static       Image[][] fearImages = null;
    private        final Image[][] images;
    private              int       image  = 0;

    // �tat du fant�me
    private static final int     MAX_FEAR_LENGTH = 300;
    private              boolean dead, afraid;
    private              int     fearLength;

    // Variables relatives � l'animation
    private static final int ANIM_FREEZE = 2;
    private              int anim        = 0;

    // Variables utilis�es pour le calcul de la direction dans doMove()
    private static final int X1 = 0, X2 = 3, Y1 = 1, Y2 = 2;

    // Niveau de difficult�
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
     * D�finit le niveau de difficult�
     */
    public void setDifficulty(final int difficulty)
    {
	this.difficulty = difficulty;

	// Mise � jour de la vitesse du fant�me
	moves = Position.makeDirs(difficulty <= 1 ? DEFAULT_ANIM_OFFSET / 2 :
				  DEFAULT_ANIM_OFFSET);
    }

    /**
     * Dessine le fant�me
     */
    public void draw(final Graphics gc)
    {
	// N'affiche que s'il est "vivant"
	if (dead)
	    return;

	// Si la direction n'est pas encore d�finie, prend la premi�re
	final int dir = lastDir >= 0 ? lastDir : 0;

	// Teste si on a assez attendu pour passer � l'image suivante
	if (++anim == ANIM_FREEZE) {
	    // Met � z�ro le compteur
	    anim = 0;

	    // S�lectionne la prochaine image de l'animation
	    if (image < IMAGES - 1)
		image++;
	    else
		image = 0;
	}

	// Finalement, dessine l'image s�lection�e
	board.drawImage(gc, ((afraid && (fearLength >= 64 ||
					 ((fearLength / 8) % 2) == 0)) ?
			     fearImages : images)[dir][image], pos);
    }

    /**
     * M�thode appel�e r�guli�rement pour �ventuellement effectuer un
     * mouvement et mettre � jour les variables de position ; c'est de cette
     * m�thode que d�pend l'intelligence artificielle
     */
    public void doMove()
    {
	// Ne d�place que s'il est "vivant"
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

		// S�lectionne les directions possibles en �vitant de
		// retourner en arri�re (direction oppos�e)
		for (dir = 0; dir < NB_DIRS; dir++)
		    if (dir != oppDir && canMove(dir))
			dirs[totalDirs++] = dir;

		if (totalDirs == 1)
		    // S�lectionne la seule direction possible
		    dir = dirs[0];
		else if (totalDirs > 0)
		    // S�lectionne une direction au hasard si c'est possible
		    dir = dirs[(int) (Math.random() * totalDirs)];
		else if (canMove(oppDir))
		    // Sinon il faut retourner en arri�re
		    dir = oppDir;
		else
		    // Aucun mouvement n'est possible
		    return;
	    } else {
		final Position pacmanPos = pacman.getPosition();

		// D�placement en X
		if (pos.x < pacmanPos.x) {
		    dirs[X1] = RIGHT;
		    dirs[X2] = LEFT;
		} else if (pos.x >= pacmanPos.x) {
		    dirs[X1] = LEFT;
		    dirs[X2] = RIGHT;
		}

		// D�placement en Y
		if (pos.y < pacmanPos.y) {
		    dirs[Y1] = DOWN;
		    dirs[Y2] = UP;
		} else if (pos.y >= pacmanPos.y) {
		    dirs[Y1] = UP;
		    dirs[Y2] = DOWN;
		}

		// Suivant la distance par rapport � Pacman et X et en Y,
		// inverse les d�placements en X ou en Y
		if (Math.abs(pos.x - pacmanPos.x) <
		    Math.abs(pos.y - pacmanPos.y)) {
		    dir = dirs[X1];
		    dirs[X1] = dirs[Y1];
		    dirs[Y1] = dir;

		    dir = dirs[X2];
		    dirs[X2] = dirs[Y2];
		    dirs[Y2] = dir;
		}

		// Si le fant�me est effray�, inverse le sens de chaque
		// direction
		if (afraid) {
		    dir = dirs[X1];
		    dirs[X1] = dirs[X2];
		    dirs[X2] = dir;

		    dir = dirs[Y1];
		    dirs[Y1] = dirs[Y2];
		    dirs[Y2] = dir;
		}

		// S�lectionne la premi�re direction possible en �vitant de
		// revenir en arri�re
		for (dir = 0; dir < NB_DIRS; dir++)
		    if (dirs[dir] != oppDir && canMove(dirs[dir])) {
			dir = dirs[dir];
			break;
		    }

		if (dir == NB_DIRS) {
		    if (canMove(lastDir))
			dir = lastDir;
		    else if (canMove(oppDir))
			// Il faut retourner en arri�re
			dir = oppDir;
		    else
			// Aucune direction
			return;
		}
	    }

	    // Effectue le mouvement
	    move(dir);
	}

	// R�duction du d�lai de la peur
	if (fearLength > 0)
	    fearLength--;
	else {
	    afraid = false;
	}
    }

    /**
     * Fait "mourir" le fant�me
     */
    public void die()
    {
	dead = true;
    }

    /**
     * Fait "revivre" le fant�me
     */
    public void undie()
    {
	dead   = false;
	afraid = false;
	initPosition();
    }

    /**
     * Retourne l'�tat du fant�me ("mort" ou "vivant")
     */
    public boolean isDead()
    {
	return dead;
    }

    /**
     * Rend le fant�me appeur� (Pacman mange une super-pastille)
     */
    public void fear()
    {
	if (!dead) {
	    afraid = true;
	    fearLength = MAX_FEAR_LENGTH;
	}
    }

    /**
     * Permet de savoir si le fant�me est appeur�
     */
    public boolean isAfraid()
    {
	return afraid;
    }
}

// Fin de fichier
