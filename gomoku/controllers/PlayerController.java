package projet1.gomoku.controllers;

import projet1.gomoku.gamecore.Coords;
import projet1.gomoku.gamecore.GomokuBoard;
import projet1.gomoku.gamecore.enums.Player;

/**Représente un joueur de Gomoku*/
public abstract class PlayerController {
    /**
     * Permet à un joueur de jouer un coup
     * @param board Plateau de jeu
     * @param player Joueur qui doit jouer
     * @return Coordonnées du coup joué
     */
    public abstract Coords play(GomokuBoard board, Player player);
}
