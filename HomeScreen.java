/*
 * ---------------------------------------------------------------------------
 *
 * Facman -- Un cl�ne de Pacman en Java
 * Copyright (c) 2004 Benjamin Gaillard & Lionel Imbs
 *
 * ---------------------------------------------------------------------------
 *
 * Fichier     : HomeScreen.java
 *
 * Description : La classe HomeScreen impl�mente un �cran d'accueil.
 *
 * Commentaire : Ceci est le premier �cran du jeu � �tre affich�. Il propose �
 *               l'utilisateur de choisir son niveau de difficult�.
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
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Font;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * La classe HomeScreen, qui affiche un �cran d'accueil, avec dessus le
 * meilleur score atteint, et propose le choix de difficult� � l'utilisateur
 */
class HomeScreen implements KeyListener
{
    // Variables utilis�es pour les choix
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

    // Taille de la fen�tre
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

	// Cr�e l'objet Game
	game = new Game(gc, loader, component, this);
    }

    public void init()
    {
	// Affiche un message indiquant que les donn�es sont en train d'�tre
	// charg�es
	gc.setColor(COLOR_BACKGROUND);
	gc.fillRect(0, 0, width, height);
	gc.setColor(COLOR_TEXT);
	gc.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 24));
	drawCenteredText("Chargement des donn�es...", width / 2, height / 2);

	// Chargement des donn�es et des images
	loader.waitLoading();
	game.init();
    }

    /**
     * M�thode publique servant � lancer l'�cran d'accueil
     */
    public void run()
    {
	// Met � jour le score
	final int newScore = game.getScore();
	if (newScore > score)
	    score = newScore;

	// Affiche l'�cran
	paintScreen();
	paintChoices();
	update();

	// Ajoute la gestion des �v�nements clavier
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
     * Demande le redessin de la fen�tre
     */
    private void update()
    {
	component.repaint();
    }

    /**
     * Affiche du texte centr� par rapport � un point
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
     * Affiche un nombre centr� par rapport � un point
     */
    public void drawCenteredText(final int number, final int x, final int y)
    {
	drawCenteredText("" + number, x, y);
    }

    /**
     * M�thode appel�e quand une touche est enfonc�e
     */
    public void keyPressed(final KeyEvent event)
    {
	switch (event.getKeyCode()) {
	case KeyEvent.VK_LEFT:
	case KeyEvent.VK_KP_LEFT:
	case KeyEvent.VK_NUMPAD4:
	    // Choix pr�c�dent
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
	    // Retire la gestion des �v�nements clavier
	    component.removeKeyListener(this);

	    // Lancement du jeu
	    game.setDifficulty(choice);
	    game.run();
	}
    }

    /**
     * M�thode appel�e quand une touche est rel�ch�e
     */
    public void keyReleased(final KeyEvent event) { /* Non utilis�e */ }

    /**
     * M�thode appel�e quand une touche g�n�re un �v�nement "entr�e d'un
     * caract�re", ce qui peut arriver plusieurs fois lors d'un seul
     * enfoncement de touche � intervalle r�gulier (r�p�tition)
     */
    public void keyTyped(final KeyEvent event) { /* Non utilis�e */ }
}

// Fin du fichier
