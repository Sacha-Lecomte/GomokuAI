package projet1.gomoku.gamecore;

public class Utils {
    
    /** Ajoute deux entiers et renvoie Integer.MAX_VALUE si le résultat dépasse la valeur maximale d'un entier
     * @return a + b ou Integer.MAX_VALUE si a + b > Integer.MAX_VALUE
     */
    public static int addAndBound(int a, int b){
        if (a > Integer.MAX_VALUE - b) return Integer.MAX_VALUE;
        return a + b;
    }
}
