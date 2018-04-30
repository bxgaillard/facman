/*
 * ---------------------------------------------------------------------------
 *
 * Facman -- Un cl�ne de Pacman en Java
 * Copyright (c) 2004 Benjamin Gaillard & Lionel Imbs
 *
 * ---------------------------------------------------------------------------
 *
 * Fichier     : Position.java
 *
 * Description : La classe Position permet de g�rer de mani�re simple les
 *               coordonn�es en deux dimensions ainsi que leur d�placement.
 *
 * Commentaire : Cette classe est utilis�e par de nombreuses autres classes du
 *               projet, notamment Pacman, Ghost et Board.
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


/**
 * La classe Position, qui permet de stocker des coordonn�es en deux
 * dimensions
 */
class Position
{
    // Coordonn�es
    public int x, y;

    /**
     * Constructeur par d�faut
     */
    Position() {}

    /**
     * Constructeur � partir de coordonn�es
     */
    Position(final int x, final int y)
    {
	setPosition(x, y);
    }

    /**
     * Constructeur � partir d'un objet Position existant
     */
    Position(final Position pos)
    {
	setPosition(pos);
    }

    /**
     * Affecte les coordonn�es de celles pass�es en param�tres
     */
    public void setPosition(final int x, final int y)
    {
	this.x = x;
	this.y = y;
    }

    /**
     * Affecte les coordonn�es de celles d'un objet Position existant
     */
    public void setPosition(final Position pos)
    {
	x = pos.x;
	y = pos.y;
    }

    /**
     * Ajoute les coordonn�es pass�es en param�tres � celles courantes
     */
    public void move(final int x, final int y)
    {
	this.x += x;
	this.y += y;
    }

    /**
     * Ajoute les coordonn�es d'un objet Position existant � celles courantes
     */
    public void move(final Position pos)
    {
	x += pos.x;
	y += pos.y;
    }

    /**
     * Teste l'�galit� entre deux objets Position
     */
    public boolean equals(final Position pos)
    {
	return x == pos.x && y == pos.y;
    }

    /**
     * Cr�e un tableau de 4 objets Position, chacun repr�sentant une des
     * quatre directions, dont la distance par rapport � la coordonn�e (0, 0)
     * est sp�cifi�e en param�tre
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
