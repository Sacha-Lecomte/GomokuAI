package projet1.gomoku;

/*
 * Code created by Thomas Cohen (Esisar) 
 * open source code
 * */
 
import projet1.gomoku.controllers.HumanPlayer;
import projet1.gomoku.controllers.PlayerController;
import projet1.gomoku.controllers.ai.AI_Random;
import projet1.gomoku.gamecore.Coords;
import projet1.gomoku.gamecore.GomokuBoard;
import projet1.gomoku.gamecore.enums.Player;
import projet1.gomoku.gamecore.enums.TileState;
import projet1.gomoku.gamecore.enums.WinnerState;

public class Game {
    
    /**Lancer une partie entre deux joueurs de Gomoku
     * @param player1 Joueur 1
     * @param player2 Joueur 2
     * @return Vainqueur de la partie
    */
    public static WinnerState startMatch(PlayerController player1, PlayerController player2){
        GomokuBoard board = new GomokuBoard();
        WinnerState winnerState;
        Player currentPlayer = Player.White;

        int roundCount = 0;

        long player1TotalPlayTime = 0;
        long player2TotalPlayTime = 0;
        long player1LongestPlayTime = 0;
        long player2LongestPlayTime = 0;
        long player1ShortestPlayTime = Long.MAX_VALUE;
        long player2ShortestPlayTime = Long.MAX_VALUE;

        while ((winnerState = board.getWinnerState()) == WinnerState.None){ // Tant que la partie n'est pas finie
            roundCount++;

            System.out.println("Tour " + roundCount + ": " + (currentPlayer == Player.White ? "BLANC" : "NOIR"));
            long startTime = System.currentTimeMillis();
            Coords move = currentPlayer == Player.White ? player1.play(board, Player.White) : player2.play(board, Player.Black); // Obtenir le coup du joueur
            long moveDuration = System.currentTimeMillis() - startTime;

            if (currentPlayer == Player.White){
                player1TotalPlayTime += moveDuration;
                player1LongestPlayTime = Math.max(player1LongestPlayTime, moveDuration);
                player1ShortestPlayTime = Math.min(player1ShortestPlayTime, moveDuration);
            }
            else{
                player2TotalPlayTime += moveDuration;
                player2LongestPlayTime = Math.max(player2LongestPlayTime, moveDuration);
                player2ShortestPlayTime = Math.min(player2ShortestPlayTime, moveDuration);
            }

            board.set(move, currentPlayer == Player.White ? TileState.White : TileState.Black); // Jouer le coup

            System.out.println("Ligne: " + move.row);
            System.out.println("Colonne: " + move.column);
            System.out.println();
            board.print();
            System.out.println();

            currentPlayer = currentPlayer == Player.White ? Player.Black : Player.White; // Changer de joueur
        }

        if (winnerState == WinnerState.Tie) System.out.println("Égalité !");
        else System.out.println("Vainqueur: " + (winnerState == WinnerState.White ? "Blanc" : "Noir"));

        double movePerPlayer = (double)(roundCount / 2);
        int player1MoveCount = (int)Math.ceil(movePerPlayer);
        int player2MoveCount = (int)Math.floor(movePerPlayer);

        System.out.println("\nStatistiques :");
        System.out.println("Blanc: " + player1MoveCount + " coups, " + (player1TotalPlayTime/player1MoveCount) + " ms/coup, " + player1LongestPlayTime + " ms (max), " + player1ShortestPlayTime + " ms (min)");
        System.out.println("Noir: " + player2MoveCount + " coups, " + (player2TotalPlayTime/player2MoveCount) + " ms/coup, " + player2LongestPlayTime + " ms (max), " + player2ShortestPlayTime + " ms (min)");

        return winnerState;
    }

    public static void main(String[] args) {
       // startMatch(new AI_Sweep(2), new AI_Sweep(2)); // Lancer une partie entre deux IA Sweep
        //startMatch(new AI_Star(3), new AI_Star(3)); // Lancer une partie entre deux IA Star
        //startMatch(new AI_Star(2), new AI_Sweep(2)); // Lancer une partie entre une IA Sweep et une IA Star
        startMatch(new HumanPlayer(), new AI_Random(2)); // Lancer une partie entre un joueur humain et une IA Sweep
    }

}
