/*
 * ---------------------------------------------------------------------------
 *
 * Facman -- Un clône de Pacman en Java
 * Copyright (c) 2004 Benjamin Gaillard & Lionel Imbs
 *
 * ---------------------------------------------------------------------------
 *
 * Fichier     : Game.java
 *
 * Description : La classe Game effectue tout les calculs nécessaires pour le
 *               déroulement du jeu.
 *
 * Commentaire : C'est la classe principale du jeu. Elle gère la grille de
 *               jeu, les personnages, le timer, et les événements clavier.
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
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Image;
import java.awt.Font;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.Timer;

/**
 * La classe Game, qui gère tout ce qui est en rapport avec l'exécution du jeu
 * lui-même et l'intéraction avec les autres classes.
 */
class Game implements ActionListener, KeyListener
{
    // Variables relatives au timer
    private static final int     IMG_PER_SEC = 25;
    private static final int     DELAY = 1000 / IMG_PER_SEC;
    private        final Timer   timer;
    private              boolean sleeping = false;

    // Personnages : Pacman et les fantômes
    private static final int     MAX_NB_GHOSTS = 4;
    private        final Pacman  pacman;
    private        final Ghost[] ghosts = new Ghost[MAX_NB_GHOSTS];
    private              int     nbGhosts;
    private        final Image[] pacmanImg;

    // Pile pour l'apparition des fantômes
    private static final int   BIRTH_DELAY = 150;
    private        final int[] ghostStack = new int[MAX_NB_GHOSTS];
    private              int   stackSize = 0, stackStart = 0;
    private              int   birth = 0;

    // Variables utilisées pour la pause
    private static final Color    PAUSE_COLOR = new Color(0f, 0f, 0f, .5f);
    private        final int      width, height;
    private              boolean  paused = false, playing, quitting;

    // Variables utilisées pour l'affichage de texte en images
    private static final int      TEXT_IMG_WIDTH = 291, TEXT_IMG_HEIGHT = 70;
    private static final int      PAUSE_IMG = 0, WIN_IMG = 1, LOST_IMG = 2;
    private        final Image[]  textImages = new Image[3];
    private        final Position textImgPos;

    // Variables utilisées pour l'affichage des textes
    private static final int         RECT_LEVEL = 0, RECT_SCORE = 1;
    private static final int         RECT_LIVES = 2, RECT_BASELINE = 17;
    private static final Rectangle[] RECTANGLES = {
    	new Rectangle(273, 9, 62, 22),
	new Rectangle(424, 9, 62, 22),
	new Rectangle(569, 9, 62, 22)
    };

    // Variables utilisées pour le calcul du score et des vies
    private static final int SCORE_GUM = 1, SCORE_SUPERGUM = 5;
    private static final int SCORE_EAT = 10, SCORE_EATEN = 10;
    private static final int SCORE_LEVEL = 50;
    private              int score = 0, lives;

    // Image de la fenêtre
    private final Image window;

    // Grille de jeu
    private final Board board;

    // Contexte graphique dans lequel dessiner
    private final Graphics gc;

    // Composant chargé de l'affichage
    private final Component component;

    // Écran d'accueil
    private final HomeScreen home;

    /**
     * Constructeur de la classe Game
     */
    public Game(final Graphics gc, final ImageLoader loader,
		final Component component, final HomeScreen home)
    {
	// Initialise l'objet
	this.gc        = gc;
	this.component = component;
	this.home      = home;
	width  = component.getWidth();
	height = component.getHeight();

	// Crée les objets auxiliaires
	board = new Board(loader, component, this);
	timer = new Timer(DELAY, this);

	// Crée les personnages
	pacman = new Pacman(board, loader, component);
	for (int ghost = 0; ghost < MAX_NB_GHOSTS; ghost++)
	    ghosts[ghost] = new Ghost(board, loader, ghost, pacman);

	// Charge les images
	Image[][] images = loader.load("texts", 1, textImages.length,
				       TEXT_IMG_WIDTH, TEXT_IMG_HEIGHT);
	for (int image = 0; image < textImages.length; image++)
	    textImages[image] = images[image][0];
	textImgPos = new Position((width - TEXT_IMG_WIDTH) / 2,
				  (height - TEXT_IMG_HEIGHT) / 2);
	pacmanImg = loader.load("pacman2", 2, 1)[0];
	window = loader.load("window");
    }

