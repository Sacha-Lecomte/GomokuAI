package gamecore;

/**Représente un score pour une case du plateau de jeu*/
public class TileScore{
    public Coords coords;
    public int score;

    public TileScore(Coords coords, int score){
        this.coords = coords;
        this.score = score;
    }
}
