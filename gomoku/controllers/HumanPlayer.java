package projet1.gomoku.controllers;

import java.util.Scanner;

import projet1.gomoku.gamecore.Coords;
import projet1.gomoku.gamecore.GomokuBoard;
import projet1.gomoku.gamecore.enums.Player;
import projet1.gomoku.gamecore.enums.TileState;

/**Représente un joueur de Gomoku humain*/
public class HumanPlayer extends PlayerController {
    private Scanner scanner;

    public HumanPlayer(){
        scanner = new Scanner(System.in);
    }

    @Override
    public Coords play(GomokuBoard board, Player player){
        Coords coords = new Coords();

        while (coords.row == -1){ // Tant que la ligne n'est pas définie
            try {
                System.out.print("Ligne: ");
                coords.row = scanner.nextInt();

                if (!board.areCoordsValid(0, coords.row)){
                    System.out.println("Cette ligne n'existe pas.");
                    coords.row = -1; // Réinitialiser la ligne
                }
            }
            catch(Exception e){
                System.out.println("Valeur invalide.");
            }
        }

        while (coords.column == -1){
            try {
                System.out.print("Colonne: ");
                coords.column = scanner.nextInt();

                if (!board.areCoordsValid(coords)){
                    System.out.println("Cette colonne n'existe pas.");
                    coords.column = -1; // Réinitialiser la colonne
                }
                else if (board.get(coords) != TileState.Empty){
                    System.out.println("Cette case est déjà occupée.");
                    coords.column = -1; // Réinitialiser la colonne
                }
            }
            catch(Exception e){
                System.out.println("Valeur invalide.");
            }
        }

        return coords;
    }
}