    /**
     * Initialise l'objet et charge les données
     */
    public void init()
    {
	board.initImages();
	board.loadLevels();
    }

    /**
     * Méthode publique servant à lancer le jeu
     */
    public void run()
    {
	// Initialisation de la police
	gc.setFont(new Font("SansSerif", Font.BOLD, 14));

	// Lancement du jeu
	beginGame();
    }

    /**
     * Obtient le dernier score obtenu
     */
    public int getScore()
    {
	return score;
    }

    /**
     * Définit le niveau de difficulté
     */
    public void setDifficulty(final int difficulty)
    {
	for (int ghost = 0; ghost < MAX_NB_GHOSTS; ghost++)
	    ghosts[ghost].setDifficulty(difficulty);
    }

    /**
     * Initialise l'état et la position de chaque personnage
     */
    private void initCharacters()
    {
	// Initialise la position des personnages
	pacman.initPosition();
	ghosts[0].undie();
	for (int ghost = 1; ghost < MAX_NB_GHOSTS; ghost++) {
	    ghosts[ghost].die();
	    ghostStack[ghost - 1] = ghost;
	}

	// Initialise la pile d'apparition des fantômes
	stackSize = MAX_NB_GHOSTS - 1;
	stackStart = 0;
	birth = BIRTH_DELAY;

	// Met le jeu en attente d'action de l'utilisateir
	playing  = false;
	quitting = false;
    }

    /**
     * Passe au niveau suivant
     */
    private boolean nextLevel()
    {
	// Charge le niveau suivant
	if (!board.nextLevel())
	    return false;

	// Initialise la position des personnages
	initCharacters();

	// Mise à jour du score et des vies
	score += SCORE_LEVEL;
	lives++;

	// Redessine la fenêtre
	paint();
	displayLevel();
	displayScore();
	displayLives();
	update();
	return true;
    }

    /**
     * Redessine tout l'écran de jeu
     */
    private void paint()
    {
	// Dessine le terrain
	board.paint(gc);

	// Dessine les personnages
	pacman.draw(gc);
	for (int ghost = 0; ghost < MAX_NB_GHOSTS; ghost++)
	    ghosts[ghost].draw(gc);
    }

    /**
     * Demande à la fenêtre ou à l'applet de mettre à jour son contenu
     */
    private void update()
    {
	component.repaint();
    }

    /**
     * Affiche un texte après avoir effacé le fond, dans le rectangle spécifié
     */
    private void drawText(final int rectangle, final String text)
    {
	final Graphics2D gc = (Graphics2D) this.gc;

	// Efface le fond
	gc.setColor(Color.BLACK);
	gc.fillRect(RECTANGLES[rectangle].x, RECTANGLES[rectangle].y,
		    RECTANGLES[rectangle].width,
		    RECTANGLES[rectangle].height);

	// Affiche le texte
	gc.setColor(Color.GREEN);
	gc.drawString(text, RECTANGLES[rectangle].x +
		      RECTANGLES[rectangle].width - 4 - (int)
		      Math.ceil(gc.getFont().
				getStringBounds(text,
						gc.getFontRenderContext()).
				getWidth()),
		      RECTANGLES[rectangle].y + RECT_BASELINE);
    }

    /**
     * Affiche un nombre dans un rectangle après l'avoir effacé
     */
    private void drawText(final int rectangle, final int number)
    {
	drawText(rectangle, "" + number);
    }

    /**
     * Affiche le numéro de niveau
     */
    private void displayLevel()
    {
	drawText(RECT_LEVEL, board.getLevel() + "/" + board.getLevelCount());
    }

    /**
     * Affiche le score
     */
    private void displayScore()
    {
	drawText(RECT_SCORE, score);
    }

    /**
     * Affiche le nombre de vies restant
     */
    private void displayLives()
    {
	drawText(RECT_LIVES, lives);
    }

    /**
     * Dessine une des images contenant du texte
     */
    private void drawTextImage(final int image)
    {
	gc.drawImage(textImages[image], textImgPos.x, textImgPos.y,
		     component);
    }

