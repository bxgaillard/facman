/*
 * ---------------------------------------------------------------------------
 *
 * Facman -- Un clône de Pacman en Java
 * Copyright (c) 2004 Benjamin Gaillard & Lionel Imbs
 *
 * ---------------------------------------------------------------------------
 *
 * Fichier     : Board.java
 *
 * Description : La classe Board gère la grille de jeu. C'est elle qui est
 *               chargée d'initialiser les niveaux ainsi que la position et la
 *               direction initiale des personnages.
 *
 * Commentaire : Cette classe charge les niveaux depuis les fichiers
 *               levels/level???.txt.
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
import java.util.Vector;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Image;
import java.awt.Component;

/**
 * La classe Board, qui charge les niveaux et initialise le terrain de jeu
 */
class Board
{
    // Variables concernant la taille des cases et de la fenêtre
    private static final int BOX_SIZE = 32;
    private static final int WIDTH = 19, HEIGHT = 13;
    private static final int LEFT = BOX_SIZE / 2, TOP = BOX_SIZE * 3 / 2;

    // Variables concernant les cases et la grille
    private static final byte     BOX_EMPTY = 0;
    private static final byte     BOX_GUM = 1, BOX_SUPERGUM = 2;
    private static final byte     BOX_GHOSTS = 3, BOX_TELEPORTERS = 4;
    private static final byte     BOX_WALL = BOX_TELEPORTERS + 20;
    private        final Vector   boards = new Vector();
    private        final byte[][] board = new byte[HEIGHT][WIDTH];

    // Variables concernant les positions
    private final Position   pacmanPos   = new Position();
    private final Position   ghostPos    = new Position();
    private       int        pacmanDir, ghostDir;
    private final Position[] teleporters = new Position[20];

    // Variables relatives au niveau courant
    private int level;
    private int gumCount;

    // Images
    private final Image[][] background;

    // Objets auxiliaires
    private final ImageLoader loader;
    private final Component component;
    private final Game game;

    /**
     * Constructeur de la classe Board
     */
    public Board(final ImageLoader loader, final Component component,
		 final Game game)
    {
	// Initialisation des variables
	this.loader = loader;
	this.component = component;
	this.game = game;

	// Chargement des images
	background = loader.load("boxes", 3, 2);
    }

    /**
     * Crée les images des cases
     */
    public void initImages()
    {
	Image gum;

	// Met un fond dans les images des pastilles
	for (int i = 0; i < background[1].length; i++) {
	    gum = component.createImage(BOX_SIZE, BOX_SIZE);
	    gum.getGraphics().drawImage(background[0][1], 0, 0, component);
	    gum.getGraphics().drawImage(background[1][i], 0, 0, component);
	    background[1][i] = gum;
	}
    }

    /**
     * Réinitialise le compteur de niveaux ; nextLevel() doit être appelée
     * ensuite pour charger le premier niveau.
     */
    public void resetLevel()
    {
	level = 0;
    }

    /**
     * Charge le niveau suivant
     */
    public boolean nextLevel()
    {
	if (level < boards.size())
	    return load(level++);
	return false;
    }

    /**
     * Initialise un niveau
     */
    private boolean load(final int number)
    {
	// Vérifie le numéro du niveau
	if (number < 0 || number >= boards.size())
	    return false;

	// Récupère le tableau où est stocké le niveau
	final byte[][] array = (byte[][]) boards.elementAt(number);
	char chr;

	// Initialise les téléporteurs
	for (int i = 0; i < teleporters.length; i++)
	    teleporters[i] = null;

	// Initialise chaque case de la grille
	for (int y = 0; y < HEIGHT; y++)
	    for (int x = 0; x < WIDTH; x++)
		switch ((chr = (char) array[y][x])) {
		case '#':
		    board[y][x] = BOX_WALL;
		    break;

		case ' ':
		    board[y][x] = BOX_EMPTY;
		    break;

		case '.':
		    board[y][x] = BOX_GUM;
		    gumCount++;
		    break;

		case 'o':
		    board[y][x] = BOX_SUPERGUM;
		    gumCount++;
		    break;

		case '<':
		    board[y][x] = BOX_EMPTY;
		    pacmanDir = 0;
		    pacmanPos.setPosition(x, y);
		    break;

		case '>':
		    board[y][x] = BOX_EMPTY;
		    pacmanDir = 1;
		    pacmanPos.setPosition(x, y);
		    break;

		case '^':
		    board[y][x] = BOX_EMPTY;
		    pacmanDir = 2;
		    pacmanPos.setPosition(x, y);
		    break;

		case 'v':
		    board[y][x] = BOX_EMPTY;
		    pacmanDir = 3;
		    pacmanPos.setPosition(x, y);
		    break;

		case 'G':
		    board[y][x] = BOX_GHOSTS;
		    ghostPos.setPosition(x, y);
		    break;

		case '\n':
		    while (x < WIDTH)
			board[y][x++] = BOX_WALL;
		    x = -1;
		    y++;
		    break;

		default:
		    if (chr >= '0' && chr <= '9') {
			// Ajout du téléporteur à la liste
			int value = (int) (chr - '0');
			board[y][x] = (byte) (BOX_TELEPORTERS + value);

			if (teleporters[value] == null)
			    // Première occurence du téléporteur
			    teleporters[value] = new Position(x, y);
			else {
			    // Deuxième occurence : échange des coordonnées
			    teleporters[value + 10] = new Position(x, y);
			    board[teleporters[value].y]
				[teleporters[value].x] =
				(byte) (BOX_TELEPORTERS + value + 10);
			}
		    }
		}

	// Niveau chargé correctement
	return true;
    }

