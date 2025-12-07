package projet1.gomoku.gamecore;

import projet1.gomoku.gamecore.enums.TileState;
import projet1.gomoku.gamecore.enums.WinnerState;

/**Plateau de Gomoku*/
public class GomokuBoard extends Array2D<TileState>{

    /**Taille du plateau*/
    public static final int size = 15;

    /**Créer un plateau de Gomoku vide*/
    public GomokuBoard(){
        super(size, size, TileState.Empty);
    }

    /**Remplir le plateau de manière aléatoire
     * @param whiteRatio Ratio de cases blanches
     * @param blackRatio Ratio de cases noires
     * @throws IllegalArgumentException Si la somme des ratios blancs et noirs dépasse 1
    */
    public void randomize(double whiteRatio, double blackRatio){
        if (whiteRatio + blackRatio > 1) throw new IllegalArgumentException("La somme des ratios blancs et noirs ne peut pas dépasser 1.");

        for (int y = 0; y < size; y++){
            for (int x = 0; x < size; x++){
                double random = Math.random();
                if (random < whiteRatio) set(x, y, TileState.White);
                else if (random < whiteRatio + blackRatio) set(x, y, TileState.Black);
                else set(x, y, TileState.Empty);
            }
        }
    }

    /**Obtenir l'état de fin de partie du plateau
     * @return Etat de fin de partie
    */
    public WinnerState getWinnerState(){
        
        // Liste des directions à tester pour trouver un alignement de 5 pièces
        final int[][] DIRECTIONS_TO_CHECK = { 
            {-1, -1}, // Diagonales bas-gauche haut-droite
            {-1, 0}, // Verticales
            {-1, 1}, // Diagonales haut-gauche bas-droite
            {0, 1} // Horizontales
        };

        TileState currentCellState; // Alloue de la mémoire pour stocker l'état de la cellule testée
        boolean isBoardFull = true; // Indique si le plateau est plein

        for (int row = 0; row < size; row++){
            for (int column = 0; column < size; column++){

                currentCellState = get(column, row);

                if (currentCellState != TileState.Empty){ // Si la case n'est pas vide, chercher un alignement de 5 pièces dans toutes les directions
    
                    for (int[] direction : DIRECTIONS_TO_CHECK){
    
                        boolean hasCurrentPlayerWon = true;
                        
                        for (int step = 1; step <= 4; step++){ // Pour chaque pas allant de 1 à 4
    
                            // Calculer les coordonnées de la cellule à tester
                            int currentRow = row + direction[0] * step;
                            int currentColumn = column + direction[1] * step;
    
                            if (!areCoordsValid(currentColumn, currentRow) || // Si le point est en dehors du plateau
                                get(currentColumn, currentRow) != currentCellState){  // Ou si le point n'est pas occupé par le joueur testé
                                hasCurrentPlayerWon = false;
                                break; // Arrêter de tester cette direction
                            }
                        }
    
                        if (hasCurrentPlayerWon){ // Si un joueur a gagné, retourner sa couleur
                            return (currentCellState == TileState.Black) ? WinnerState.Black : WinnerState.White;
                        }
                    }
    
                }
                else{ // Si la cellule est vide, le plateau n'est pas plein
                    isBoardFull = false;
                }
            }
        }

        if (isBoardFull) return WinnerState.Tie; // Si aucun joueur n'a gagné et que le plateau est plein, égalité

        return WinnerState.None; // Si aucun joueur n'a gagné mais que le plateau n'est pas plein, la partie n'est pas finie
    }
         
    @Override
    public void print(){
        System.out.print("   ");
        for (int i = 0; i < size; i++){
            System.out.print(String.format("%02d ", i)); // Afficher le numéro des colonnes
        }
        System.out.println();

        Coords currentCellCoords = new Coords();

        for (currentCellCoords.row = 0; currentCellCoords.row < size; currentCellCoords.row ++){
            System.out.print(String.format("%02d ", currentCellCoords.row)); // Afficher le numéro de la ligne

            for (currentCellCoords.column = 0; currentCellCoords.column < size; currentCellCoords.column ++){
                switch (get(currentCellCoords)){ // Afficher le caractère correspondant à l'état de chaque cellule
                case White:
                    System.out.print("W  ");  // ■
                    break;
                case Black:
                    System.out.print("B  ");  // □
                    break;
                default:
                    System.out.print("-  ");  // •
                }
            }

            System.out.println();
        }
    }

    @Override
    public GomokuBoard clone(){
        GomokuBoard clone = new GomokuBoard();

        for (int x = 0; x < getWidth(); x++){
            for (int y = 0; y < getHeight(); y++){
                clone.set(x, y, get(x, y));
            }
        }

        return clone;
    }
}

