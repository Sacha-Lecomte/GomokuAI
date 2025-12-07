package gamecore.enums;

/**Liste des états possibles pour la fin d'une partie*/
public enum WinnerState{
    /**Victoire du joueur blanc*/
    White,

    /**Victoire du joueur noir*/
    Black,

    /**Partie non finie*/
    None,

    /**Egalité (plus de place sur le plateau)*/
    Tie
}