    /**
     * Commence le jeu
     */
    private void beginGame()
    {
	// Dessine la fenêtre
	board.paintBorder(gc);
	gc.drawImage(window, 0, 0, component);

	// Charge le premier niveau
	board.resetLevel();
	score = -SCORE_LEVEL;
	lives = 1;
	nextLevel();

	// Redessine la fenêtre
	paint();
	update();

	// Ajoute la gestion des événements clavier
	component.addKeyListener(this);

	// Démarre le timer
	timer.start();
    }

    /**
     * Termine le jeu
     */
    private void endGame()
    {
	// Arrêt du timer
	timer.stop();

	// Retire la gestion des événements clavier
	component.removeKeyListener(this);

	// Rend la main à l'écran d'accueil
	home.run();
    }

    /**
     * Met le jeu en pause
     */
    private void beginPause()
    {
	// Arrête le timer
	paused = true;
	timer.stop();

	// Affiche le texte de pause
	final Rectangle rect = board.getRectangle();
	gc.setColor(PAUSE_COLOR);
	gc.fillRect(rect.x, rect.y, rect.width, rect.height);
	drawTextImage(PAUSE_IMG);
	update();
    }

    /**
     * Sort le jeu de la pause
     */
    private void endPause()
    {
	// Réactive le timer
	paused = false;
	paint();
	update();
	timer.start();
    }

    /**
     * Endort le jeu pendant un certain temps en modifiant le timer
     */
    private void sleep(int time)
    {
	timer.setInitialDelay(time);
	timer.setRepeats(false);
	timer.restart();
	sleeping = true;
    }

    /**
     * Endort le jeu pendant deux secondes
     */
    private void sleep()
    {
	sleep(2000);
    }

    /**
     * Méthode appelée lors de la victoire ou de la mort de Pacman
     */
    private boolean endLevel()
    {
	// Victoire
	if (board.getGumCount() == 0)
	    return nextLevel();

	if (lives >= 0) {
	    // Effacement des cases occupées par les personnages
	    board.clearBoxes(gc, pacman);
	    for (int ghost = 0; ghost < MAX_NB_GHOSTS; ghost++)
		board.clearBoxes(gc, ghosts[ghost]);

	    // Réinitialisation des personnages
	    initCharacters();
	    update();
	    return true;
	}

	// Perdu, fin du jeu
	endGame();
	return false;
    }

    /**
     * Méthode appelée périodiquement par le timer
     */
    public void actionPerformed(final ActionEvent event)
    {
	// Si le jeu est en attente
	if (sleeping) {
	    sleeping = false;
	    if (endLevel()) {
		timer.setInitialDelay(DELAY);
		timer.setRepeats(true);
		timer.restart();
	    }
	    return;
	}

	// Si le joueur n'a pas commencé la partie
	if (!playing) {
	    board.clearBoxes(gc, pacman);
	    board.clearBoxes(gc, ghosts[0]);
	    pacman.draw(gc);
	    ghosts[0].draw(gc);
	    update();
	    return;
	}

	// Effacement des cases occupées par les personnages
	board.clearBoxes(gc, pacman);
	for (int ghost = 0; ghost < MAX_NB_GHOSTS; ghost++)
	    board.clearBoxes(gc, ghosts[ghost]);

	// Effectue le mouvement de chaque personnage
	for (int ghost = 0; ghost < MAX_NB_GHOSTS; ghost++) {
	    ghosts[ghost].doMove();
	    ghosts[ghost].draw(gc);
	}
	pacman.doMove();
	pacman.draw(gc);

	// Fait "renaître" les fantômes "morts"
	if (stackSize > 0) {
	    if (birth > 0)
		birth--;
	    else {
		ghosts[ghostStack[stackStart]].undie();
		stackStart = (stackStart + 1) % MAX_NB_GHOSTS;
		stackSize--;
		birth = BIRTH_DELAY;
	    }
	}

	// Teste s'il y a une fin de partie
	if (board.getGumCount() == 0) {
	    // Gagné !
	    board.drawImage(gc, pacmanImg[0], pacman.getPosition());
	    drawTextImage(WIN_IMG);
	    sleep();
	} else
	    // Teste les collisions
	    for (int ghost = 0; ghost < MAX_NB_GHOSTS; ghost++)
		if (!ghosts[ghost].isDead() &&
		    pacman.collides(ghosts[ghost])) {
		    if (ghosts[ghost].isAfraid()) {
			// Pacman mange le fantôme
			score += SCORE_EAT;
			displayScore();
			ghosts[ghost].die();
			ghostStack[(stackStart + stackSize++) % MAX_NB_GHOSTS]
			    = ghost;
		    } else {
			// Le fantôme mange Pacman
			board.drawImage(gc, pacmanImg[1],
					pacman.getPosition());

			// Mise à jour du score
			if ((score -= SCORE_EATEN) < 0)
			    score = 0;
			displayScore();

			// Mise à jour des vies
			if (lives > 0) {
			    lives--;
			    displayLives();
			} else {
			    displayLives();
			    lives--;
			    drawTextImage(LOST_IMG);
			}

			// Mise en pause
			sleep();
		    }
		}

	// Affiche les changements
	update();
    }

