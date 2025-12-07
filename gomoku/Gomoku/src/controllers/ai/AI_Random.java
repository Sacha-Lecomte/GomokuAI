package controllers.ai;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import controllers.PlayerController;
import gamecore.Coords;
import gamecore.GomokuBoard;
import gamecore.enums.Player;
import gamecore.enums.TileState;

/**Représente un IA qui cherche les coups en se positionnant sur chaque case, puis en vérifiant le contenu des 4 cases autour dans les 8 directions */
public class AI_Random extends PlayerController {


    public AI_Random(int minimaxDepth){
        
    }

    public AI_Random(){
        super();
    }

    public int evaluateBoard(GomokuBoard board, Player player){
        /* Il y a sans doute des choses à modifier ici*/
        return 0;
    }

    
    public Coords[] getAvailableMoves(GomokuBoard board, Player player){
        Coords currentCellCoords = new Coords();

        TileState playerCellState = player == Player.White ? TileState.White : TileState.Black;
        
        Map<Coords, Integer> moves = new HashMap<>();

        for (currentCellCoords.row = 0; currentCellCoords.row < GomokuBoard.size; currentCellCoords.row++){
            for (currentCellCoords.column = 0; currentCellCoords.column < GomokuBoard.size; currentCellCoords.column++){
                if (board.get(currentCellCoords) == TileState.Empty){ // Si la case est vide
                    
                    board.set(currentCellCoords, playerCellState); // Jouer le coup
                    int score = evaluateBoard(board, player); // Evaluer le coup
                    board.set(currentCellCoords, TileState.Empty); // Annuler le coup

                    moves.put(currentCellCoords.clone(), score); // Enregistrer le coup
                }
            }
        }

        Stream<Map.Entry<Coords, Integer>> sorted = moves.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())); // Trier les coups par ordre de priorité décroissante

        return sorted.map(Map.Entry::getKey).toArray(Coords[]::new); // Retourner les coordonnées des coups
    }

	@Override
	public Coords play(GomokuBoard board, Player player) {
		// on retournelepremier coup
		return getAvailableMoves(board, player)[0];
	}
}
