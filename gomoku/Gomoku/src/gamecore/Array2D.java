package gamecore;

import java.util.ArrayList;
import java.util.List;

/**Classe générique pour stocker un tableau 2D
 * @param <T> Type des éléments du tableau
*/
public class Array2D<T> implements Cloneable {
    /**Tableau 2D*/
    private List<List<T>> array;

    /**Créer un tableau 2D
     * @param width Largeur du tableau
     * @param height Hauteur du tableau
     * @param defaultValue Valeur par défaut
    */
    public Array2D(int width, int height, T defaultValue){
        array = new ArrayList<>(width);

        for (int x = 0; x < width; x++){
            List<T> column = new ArrayList<>(height);
            for (int y = 0; y < height; y++){
                column.add(defaultValue);
            }
            array.add(column);
        }
    }

    /**Créer un tableau 2D
     * @param width Largeur du tableau
     * @param height Hauteur du tableau
    */
    public Array2D(int width, int height){
        this(width, height, null);
    }

    /**Obtenir la valeur d'un élément
     * @param x Numéro de colonne
     * @param y Numéro de ligne
     * @return Valeur de l'élément
    */
    public T get(int x, int y){
        return array.get(x).get(y);
    }

    /**Définir la valeur d'un élément
     * @param x Numéro de colonne
     * @param y Numéro de ligne
     * @param value Valeur à définir
    */
    public void set(int x, int y, T value){
        array.get(x).set(y, value);
    }

    /**Obtenir la valeur d'un élément
     * @param coords Coordonnées de l'élément
     * @return Valeur de l'élément
    */
    public T get(Coords coords){
        return get(coords.column, coords.row);
    }

    /**Définir la valeur d'un élément
     * @param coords Coordonnées de l'élément
     * @param value Valeur à définir
    */
    public void set(Coords coords, T value){
        set(coords.column, coords.row, value);
    }

    /**Obtenir la largeur du tableau
     * @return Largeur du tableau
    */
    public int getWidth(){
        return array.size();
    }

    /**Obtenir la hauteur du tableau
     * @return Hauteur du tableau
    */
    public int getHeight(){
        return array.get(0).size();
    }


    /**Vérifier si les coordonnées sont valides
     * @param x Numéro de colonne
     * @param y Numéro de ligne
     * @return true si les coordonnées sont valides, false sinon
    */
    public boolean areCoordsValid(int x, int y){
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
    }

    /**Vérifier si les coordonnées sont valides
     * @param coords Coordonnées à tester
     * @return true si les coordonnées sont valides, false sinon
    */
    public boolean areCoordsValid(Coords coords){
        return coords.column >= 0 && coords.column < getWidth() && coords.row >= 0 && coords.row < getHeight();
    }

    /**Afficher le tableau dans la console*/
    public void print(){
        for (int y = 0; y < getHeight(); y++){
            for (int x = 0; x < getWidth(); x++){
                System.out.print(get(x, y) + " ");
            }
            System.out.println();
        }
    }


    @Override
    public Array2D<T> clone(){
        Array2D<T> clone = new Array2D<>(getWidth(), getHeight());

        for (int x = 0; x < getWidth(); x++){
            for (int y = 0; y < getHeight(); y++){
                clone.set(x, y, get(x, y));
            }
        }

        return clone;
    }
}
