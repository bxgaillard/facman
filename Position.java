/*
 * ---------------------------------------------------------------------------
 *
 * Facman -- Un clône de Pacman en Java
 * Copyright (c) 2004 Benjamin Gaillard & Lionel Imbs
 *
 * ---------------------------------------------------------------------------
 *
 * Fichier     : Position.java
 *
 * Description : La classe Position permet de gérer de manière simple les
 *               coordonnées en deux dimensions ainsi que leur déplacement.
 *
 * Commentaire : Cette classe est utilisée par de nombreuses autres classes du
 *               projet, notamment Pacman, Ghost et Board.
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


/**
 * La classe Position, qui permet de stocker des coordonnées en deux
 * dimensions
 */
class Position
{
    // Coordonnées
    public int x, y;

    /**
     * Constructeur par défaut
     */
    Position() {}

    /**
     * Constructeur à partir de coordonnées
     */
    Position(final int x, final int y)
    {
	setPosition(x, y);
    }

    /**
     * Constructeur à partir d'un objet Position existant
     */
    Position(final Position pos)
    {
	setPosition(pos);
    }

    /**
     * Affecte les coordonnées de celles passées en paramètres
     */
    public void setPosition(final int x, final int y)
    {
	this.x = x;
	this.y = y;
    }

    /**
     * Affecte les coordonnées de celles d'un objet Position existant
     */
    public void setPosition(final Position pos)
    {
	x = pos.x;
	y = pos.y;
    }

    /**
     * Ajoute les coordonnées passées en paramètres à celles courantes
     */
    public void move(final int x, final int y)
    {
	this.x += x;
	this.y += y;
    }

    /**
     * Ajoute les coordonnées d'un objet Position existant à celles courantes
     */
    public void move(final Position pos)
    {
	x += pos.x;
	y += pos.y;
    }

    /**
     * Teste l'égalité entre deux objets Position
     */
    public boolean equals(final Position pos)
    {
	return x == pos.x && y == pos.y;
    }

    /**
     * Crée un tableau de 4 objets Position, chacun représentant une des
     * quatre directions, dont la distance par rapport à la coordonnée (0, 0)
     * est spécifiée en paramètre
     */
    public static Position[] makeDirs(final int size)
    {
	final Position[] dirs = {
	    new Position(-size, 0), // Gauche
	    new Position(size, 0),  // Droite
	    new Position(0, -size), // Haut
	    new Position(0, size)   // Bas
	};

	return dirs;
    }
}

// Fin du fichier
