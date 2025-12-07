package gamecore.ai;

import java.util.ArrayList;

import gamecore.Coords;

public class GameTree {
	
	public Coords coup;
	public Integer old_borne; //borne du grand-père min ou max
	public Integer borne; //borne du père min ou max 
	public ArrayList<GameTree> subTrees; //feuilles de l'arbre
	
	public GameTree() {
		this.coup = null;
		this.old_borne = null;
		this.borne = null;
		this.subTrees = new ArrayList<GameTree>();
	}
	
	public GameTree(Coords coup, Integer old_borne, Integer borne, ArrayList<GameTree> subTrees) {
		this.coup = coup;
		this.old_borne = old_borne;
		this.borne = borne;
		this.subTrees = subTrees;
	}
	
}