    /**
     * Charge un niveau portant le numéro indiqué
     */
    private static byte[][] loadLevel(final int number)
    {
	return loadFile("levels/level" + ((number / 100 % 10)) +
			((number / 10) % 10) + (number % 10) + ".txt");
    }

    /**
     * Charge un niveau contenu dans un fichier
     */
    private static byte[][] loadFile(final String file)
    {
	final byte[][]     board = new byte[HEIGHT][WIDTH];
	final StringBuffer buf = new StringBuffer();
	final InputStream  stream = Board.class.getResourceAsStream(file);
	int x = 0, y = 0, chr;

	// Récupère les octets du fichier
	try {
	    while (y < HEIGHT && (chr = stream.read()) != -1) {
		if (x < WIDTH) {
		    if ((char) chr != '\n')
			board[y][x] = (byte) chr;
		    else {
			// Remplit le reste de la ligne de murs
			while (x < WIDTH)
			    board[y][x++] = '#';
			x = -1;
			y++;
		    }

		    x++;
		} else
		    while (chr != -1) {
			if ((char) chr == '\n') {
			    x = 0;
			    y++;
			    break;
			}
			chr = stream.read();
		    }
	    }

	    stream.close();
	} catch (Exception e) {
	    return null;
	}

	// Remplit ce qui reste de murs
	if (y < HEIGHT) {
	    while (x < WIDTH)
		board[y][x++] = '#';
	    while (y < HEIGHT) {
		for (x = 0; x < WIDTH; x++)
		    board[y][x] = '#';
		y++;
	    }
	}

	// Retour du résultat
	return board;
    }

    /**
     * Charge tous les niveaux
     */
    public void loadLevels()
    {
	byte[][] level;

	// Niveau de 1 à 999
	for (int i = 1; i < 1000; i++)
	    if ((level = loadLevel(i)) != null)
		boards.add(level);
	    else
		break;
    }

    /**
     * Obtient le numéro de niveau courant
     */
    public int getLevel()
    {
	return level;
    }

    /**
     * Obtient le nombre total de niveaux
     */
    public int getLevelCount()
    {
	return boards.size();
    }

    /**
     * Obtient la taille du côté d'une case en pixels
     */
    public static int getBoxSize()
    {
	return BOX_SIZE;
    }

    /**
     * Méthode permettant de savoir si une case est un mur
     */
    public boolean isBoxWall(Position pos)
    {
	if (pos.x < 0 || pos.x >= WIDTH || pos.y < 0 || pos.y >= HEIGHT)
	    return true;
	return board[pos.y][pos.x] >= BOX_WALL;
    }

    /**
     * Méthode permettant de savoir si une case est un téléporteur
     */
    public boolean isBoxTeleporter(Position pos)
    {
	if (pos.x < 0 || pos.x >= WIDTH || pos.y < 0 || pos.y >= HEIGHT)
	    return true;
	return board[pos.y][pos.x] >= BOX_TELEPORTERS &&
	    board[pos.y][pos.x] < BOX_WALL;
    }

    /**
     * Obtient la position d'arrivée d'un téléporteur situé à telle position
     */
    public Position getTeleporter(Position pos)
    {
	if (isBoxTeleporter(pos))
	    return teleporters[board[pos.y][pos.x] - BOX_TELEPORTERS];
	return null;
    }

    /**
     * Obtient la position initiale de Pacman (false) ou des fantômes (true)
     */
    public Position getInitialPosition(boolean character)
    {
	return new Position(character ? ghostPos : pacmanPos);
    }

    /**
     * Obtient la direction initiale de Pacman (false) ou des fantômes (true)
     */
    public int getInitialDir(boolean character)
    {
	if (!character)
	    return pacmanDir;

	// La direction initiale correspond à la direction vers Pacman
	if (Math.abs(pacmanPos.x - ghostPos.x) >=
	    Math.abs(pacmanPos.y - ghostPos.y))
	    return pacmanPos.x < ghostPos.x ? 0 : 1;
	return pacmanPos.y < ghostPos.y ? 2 : 3;
    }

