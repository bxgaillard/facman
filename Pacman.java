/*
 * ---------------------------------------------------------------------------
 *
 * Facman -- Un cl�ne de Pacman en Java
 * Copyright (c) 2004 Benjamin Gaillard & Lionel Imbs
 *
 * ---------------------------------------------------------------------------
 *
 * Fichier     : Pacman.java
 *
 * Description : La classe Pacman est d�riv�e de la classe Character. Elle
 *               g�re tout ce qui est en rapport avec le contr�le, le
 *               d�placement et l'affichage du personnage contr�lable par le
 *               joueur, en l'occurence un Pacman.
 *
 * Commentaire : C'est dans ce fichier que se situe toute la gestion du
 *               gameplay, qui a �t� inspir� du jeu Greedy (un autre cl�ne de
 *               Pacman) plut�t que du Pacman originel de Namco suivant nos
 *               go�ts personnels. Si le gameplay de convient pas, il suffit
 *               de modifier la m�thode doMove().
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
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * La classe Pacman, g�rant les actions du joueur ainsi que les mouvements et
 * l'affichage du Pacman
 */
class Pacman extends Character implements KeyListener
{
    // Variables relatives aux touches appuy�es
    private final boolean[] keys = {false, false, false, false};
    private       int       pressed = 0;

    // Variables relatives aux sprites
    private static final int       IMAGES = 5, IMAGE_SIZE = 32;
    private        final Image[][] images;
    private              int       image = 0, nextImage = 1;

    // Variables relatives � l'animation
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
	// Teste si on a assez attendu pour passer � l'image suivante
	if (++anim == ANIM_FREEZE) {
	    // Met � z�ro le compteur
	    anim = 0;

	    // S�lectionne la prochaine image de l'animation
	    image += nextImage;

	    // Si on arrive en bout de s�rie, on change le sens de l'animation
	    if (image == 0)
		nextImage = 1;
	    else if (image == IMAGES - 1)
		nextImage = -1;
	}

	// Finalement, dessine l'image s�lection�e
	board.drawImage(gc, images[lastDir][image], pos);
    }

    /**
     * M�thode appel�e r�guli�rement pour �ventuellement effectuer un
     * mouvement et mettre � jour les variables de position ; c'est de cette
     * m�thode que d�pend le gameplay
     */
    public void doMove()
    {
	final int opposite = oppositeDir(lastDir);

	if (canMove(lastDir) && ((pressed == 1 && keys[lastDir]) ||
				 (pressed > 0 && !keys[opposite] &&
				  (offset.x != 0 || offset.y != 0))))
	    // Si on n'est pas encore au centre d'une case et que le mouvement
	    // s�lectionn� est diff�rent, ou que le mouvement s�lectionn� est
	    // le m�me, on continue dans la m�me direction
	    move(lastDir);
	else {
	    int dir;

	    // Sinon, on regarde quelles directions sont s�lectionn�es et on
	    // effectue le mouvement correspondant si c'est possible
	    for (dir = LEFT; dir <= DOWN; dir++)
		if (dir != lastDir && keys[dir] && canMove(dir)) {
		    move(dir);
		    break;
		}

	    // Si le mouvement pr�c�dent n'est pas possible mais qu'une touche
	    // est encore enfonc�e, on continue dans la direction du dernier
	    // mouvement effectu�, si c'est possible
	    if (dir > DOWN && pressed > 0 &&
		canMove(lastDir) && !keys[opposite])
		move(lastDir);
	}

	// Si l'on est pr�s du centre d'une case, mange la pastille qui s'y
	// trouve
	if (Math.abs(offset.x) <= boxSize / 4 &&
	    Math.abs(offset.y) <= boxSize / 4)
	    board.eatGum(boxPos);
    }

    /**
     * Choisit une direction parmi les 4 disponible (gauche, droite, haut, bas)
     * en fonctions du code de la touche pass� en param�tre
     */
    private static int selectKey(final int keyCode)
    {
	// Gauche : touches fl�che gauche et 4/gauche du pav� num�rique
	switch (keyCode) {
	case KeyEvent.VK_LEFT:
	case KeyEvent.VK_KP_LEFT:
	case KeyEvent.VK_NUMPAD4:
	    return LEFT;

	// Droite : touches fl�che droite et 6/droite du pav� num�rique
	case KeyEvent.VK_RIGHT:
	case KeyEvent.VK_KP_RIGHT:
	case KeyEvent.VK_NUMPAD6:
	    return RIGHT;

	// Gauche : touches fl�che haut et 8/haut du pav� num�rique
	case KeyEvent.VK_UP:
	case KeyEvent.VK_KP_UP:
	case KeyEvent.VK_NUMPAD8:
	    return UP;

	// Gauche : touches fl�che bas et 2/bas du pav� num�rique
	case KeyEvent.VK_DOWN:
	case KeyEvent.VK_KP_DOWN:
	case KeyEvent.VK_NUMPAD2:
	    return DOWN;
	}

	// La touche appuy�e ne correspond pas � une direction
	return -1;
    }

    /**
     * M�thode appel�e quand une touche est enfonc�e
     */
    public void keyPressed(final KeyEvent event)
    {
	// S�lectionne une direction si c'est une touche de direction
	final int key = selectKey(event.getKeyCode());

	// Si c'est une direction, indique qu'elle est entr�e par l'utilisateur
	if (key != -1 && !keys[key]) {
	    keys[key] = true;
	    pressed++;
	}
    }

    /**
     * M�thode appel�e quand une touche est rel�ch�e
     */
    public void keyReleased(final KeyEvent event)
    {
	// S�lectionne une direction si c'est une touche de direction
	final int key = selectKey(event.getKeyCode());

	// Si c'est une direction, indique qu'elle n'est plus voulue
	if (key != -1 && keys[key]) {
	    keys[key] = false;
	    pressed--;
	}
    }

    /**
     * M�thode appel�e quand une touche g�n�re un �v�nement "entr�e d'un
     * caract�re", ce qui peut arriver plusieurs fois lors d'un seul
     * enfoncement de touche � intervalle r�gulier (r�p�tition)
     */
    public void keyTyped(final KeyEvent event) { /* On ne s'en sert pas */ }
}

// Fin de fichier
