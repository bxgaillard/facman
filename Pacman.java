/*
 * ---------------------------------------------------------------------------
 *
 * Facman -- Un clône de Pacman en Java
 * Copyright (c) 2004 Benjamin Gaillard & Lionel Imbs
 *
 * ---------------------------------------------------------------------------
 *
 * Fichier     : Pacman.java
 *
 * Description : La classe Pacman est dérivée de la classe Character. Elle
 *               gère tout ce qui est en rapport avec le contrôle, le
 *               déplacement et l'affichage du personnage contrôlable par le
 *               joueur, en l'occurence un Pacman.
 *
 * Commentaire : C'est dans ce fichier que se situe toute la gestion du
 *               gameplay, qui a été inspiré du jeu Greedy (un autre clône de
 *               Pacman) plutôt que du Pacman originel de Namco suivant nos
 *               goûts personnels. Si le gameplay de convient pas, il suffit
 *               de modifier la méthode doMove().
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
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * La classe Pacman, gérant les actions du joueur ainsi que les mouvements et
 * l'affichage du Pacman
 */
class Pacman extends Character implements KeyListener
{
    // Variables relatives aux touches appuyées
    private final boolean[] keys = {false, false, false, false};
    private       int       pressed = 0;

    // Variables relatives aux sprites
    private static final int       IMAGES = 5, IMAGE_SIZE = 32;
    private        final Image[][] images;
    private              int       image = 0, nextImage = 1;

    // Variables relatives à l'animation
    private static final int ANIM_FREEZE = 2;
    private              int anim = 0;

    /**
     * Constructeur de la classe Pacman
     */
    public Pacman(final Board board, final ImageLoader loader,
		  final Component component)
    {
	// Appel du constructeur de la superclasse (classe parente)
	super(board);

	// Initialisation des variables
	component.addKeyListener(this);

	// Chargement des sprites
	images = loader.load("pacman", IMAGES, NB_DIRS);
    }

    /**
     * Initialise la position
     */
    public void initPosition()
    {
	initPosition(false);
    }

    /**
     * Dessine le Pacman
     */
    public void draw(final Graphics gc)
    {
	// Teste si on a assez attendu pour passer à l'image suivante
	if (++anim == ANIM_FREEZE) {
	    // Met à zéro le compteur
	    anim = 0;

	    // Sélectionne la prochaine image de l'animation
	    image += nextImage;

	    // Si on arrive en bout de série, on change le sens de l'animation
	    if (image == 0)
		nextImage = 1;
	    else if (image == IMAGES - 1)
		nextImage = -1;
	}

	// Finalement, dessine l'image sélectionée
	board.drawImage(gc, images[lastDir][image], pos);
    }

    /**
     * Méthode appelée régulièrement pour éventuellement effectuer un
     * mouvement et mettre à jour les variables de position ; c'est de cette
     * méthode que dépend le gameplay
     */
    public void doMove()
    {
	final int opposite = oppositeDir(lastDir);

	if (canMove(lastDir) && ((pressed == 1 && keys[lastDir]) ||
				 (pressed > 0 && !keys[opposite] &&
				  (offset.x != 0 || offset.y != 0))))
	    // Si on n'est pas encore au centre d'une case et que le mouvement
	    // sélectionné est différent, ou que le mouvement sélectionné est
	    // le même, on continue dans la même direction
	    move(lastDir);
	else {
	    int dir;

	    // Sinon, on regarde quelles directions sont sélectionnées et on
	    // effectue le mouvement correspondant si c'est possible
	    for (dir = LEFT; dir <= DOWN; dir++)
		if (dir != lastDir && keys[dir] && canMove(dir)) {
		    move(dir);
		    break;
		}

	    // Si le mouvement précédent n'est pas possible mais qu'une touche
	    // est encore enfoncée, on continue dans la direction du dernier
	    // mouvement effectué, si c'est possible
	    if (dir > DOWN && pressed > 0 &&
		canMove(lastDir) && !keys[opposite])
		move(lastDir);
	}

	// Si l'on est près du centre d'une case, mange la pastille qui s'y
	// trouve
	if (Math.abs(offset.x) <= boxSize / 4 &&
	    Math.abs(offset.y) <= boxSize / 4)
	    board.eatGum(boxPos);
    }

    /**
     * Choisit une direction parmi les 4 disponible (gauche, droite, haut, bas)
     * en fonctions du code de la touche passé en paramètre
     */
    private static int selectKey(final int keyCode)
    {
	// Gauche : touches flèche gauche et 4/gauche du pavé numérique
	switch (keyCode) {
	case KeyEvent.VK_LEFT:
	case KeyEvent.VK_KP_LEFT:
	case KeyEvent.VK_NUMPAD4:
	    return LEFT;

	// Droite : touches flèche droite et 6/droite du pavé numérique
	case KeyEvent.VK_RIGHT:
	case KeyEvent.VK_KP_RIGHT:
	case KeyEvent.VK_NUMPAD6:
	    return RIGHT;

	// Gauche : touches flèche haut et 8/haut du pavé numérique
	case KeyEvent.VK_UP:
	case KeyEvent.VK_KP_UP:
	case KeyEvent.VK_NUMPAD8:
	    return UP;

	// Gauche : touches flèche bas et 2/bas du pavé numérique
	case KeyEvent.VK_DOWN:
	case KeyEvent.VK_KP_DOWN:
	case KeyEvent.VK_NUMPAD2:
	    return DOWN;
	}

	// La touche appuyée ne correspond pas à une direction
	return -1;
    }

    /**
     * Méthode appelée quand une touche est enfoncée
     */
    public void keyPressed(final KeyEvent event)
    {
	// Sélectionne une direction si c'est une touche de direction
	final int key = selectKey(event.getKeyCode());

	// Si c'est une direction, indique qu'elle est entrée par l'utilisateur
	if (key != -1 && !keys[key]) {
	    keys[key] = true;
	    pressed++;
	}
    }

    /**
     * Méthode appelée quand une touche est relâchée
     */
    public void keyReleased(final KeyEvent event)
    {
	// Sélectionne une direction si c'est une touche de direction
	final int key = selectKey(event.getKeyCode());

	// Si c'est une direction, indique qu'elle n'est plus voulue
	if (key != -1 && keys[key]) {
	    keys[key] = false;
	    pressed--;
	}
    }

    /**
     * Méthode appelée quand une touche génère un événement "entrée d'un
     * caractère", ce qui peut arriver plusieurs fois lors d'un seul
     * enfoncement de touche à intervalle régulier (répétition)
     */
    public void keyTyped(final KeyEvent event) { /* On ne s'en sert pas */ }
}

// Fin de fichier
