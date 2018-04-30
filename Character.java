/*
 * ---------------------------------------------------------------------------
 *
 * Facman -- Un cl�ne de Pacman en Java
 * Copyright (c) 2004 Benjamin Gaillard & Lionel Imbs
 *
 * ---------------------------------------------------------------------------
 *
 * Fichier     : Character.java
 *
 * Description : La classe dont d�rivent Pacman et Ghost. Elle sert � d�f�nir
 *               les m�thodes qu'elles devront impl�menter et d�finit aussi
 *               des m�thodes communes aux deux classes.
 *
 * Commentaire : C'est ici que sont cod�es les fonctions permettant de tester
 *               et d'effectuer les mouvements des personnages.
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

/**
 * La classe Character, classe de base pour les classes Pacman et Ghost,
 * indiquant les m�thodes abstraites � d�finir et d�clarant quelques m�thodes
 * utiles � ces sous-classes
 */
abstract class Character
{
    // Variables relatives aux directions
    protected static final int NB_DIRS = 4;
    protected static final int DEFAULT_ANIM_OFFSET = 4;
    protected static final int LEFT = 0, RIGHT = 1, UP = 2, DOWN = 3;
    protected static final String[] DIRS = {"left", "right", "up", "down"};
    protected              Position[] moves;

    // Variables relatives � la position
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
     * Constructeur de la classe Character avec le d�placement par d�faut
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
     * Retourne la position en tant que d�calage par rapport � une case
     */
    public Position getBoxOffset()
    {
	return offset;
    }

    /**
     * Calcule la direction oppos�e � celle pass�e en param�tre
     */
    protected static int oppositeDir(final int dir)
    {
	// Teste la validit� de la direction
	if (dir >= 0 && dir < NB_DIRS)
	    // Direction oppos�e
	    return (NB_DIRS + 1 - dir) % NB_DIRS;

	// Direction invalide
	return -1;
    }

    /**
     * Teste si un mouvement dans la direction donn�e est possible
     * (c'est-�-dire si un mur ne bloque pas le passage)
     */
    protected boolean canMove(final int dir)
    {
	// Teste si la direction est valide
	if (dir < 0 || dir >= NB_DIRS)
	    return false;

	// Variables servant � calculer la nouvelle position
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

	// Teste si la case vis�e est un mur
	if (board.isBoxWall(newBoxPos))
	    return false;

	// Si on n'est pas align� sur la prochaine case, fait des tests
	// suppl�mentaires
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
     * Met � jour les variables relatives � la position pour effectuer un
     * mouvement
     */
    protected void move(final int dir)
    {
	// Si la direction est valide
	if (dir >= 0 && dir < NB_DIRS) {
	    // Mise � jour des variables de position
	    offset.move(moves[dir]);
	    pos.move(moves[dir]);

	    // Change de case si le d�calage devient assez grand
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

	    // Sauvegarde de la derni�re direction prise
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

    // M�thodes abstraites
    abstract public void draw(final Graphics gc);
    abstract public void doMove();
}

// Fin du fichier
