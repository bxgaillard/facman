/*
 * ---------------------------------------------------------------------------
 *
 * Facman -- Un clône de Pacman en Java
 * Copyright (c) 2004 Benjamin Gaillard & Lionel Imbs
 *
 * ---------------------------------------------------------------------------
 *
 * Fichier     : HomeScreen.java
 *
 * Description : La classe HomeScreen implémente un écran d'accueil.
 *
 * Commentaire : Ceci est le premier écran du jeu à être affiché. Il propose à
 *               l'utilisateur de choisir son niveau de difficulté.
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
import java.awt.Image;
import java.awt.Font;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * La classe HomeScreen, qui affiche un écran d'accueil, avec dessus le
 * meilleur score atteint, et propose le choix de difficulté à l'utilisateur
 */
class HomeScreen implements KeyListener
{
    // Variables utilisées pour les choix
    private static final String[] CHOICES = {
	"Facile", "Moyen", "Difficile" };
    private static final Color COLOR_BACKGROUND = Color.WHITE;
    private static final Color COLOR_TEXT       = Color.BLACK;
    private static final Color COLOR_SCORE      = Color.RED;
    private static final Color COLOR_CHOICE     = Color.BLUE;
    private static final Color COLOR_SELECTED   = Color.YELLOW;
    private int choice = 1;

    // Le meilleur score
    private int score = 0;

    // Taille de la fenêtre
    private final int width, height;

    // Objets auxiliaires
    private final Graphics    gc;
    private final ImageLoader loader;
    private final Component   component;
    private final Image       homeImage;
    private final Game        game;

    /**
     * Constructeur de la classe HomeScreen
     */
    public HomeScreen(final Graphics gc, final ImageLoader loader,
		      final Component component)
    {
	// Initialise l'objet
	this.gc        = gc;
	this.loader    = loader;
	this.component = component;
	width  = component.getWidth();
	height = component.getHeight();

	// Charge l'image
	homeImage = loader.load("home");

	// Crée l'objet Game
	game = new Game(gc, loader, component, this);
    }

    public void init()
    {
	// Affiche un message indiquant que les données sont en train d'être
	// chargées
	gc.setColor(COLOR_BACKGROUND);
	gc.fillRect(0, 0, width, height);
	gc.setColor(COLOR_TEXT);
	gc.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 24));
	drawCenteredText("Chargement des données...", width / 2, height / 2);

	// Chargement des données et des images
	loader.waitLoading();
	game.init();
    }

    /**
     * Méthode publique servant à lancer l'écran d'accueil
     */
    public void run()
    {
	// Met à jour le score
	final int newScore = game.getScore();
	if (newScore > score)
	    score = newScore;

	// Affiche l'écran
	paintScreen();
	paintChoices();
	update();

	// Ajoute la gestion des événements clavier
	component.addKeyListener(this);
    }

    /**
     * Dessine le fond et affiche le score
     */
    private void paintScreen()
    {
	// Dessine l'image de fond
	gc.drawImage(homeImage, 0, 0, component);

	// Initialise la police
	gc.setFont(new Font("SansSerif", Font.BOLD, 20));

	// Affiche le score
	gc.setColor(COLOR_TEXT);
	drawCenteredText("Meilleur score :", width / 2, 150);
	gc.setColor(COLOR_SCORE);
	drawCenteredText(score, width / 2, 175);
    }

    /**
     * Affiche les choix en mettant en surbrillance le choix actuel
     */
    private void paintChoices()
    {
	// Initialise la police
	gc.setFont(new Font("SansSerif", Font.BOLD, 28));

	for (int i = 0; i < CHOICES.length; i++) {
	    gc.setColor(i == choice ? COLOR_SELECTED : COLOR_CHOICE);
	    drawCenteredText(CHOICES[i], (2 * i + 1) *  width /
			     (CHOICES.length * 2), height - 30);
	}
    }

    /**
     * Demande le redessin de la fenêtre
     */
    private void update()
    {
	component.repaint();
    }

    /**
     * Affiche du texte centré par rapport à un point
     */
    public void drawCenteredText(final String text, final int x, final int y)
    {
	final Graphics2D gc = (Graphics2D) this.gc;

	// Affiche le texte
	gc.drawString(text, x - (int)
		      Math.ceil(gc.getFont().
				getStringBounds(text,
						gc.getFontRenderContext()).
				getWidth()) / 2, y);
    }

    /**
     * Affiche un nombre centré par rapport à un point
     */
    public void drawCenteredText(final int number, final int x, final int y)
    {
	drawCenteredText("" + number, x, y);
    }

    /**
     * Méthode appelée quand une touche est enfoncée
     */
    public void keyPressed(final KeyEvent event)
    {
	switch (event.getKeyCode()) {
	case KeyEvent.VK_LEFT:
	case KeyEvent.VK_KP_LEFT:
	case KeyEvent.VK_NUMPAD4:
	    // Choix précédent
	    choice = (choice + CHOICES.length - 1) % CHOICES.length;
	    paintChoices();
	    update();
	    break;

	case KeyEvent.VK_RIGHT:
	case KeyEvent.VK_KP_RIGHT:
	case KeyEvent.VK_NUMPAD6:
	    // Choix suivant
	    choice = (choice + 1) % CHOICES.length;
	    paintChoices();
	    update();
	    break;

	case KeyEvent.VK_SPACE:
	case KeyEvent.VK_ENTER:
	    // Retire la gestion des événements clavier
	    component.removeKeyListener(this);

	    // Lancement du jeu
	    game.setDifficulty(choice);
	    game.run();
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
