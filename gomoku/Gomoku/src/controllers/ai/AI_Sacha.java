package controllers.ai;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;

import controllers.PlayerController;
import gamecore.Coords;
import gamecore.GomokuBoard;
import gamecore.ai.GameTree;
import gamecore.enums.Player;
import gamecore.enums.TileState;
import gamecore.enums.WinnerState;


public class AI_Sacha extends PlayerController {

    public int minimaxDepth = 2; // profondeur minimax par défaut augmentée pour que l'IA puisse anticiper les réponses adverses
	public final static int SCORE_3_STRAIGTH = 50; //0OOOXX
	public final static int SCORE_3_DOUBLE = 100; //XXOOOXX
	public final static int SCORE_3_BROKEN = 100; //XXOXOOXX
	public final static int SCORE_4_STRAIGTH = 1000; //0OOOOXX
	public final static int SCORE_4_DOUBLE = 10000; //XXOOOOXX
	public final static int SCORE_4_BROKEN = 1000; //XXOXOOOXX
	public final static int SCORE_5 = 1000000; //OOOOO
	public final static int SCORE_5_BROKEN = 10000; //XXOOXOOOXX
	
    //fonction pour customiser son board et tester l'évaluation
    public static void main(String[] args) {
        GomokuBoard b = new GomokuBoard();
        System.out.println();
        
        try (Scanner scanner = new Scanner(System.in)) {
        System.out.println("Randomize?");
        if(scanner.nextInt() == 1) {
            b.randomize(0.3, 0.2);
            b.print();
        }
        else {
			Player p = Player.White;
			int message = 0;
			b.print();
			
			while(message == 0) {
				Coords coords = new Coords();
				
		        while (coords.row == -1){ // Tant que la ligne n'est pas définie
		            try {
		                System.out.print("Ligne: ");
		                coords.row = scanner.nextInt();
		
		                if (!b.areCoordsValid(0, coords.row)){
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
		
		                if (!b.areCoordsValid(coords)){
		                    System.out.println("Cette colonne n'existe pas.");
		                    coords.column = -1; // Réinitialiser la colonne
		                }
		                else if (b.get(coords) != TileState.Empty){
		                    System.out.println("Cette case est déjà occupée.");
		                    coords.column = -1; // Réinitialiser la colonne
		                }
		            }
		            catch(Exception e){
		                System.out.println("Valeur invalide.");
		            }
		        }
		        
		        
		        b.set(coords,(p == Player.White) ? TileState.White : TileState.Black);
		        
		        b.print();
		        
		        System.out.println("On s'arrête ?");
		        message = scanner.nextInt();
		        if(message==1) {
		        	p = (p == Player.White) ? Player.Black : Player.White;
		        	message=0;
		        }
		        else if(message==2) {
		        	System.out.println("\nLe score obtenu sur ce board pour le joueur blanc est " + evaluateBoard(b, Player.White));
		        	message=0;
		        }
			}
        }
        }

        System.out.println("\nLe score obtenu sur ce board pour le joueur blanc est " + evaluateBoard(b, Player.White));
    }
	
	public AI_Sacha(int minimaxDepth){
        this.minimaxDepth = minimaxDepth;
    }

    public AI_Sacha(){
        super();
    }

    // Motifs et scores partagés (construits une seule fois)
    private static final Map<String,Integer> PATTERNS = createPatterns();
    private static final Map<String,Integer> OPP_PATTERNS = createOppPatterns(PATTERNS);

    private static Map<String,Integer> createPatterns(){
        Map<String,Integer> patterns = new HashMap<>();
        patterns.put("OOOOO", SCORE_5);
        patterns.put(".OOOO.", SCORE_4_DOUBLE);
        patterns.put("XOOOO.", SCORE_4_STRAIGTH);
        patterns.put(".OOOOX", SCORE_4_STRAIGTH);
        patterns.put("OOO.O", SCORE_4_BROKEN);
        patterns.put("O.OOO", SCORE_4_BROKEN);
        patterns.put(".OOO.", SCORE_3_STRAIGTH);
        patterns.put("O.OO.", SCORE_3_BROKEN);
        patterns.put(".O.OO", SCORE_3_BROKEN);
        patterns.put(".OO.O", SCORE_3_BROKEN);
        return patterns;
    }

    private static Map<String,Integer> createOppPatterns(Map<String,Integer> base){
        Map<String,Integer> opp = new HashMap<>();
        for(Map.Entry<String,Integer> e : base.entrySet()){
            String p = e.getKey().replace('O','t').replace('X','O').replace('t','X');
            opp.put(p, e.getValue());
        }
        return opp;
    }

    // Nouvelle évaluation : parcourir les lignes, les colonnes et les deux diagonales et reconnaître des motifs simples.
    // Pour chaque ligne on crée une chaîne avec 'O' = joueur, 'X' = adversaire, '.' = vide.
    // On recherche ensuite les motifs et on additionne les scores. Les motifs adverses sont soustraits (pondération amplifiée).
    public static int evaluateBoard(GomokuBoard board, Player player){
        int score = 0;
        TileState my = player == Player.White ? TileState.White : TileState.Black;
        TileState opp = player == Player.White ? TileState.Black : TileState.White;

        // Rassembler toutes les lignes (rangées, colonnes, diag1, diag2)
        ArrayList<String> lines = new ArrayList<>();
        
        // rangées
        for(int r=0; r<GomokuBoard.size; r++){
            StringBuilder sb = new StringBuilder();
            for(int c=0; c<GomokuBoard.size; c++) sb.append(tileChar(board.get(c,r), my, opp));
            lines.add(sb.toString());
        }
        
        // colonnes
        for(int c=0; c<GomokuBoard.size; c++){
            StringBuilder sb = new StringBuilder();
            for(int r=0; r<GomokuBoard.size; r++) sb.append(tileChar(board.get(c,r), my, opp));
            lines.add(sb.toString());
        }

        // diag \ (r augmente, c augmente)
        for(int start=-(GomokuBoard.size-1); start<=GomokuBoard.size-1; start++){
            StringBuilder sb = new StringBuilder();
            for(int r=0; r<GomokuBoard.size; r++){
                int c = r - start; // c = r - start => when start = r - c
                if(c>=0 && c<GomokuBoard.size) sb.append(tileChar(board.get(c,r), my, opp));
            }
            if(sb.length()>=1) lines.add(sb.toString());
        }
        
        // diag / (r augmente, c diminue)
        for(int start=0; start<=2*(GomokuBoard.size-1); start++){
            StringBuilder sb = new StringBuilder();
            for(int r=0; r<GomokuBoard.size; r++){
                int c = start - r;
                if(c>=0 && c<GomokuBoard.size) sb.append(tileChar(board.get(c,r), my, opp));
            }
            if(sb.length()>=1) lines.add(sb.toString());
        }

        // Évaluer pour le joueur en utilisant les maps pré-calculées
        for(String line : lines){
            for(Map.Entry<String,Integer> e : PATTERNS.entrySet()){
                int cnt = countOccurrences(line, e.getKey());
                if(cnt>0) score += cnt * e.getValue();
            }
        }

        // Évaluer les motifs adverses (pré-calculés) et les soustraire (pondération plus élevée)
        int oppScore = 0;
        for(String line : lines){
            for(Map.Entry<String,Integer> e : OPP_PATTERNS.entrySet()){
                int cnt = countOccurrences(line, e.getKey());
                if(cnt>0) oppScore += cnt * e.getValue();
            }
        }

        // Amplifier la menace adverse
        score -= oppScore * 5;

        return score;
    }

    private static char tileChar(TileState t, TileState my, TileState opp){
        if(t == my) return 'O';
        if(t == opp) return 'X';
        return '.';
    }

    private static int countOccurrences(String s, String sub){
        if(sub.length() == 0) return 0;
        int count = 0;
        for(int idx = s.indexOf(sub); idx != -1; idx = s.indexOf(sub, idx+1)) count++;
        return count;
    }


    public ArrayList<Coords> getAvailableMoves(GomokuBoard board){
        Coords currentCellCoords = new Coords();
        
        ArrayList<Coords> moves = new ArrayList<Coords>();
    // Restreindre les coups à un voisinage autour des pierres existantes pour accélérer la recherche
    // et éviter de choisir des coups éloignés non pertinents. On marque comme candidats toutes
    // les cases situées à une distance de 3 de toute pierre existante (rayon élargi pour tenir
    // compte des menaces sur plusieurs coups).
        int radius = 3;
        boolean[][] near = new boolean[GomokuBoard.size][GomokuBoard.size];
        for (currentCellCoords.row = 0; currentCellCoords.row < GomokuBoard.size; currentCellCoords.row++){
            for (currentCellCoords.column = 0; currentCellCoords.column < GomokuBoard.size; currentCellCoords.column++){
                if (board.get(currentCellCoords) != TileState.Empty){
                    for (int dr = -radius; dr <= radius; dr++){
                        for (int dc = -radius; dc <= radius; dc++){
                            int nr = currentCellCoords.row + dr;
                            int nc = currentCellCoords.column + dc;
                            if(board.areCoordsValid(nc, nr)) near[nc][nr] = true;
                        }
                    }
                }
            }
        }

        for (currentCellCoords.row = 0; currentCellCoords.row < GomokuBoard.size; currentCellCoords.row++){
            for (currentCellCoords.column = 0; currentCellCoords.column < GomokuBoard.size; currentCellCoords.column++){
                if (board.get(currentCellCoords) == TileState.Empty){ // Si la case est vide
                    if(near[currentCellCoords.column][currentCellCoords.row]){
                        moves.add(currentCellCoords.clone()); // Enregistrer le coup rapproché
                    }
                }
            }
        }

    // Si aucun coup trouvé (plateau vide), revenir au centre
        if(moves.isEmpty()){
            int center = GomokuBoard.size/2;
            moves.add(new Coords(center, center));
            return moves;
        }

    // Trier les coups par distance au centre (préférer les coups centraux)
        final int center = GomokuBoard.size/2;
        moves.sort((a,b) -> {
            int da = Math.abs(a.column - center) + Math.abs(a.row - center);
            int db = Math.abs(b.column - center) + Math.abs(b.row - center);
            return Integer.compare(da, db);
        });

        return moves;
    }

    //Compte combien de coups gagnants immédiats le joueur spécifié aurait sur ce plateau.
    //Version optimisée : restreint les candidats via getAvailableMoves() et vérifie localement autour de la case
    //sans parcourir tout le plateau (implémente la même logique directionnelle que getWinnerState mais centrée sur une case).
    private boolean checkImmediateWinAt(GomokuBoard board, Coords coord, TileState playerState){
        final int[][] DIRS = {{-1,-1},{-1,0},{-1,1},{0,1}}; // mêmes directions que dans GomokuBoard.getWinnerState
        for (int[] d : DIRS) {
            int run = 1; // la pierre posée
            // avancer dans la direction
            for (int step = 1; step <= 4; step++){
                int r = coord.row + d[0]*step;
                int c = coord.column + d[1]*step;
                if(!board.areCoordsValid(c, r) || board.get(c, r) != playerState) break;
                run++;
            }
            // reculer dans la direction opposée
            for (int step = 1; step <= 4; step++){
                int r = coord.row - d[0]*step;
                int c = coord.column - d[1]*step;
                if(!board.areCoordsValid(c, r) || board.get(c, r) != playerState) break;
                run++;
            }
            if(run >= 5) return true;
        }
        return false;
    }

    private int countImmediateWinningMoves(GomokuBoard board, TileState playerState){
        // limiter aux coups candidats proches des pierres existantes
        ArrayList<Coords> candidates = getAvailableMoves(board);
        int count = 0;
        for(Coords cc : candidates){
            if(board.get(cc) != TileState.Empty) continue;
            board.set(cc, playerState);
            boolean win = checkImmediateWinAt(board, cc, playerState);
            board.set(cc, TileState.Empty);
            if(win) count++;
        }
        return count;
    }
    
    
    public Coords getBestMove(GomokuBoard board, Player initialPlayer, Player playerLayer, int depth, GameTree tree){

    	if(depth == 0) { //on doit évaluer
        	int score = evaluateBoard(board, initialPlayer); // Evaluer le coup
        	//System.out.println("EVALUE " + tree.coup.column + " " + tree.coup.row + " "  + score + " " + (playerLayer == Player.White ? "B" : "W"));
        	tree.borne = score;
    	}
    	else { //on doit encore descendre
    		
    		TileState playerCellState = playerLayer == Player.White ? TileState.White : TileState.Black;
            
            ArrayList<Coords> availableMoves = getAvailableMoves(board);
            
//            if(tree.coup != null) System.out.println("DESCENDU " + depth + " " + tree.coup.column + " " + tree.coup.row  + " " + (playerLayer == Player.White ? "B" : "W"));
            
            for(Coords move : availableMoves) {	
            
            	board.set(move, playerCellState); // Jouer le coup pour évaluer
    	        
            	//création de l'arbre de possibilité associé
            	GameTree feuille = new GameTree(move,tree.borne,null,new ArrayList<GameTree>());
            
	        	
	        	tree.subTrees.add(feuille); //rattachement de la feuille à l'arbre du niveau actuel

	        	//affichage des élagages effectués lors de l'exploration du sous-arbre
//	        	if(getBestMove(board, initialPlayer, playerLayer == Player.White ? Player.Black : Player.White, depth-1, feuille) == null) { //On descend d'une profondeur
//        			System.out.println("ELAGAGE "+depth);
//	        	}
	        	//exploration du sous-arbre de coup possible généré
	        	getBestMove(board, initialPlayer, playerLayer == Player.White ? Player.Black : Player.White, depth-1, feuille);
	        	
	        	//remontée du score de la feuille explorée
	        	if(feuille.borne != null) {//si la feuille n'a pas été élaguée (borne = null)
	        		if(initialPlayer == playerLayer) { //On cherche le max
		        		if(tree.old_borne != null && feuille.borne  > tree.old_borne) { //si élagage possible, on l'effectue
		        			board.set(move, TileState.Empty); // Annuler le coup pour return null : élagage possible
		        			return null;
		        		}
		        		if(tree.borne == null || feuille.borne  > tree.borne) {
		        			tree.borne = feuille.borne;
//		        			System.out.println("MAX "+depth+": "+tree.borne);
		        		}
		        	}
		        	else { //On cherche le min
		        		if(tree.old_borne != null && feuille.borne < tree.old_borne) { //si élagage possible, on l'effectue
		        			board.set(move, TileState.Empty); // Annuler le coup pour return null : élagage possible
		        			return null;
		        		}
		        		if(tree.borne == null || feuille.borne < tree.borne) {
		        			tree.borne = feuille.borne;
//		        			System.out.println("MIN "+depth+": "+tree.borne);
		        		}
		        	}
	        	}
            
    	        board.set(move, TileState.Empty); // Annuler le coup
            }            

    	}
                
    // sélection du meilleur coup parmi ceux de la profondeur 1
    if(tree.coup == null) {
    	// Si on a des coups candidats, préférer ceux qui minimisent les réponses gagnantes immédiates de l'adversaire (défense contre double-menace)
        	int bestIndex = 0;
        	int bestOpponentReplies = Integer.MAX_VALUE;
        	int bestScore = Integer.MIN_VALUE;
        	for(int i=0; i<tree.subTrees.size(); i++) {
        		GameTree sub = tree.subTrees.get(i);
        		if(sub.borne == null) continue;
                // calculer combien de réponses gagnantes immédiates l'adversaire aurait si on joue ce coup
        		Coords candidate = sub.coup;
        		board.set(candidate, (initialPlayer == Player.White) ? TileState.White : TileState.Black);
        		int oppReplies = countImmediateWinningMoves(board, (initialPlayer == Player.White) ? TileState.Black : TileState.White);
        		board.set(candidate, TileState.Empty);
                // préférer moins de réponses adverses, puis une borne plus élevée (notre score évalué)
        		if(oppReplies < bestOpponentReplies || (oppReplies == bestOpponentReplies && sub.borne > bestScore)){
        			bestOpponentReplies = oppReplies;
        			bestScore = sub.borne;
        			bestIndex = i;
        		}
        	}
        	Coords bestMove = tree.subTrees.get(bestIndex).coup;
        	return bestMove;
        }
        else return tree.coup;

    }
    
	@Override
	public Coords play(GomokuBoard board, Player player) {
        // 1) Immediate winning move for self
        TileState myState = (player == Player.White) ? TileState.White : TileState.Black;
        TileState oppState = (player == Player.White) ? TileState.Black : TileState.White;
        WinnerState myWin = (player == Player.White) ? WinnerState.White : WinnerState.Black;
        WinnerState oppWin = (player == Player.White) ? WinnerState.Black : WinnerState.White;

        Coords tryCoords = new Coords();
        Coords firstOppImmediateWin = null;
        Coords firstOppDoubleThreat = null;

    // Parcours unique : vérifier une victoire immédiate pour soi (retourner), enregistrer une victoire immédiate adverse
    // et enregistrer la création d'une double-menace adverse (>=2 victoires immédiates). Priorités : victoire perso > bloquer victoire adverse immédiate > bloquer double-menace adverse.
        for(tryCoords.row = 0; tryCoords.row < GomokuBoard.size; tryCoords.row++){
            for(tryCoords.column = 0; tryCoords.column < GomokuBoard.size; tryCoords.column++){
                if(board.get(tryCoords) == TileState.Empty){
                    // 1) Vérifier victoire immédiate pour soi
                    board.set(tryCoords, myState);
                    WinnerState resMy = board.getWinnerState();
                    board.set(tryCoords, TileState.Empty);
                    if(resMy == myWin) return tryCoords.clone();

                    // 2) Vérifier victoire immédiate de l'adversaire (enregistrer le premier)
                    board.set(tryCoords, oppState);
                    WinnerState resOpp = board.getWinnerState();
                    if(resOpp == oppWin){
                        if(firstOppImmediateWin == null) firstOppImmediateWin = tryCoords.clone();
                        board.set(tryCoords, TileState.Empty);
                        // Si on a trouvé une victoire immédiate adverse on continue le balayage pour éventuellement trouver
                        // une victoire personnelle sur une autre case, mais comme on a déjà vérifié la victoire personnelle sur
                        // cette case on peut passer à la cellule suivante.
                        continue;
                    }

                    // 2.5) Vérifier la création d'une double-menace adverse (enregistrer le premier)
                    int oppImmediateWins = countImmediateWinningMoves(board, oppState);
                    if(oppImmediateWins >= 2 && firstOppDoubleThreat == null){
                        firstOppDoubleThreat = tryCoords.clone();
                    }

                    // nettoyage / restauration de l'état
                    board.set(tryCoords, TileState.Empty);
                }
            }
        }

        // After single pass, prioritize blocking immediate opponent win, then double-threat
        if(firstOppImmediateWin != null) return firstOppImmediateWin;
        if(firstOppDoubleThreat != null) return firstOppDoubleThreat;

        // 3) Otherwise use minimax search
        return getBestMove(board, player, player, minimaxDepth, new GameTree());
	}
}