    /**
     * Dessine toute la grille
     */
    public void paint(Graphics gc)
    {
	Position pos = new Position();

	for (int y = 0; y < HEIGHT; y++)
	    for (int x = 0; x < WIDTH; x++) {
		pos.setPosition(x, y);
		drawBox(gc, pos);
	    }
    }

    /**
     * Dessine une bordure de cases "mur" autour de la grille
     */
    public void paintBorder(Graphics gc)
    {
	Position pos = new Position();

	// Haut et bas
	for (int x = -1; x <= WIDTH; x++) {
	    pos.setPosition(x, -1);
	    drawBox(gc, pos);
	    pos.setPosition(x, HEIGHT);
	    drawBox(gc, pos);
	}

	// Gauche et droite
	for (int y = 0; y < HEIGHT; y++) {
	    pos.setPosition(-1, y);
	    drawBox(gc, pos);
	    pos.setPosition(WIDTH, y);
	    drawBox(gc, pos);
	}
    }

    /**
     * Dessine une case
     */
    public void drawBox(Graphics gc, Position pos)
    {
	Image img;

	// Sélection de la bonne image en fonction des coordonnées
	if (pos.x >= 0 && pos.x < WIDTH && pos.y >= 0 && pos.y < HEIGHT) {
	    byte box;

	    switch ((box = board[pos.y][pos.x])) {
	    case BOX_EMPTY:
		img = background[0][1];
		break;

	    case BOX_GUM:
		img = background[1][0];
		break;

	    case BOX_SUPERGUM:
		img = background[1][1];
		break;

	    case BOX_GHOSTS:
		img = background[1][2];
		break;

	    default:
		img = background[0][box >= BOX_WALL ? 0 : 2];
	    }
	} else
	    img = background[0][0];

	// Dessin de l'image
	drawImage(gc, img, pos.x * BOX_SIZE, pos.y * BOX_SIZE);
    }

    /**
     * Dessine la ou les cases se trouvant sous un personnage
     */
    public void clearBoxes(Graphics gc, Character chr)
    {
	final Position pos = chr.getBoxPosition(), offset = chr.getBoxOffset();

	// Case du milieu
	drawBox(gc, pos);

	// Cases alentours
	if (offset.x < 0) {
	    drawBox(gc, new Position(pos.x - 1, pos.y));
	    if (offset.y < 0) {
		drawBox(gc, new Position(pos.x, pos.y - 1));
		drawBox(gc, new Position(pos.x - 1, pos.y - 1));
	    } else if (offset.y > 0) {
		drawBox(gc, new Position(pos.x, pos.y + 1));
		drawBox(gc, new Position(pos.x - 1, pos.y + 1));
	    }
	} else if (offset.x > 0) {
	    drawBox(gc, new Position(pos.x + 1, pos.y));
	    if (offset.y < 0) {
		drawBox(gc, new Position(pos.x, pos.y - 1));
		drawBox(gc, new Position(pos.x + 1, pos.y - 1));
	    } else if (offset.y > 0) {
		drawBox(gc, new Position(pos.x, pos.y + 1));
		drawBox(gc, new Position(pos.x + 1, pos.y + 1));
	    }
	} else {
	    if (offset.y < 0)
		drawBox(gc, new Position(pos.x, pos.y - 1));
	    else if (offset.y > 0)
		drawBox(gc, new Position(pos.x, pos.y + 1));
	}
    }

    /**
     * Dessine une image aux coordonnées relatives à la position de la grille
     * (version avec coordonnées sous forme de deux entiers)
     */
    public void drawImage(Graphics gc, Image img, int x, int y)
    {
	gc.drawImage(img, x + LEFT, y + TOP, component);
    }

    /**
     * Dessine une image aux coordonnées relatives à la position de la grille
     * (version avec coordonnées sous forme d'objet Position)
     */
    public void drawImage(Graphics gc, Image img, Position pos)
    {
	drawImage(gc, img, pos.x, pos.y);
    }

    /**
     * Obtient le rectangle où se situe la grille
     */
    public static Rectangle getRectangle()
    {
	return new Rectangle(LEFT, TOP, WIDTH * BOX_SIZE, HEIGHT * BOX_SIZE);
    }

    /**
     * Méthode appelée par la classe Pacman pour manger une pastille
     */
    public void eatGum(Position pos)
    {
	final byte box = board[pos.y][pos.x];

	if (box == BOX_GUM || box == BOX_SUPERGUM) {
	    gumCount--;
	    game.ateGum(board[pos.y][pos.x] == BOX_SUPERGUM);
	    board[pos.y][pos.x] = BOX_EMPTY;
	}
    }

    /**
     * Obtient le nombre de pastilles restantes
     */
    public int getGumCount()
    {
	return gumCount;
    }
}

// Fin du fichier
