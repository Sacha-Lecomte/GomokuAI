package gamecore;

import java.util.Objects;

/**Coordonnées sur le plateau de jeu*/
public class Coords implements Cloneable {
    /**Numéro de colonne depuis la gauche à partir de 0*/
    public int column;

    /**Numéro de ligne depuis le haut à partir de 0*/
    public int row;

    /**Déclarer des coordonnées indéfinies : -1 -1*/
    public Coords(){
        column = -1;
        row = -1;
    }

    /**Déclarer des coordonnées
     * @param column Numéro de colonne
     * @param row Numéro de ligne
    */
    public Coords(int column, int row){
        this.column = column;
        this.row = row;
    }

    /**Vérifier si les coordonnées sont définies
     * @return true si les coordonnées sont définies, false sinon
    */
    public boolean isDefined(){
        return !(column == -1 || row == -1);
    }
    
    @Override
    public Coords clone(){
        return new Coords(column, row);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coords cell = (Coords) o;
        return row == cell.row && column == cell.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }
}
