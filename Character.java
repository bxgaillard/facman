/*
 * ---------------------------------------------------------------------------
 *
 * Facman -- Un clône de Pacman en Java
 * Copyright (c) 2004 Benjamin Gaillard & Lionel Imbs
 *
 * ---------------------------------------------------------------------------
 *
 * Fichier     : Character.java
 *
 * Description : La classe dont dérivent Pacman et Ghost. Elle sert à défénir
 *               les méthodes qu'elles devront implémenter et définit aussi
 *               des méthodes communes aux deux classes.
 *
 * Commentaire : C'est ici que sont codées les fonctions permettant de tester
 *               et d'effectuer les mouvements des personnages.
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

/**
 * La classe Character, classe de base pour les classes Pacman et Ghost,
 * indiquant les méthodes abstraites à définir et déclarant quelques méthodes
 * utiles à ces sous-classes
 */
abstract class Character
{
    // Variables relatives aux directions
    protected static final int NB_DIRS = 4;
    protected static final int DEFAULT_ANIM_OFFSET = 4;
    protected static final int LEFT = 0, RIGHT = 1, UP = 2, DOWN = 3;
    protected static final String[] DIRS = {"left", "right", "up", "down"};
    protected              Position[] moves;

    // Variables relatives à la position
    protected final int boxSize;
    protected final Position pos    = new Position();
    protected final Position boxPos = new Position(), offset = new Position();
    protected int lastDir;

    // Plateau de jeu
    protected final Board board;

    /**
     * Constructeur de la classe Character
     */
    protected Character(final Board board, final int animOffset)
    {
	// Initialisation des variables
	moves = Position.makeDirs(animOffset);
	this.board = board;
	boxSize = board.getBoxSize();
    }

    /**
     * Constructeur de la classe Character avec le déplacement par défaut
     */
    public Character(final Board board)
    {
	this(board, DEFAULT_ANIM_OFFSET);
    }

    /**
     * Initialise la position
     */
    protected void initPosition(final boolean character)
    {
	lastDir = board.getInitialDir(character);
	boxPos.setPosition(board.getInitialPosition(character));
	pos.setPosition(boxPos.x * boxSize, boxPos.y * boxSize);
	offset.setPosition(0, 0);
    }

    /**
     * Retourne la position en pixels
     */
    public Position getPosition()
    {
 	return pos;
    }

    /**
     * Retourne la position en cases
     */
    public Position getBoxPosition()
    {
	return boxPos;
    }

    /**
     * Retourne la position en tant que décalage par rapport à une case
     */
    public Position getBoxOffset()
    {
	return offset;
    }

    /**
     * Calcule la direction opposée à celle passée en paramètre
     */
    protected static int oppositeDir(final int dir)
    {
	// Teste la validité de la direction
	if (dir >= 0 && dir < NB_DIRS)
	    // Direction opposée
	    return (NB_DIRS + 1 - dir) % NB_DIRS;

	// Direction invalide
	return -1;
    }

    /**
     * Teste si un mouvement dans la direction donnée est possible
     * (c'est-à-dire si un mur ne bloque pas le passage)
     */
    protected boolean canMove(final int dir)
    {
	// Teste si la direction est valide
	if (dir < 0 || dir >= NB_DIRS)
	    return false;

	// Variables servant à calculer la nouvelle position
	final Position newOffset = new Position(offset);
	final Position newBoxPos = new Position(boxPos);
	newOffset.move(moves[dir]);

	// Effectue un changement de case si besoin est
	if (dir == LEFT && newOffset.x < 0)
	    newBoxPos.x--;
	else if (dir == RIGHT && newOffset.x > 0)
	    newBoxPos.x++;
	else if (dir == UP && newOffset.y < 0)
	    newBoxPos.y--;
	else if (dir == DOWN && newOffset.y > 0)
	    newBoxPos.y++;
	else
	    return true;

	// Teste si la case visée est un mur
	if (board.isBoxWall(newBoxPos))
	    return false;

	// Si on n'est pas aligné sur la prochaine case, fait des tests
	// supplémentaires
	if (dir == LEFT || dir == RIGHT) {
	    if (newOffset.y < 0)
		newBoxPos.y--;
	    else if (newOffset.y > 0)
		newBoxPos.y++;
	} else {
	    if (newOffset.x < 0)
		newBoxPos.x--;
	    else if (newOffset.x > 0)
		newBoxPos.x++;
	}

	// Teste si la case adjacente (si non alignement) est un mur
	return !board.isBoxWall(newBoxPos);
    }

    /**
     * Met à jour les variables relatives à la position pour effectuer un
     * mouvement
     */
    protected void move(final int dir)
    {
	// Si la direction est valide
	if (dir >= 0 && dir < NB_DIRS) {
	    // Mise à jour des variables de position
	    offset.move(moves[dir]);
	    pos.move(moves[dir]);

	    // Change de case si le décalage devient assez grand
	    if (offset.x <= -boxSize / 2) {
		boxPos.x--;
		offset.x += boxSize;
	    } else if (offset.x > boxSize / 2) {
		boxPos.x++;
		offset.x -= boxSize;
	    }
	    if (offset.y <= -boxSize / 2) {
		boxPos.y--;
		offset.y += boxSize;
	    } else if (offset.y > boxSize / 2) {
		boxPos.y++;
		offset.y -= boxSize;
	    }

	    // Sauvegarde de la dernière direction prise
	    lastDir = dir;
	}

	Position teleporter;
	if (offset.x == 0 && offset.y == 0 &&
	    (teleporter = board.getTeleporter(boxPos)) != null) {
	    boxPos.setPosition(teleporter);
	    pos.setPosition(boxPos.x * boxSize, boxPos.y * boxSize);
	}
    }

    public boolean collides(final Character chr)
    {
	final Position pos2 = chr.getPosition();

	return Math.abs(pos.x - pos2.x) <= boxSize / 2 &&
	    Math.abs(pos.y - pos2.y) <= boxSize / 2;
    }

    // Méthodes abstraites
    abstract public void draw(final Graphics gc);
    abstract public void doMove();
}

// Fin du fichier