    /**
     * Méthode appelée lorsque Pacman mange une pastille
     */
    public void ateGum(final boolean superGum)
    {
	if (!superGum)
	    // Pastille normale
	    score += SCORE_GUM;
	else {
	    // Super pastille
	    score += SCORE_SUPERGUM;
	    for (int ghost = 0; ghost < MAX_NB_GHOSTS; ghost++)
		ghosts[ghost].fear();
	}

	// Affiche le score mis à jour
	displayScore();
    }

    /**
     * Méthode appelée quand une touche est enfoncée
     */
    public void keyPressed(final KeyEvent event)
    {
	if (!sleeping) {
	    if (!quitting)
		switch (event.getKeyCode()) {
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_KP_LEFT:
		case KeyEvent.VK_NUMPAD4:
		case KeyEvent.VK_RIGHT:
		case KeyEvent.VK_KP_RIGHT:
		case KeyEvent.VK_NUMPAD6:
		case KeyEvent.VK_UP:
		case KeyEvent.VK_KP_UP:
		case KeyEvent.VK_NUMPAD8:
		case KeyEvent.VK_DOWN:
		case KeyEvent.VK_KP_DOWN:
		case KeyEvent.VK_NUMPAD2:
		    // Démarre le jeu si une touche de direction est appuyée
		    if (!playing)
			playing = true;
		    break;

		case KeyEvent.VK_SPACE:
		    // Si espace est appuyée, on met en pause ou on en sort
		    if (!paused)
			beginPause();
		    else
			endPause();
		    break;

		case KeyEvent.VK_ESCAPE:
		    // Échappement : on veut arrêter le jeu

		    // Arrêt du timer
		    timer.stop();
		    quitting = true;

		    // Dessin de la question
		    final Rectangle rect = board.getRectangle();
		    gc.setColor(PAUSE_COLOR);
		    gc.fillRect(rect.x, rect.y, rect.width, rect.height);
		    gc.setFont(new Font("SansSerif", Font.BOLD, 20));
		    gc.setColor(Color.WHITE);
		    home.drawCenteredText("Voulez-vraiment abandonner ? " +
					  "(O/N)", width / 2, height / 2);
		    update();
		}
	    else
		switch (event.getKeyCode()) {
		case KeyEvent.VK_O:
		    // Quitte le jeu
		    endGame();
		    break;

		case KeyEvent.VK_N:
		case KeyEvent.VK_ESCAPE:
		    // Continue le jeu
		    paint();
		    update();
		    quitting = false;
		    timer.start();
		}
	}
    }

    /**
     * Méthode appelée quand une touche est relâchée
     */
    public void keyReleased(final KeyEvent event) { /* Non utilisée */ }

    /**
     * Méthode appelée quand une touche génère un événement "entrée d'un
     * caractère", ce qui peut arriver plusieurs fois lors d'un seul
     * enfoncement de touche à intervalle régulier (répétition)
     */
    public void keyTyped(final KeyEvent event) { /* Non utilisée */ }
}

// Fin du fichier
