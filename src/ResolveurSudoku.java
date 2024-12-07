package src;

import java.util.ArrayList;
import java.util.List;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import src.Generateurs.*;

public class ResolveurSudoku {

    private final int tailleSousGrille;
    private final int tailleGrille;
    private final Model model;
    private int[][] grille;

    /**
     * Constructeur d'un résolveur de sudoku
     * @param tailleSousGrille la taille de la sous-grille
     */
    public ResolveurSudoku(int tailleSousGrille) {
        this.tailleSousGrille = tailleSousGrille; // Taille de la sous-grille
        this.tailleGrille = tailleSousGrille * tailleSousGrille; // Taille de la grille
        this.model = new Model("Sudoku"); // Crée le modèle
        this.grille = new int[tailleGrille][tailleGrille];
        GenerateurGrilleValide generateur = new GenerateurGrilleValide(this.tailleGrille);
        grille = generateur.getGrille();
    }

    //region RESOLUTION AVEC CHOCOSOLVER

    /**
     * Résout une grille de sudoku avec ChocoSolver
     */
    public void resolutionChoco() {
        afficheGrille(grille);
        // Crée les variables
        IntVar[][] grille = new IntVar[this.tailleGrille][this.tailleGrille];
        for (int i = 0; i < this.tailleGrille; i++) {
            for (int j = 0; j < this.tailleGrille; j++) {
                grille[i][j] = model.intVar("grid[" + i + "," + j + "]", 1, this.tailleGrille);
            }
        }

        // Ajoute les contraintes
        ajoutContraintes(grille);

        model.getSolver().limitSolution(10); // Limite à 10 solutions

        // Résolution
        int solutionCount = 0;
        while (model.getSolver().solve()) {
            solutionCount++;
            System.out.println("Solution #" + solutionCount);
            int[][] grilleFinale = new int[this.tailleGrille][this.tailleGrille];
            for (int i = 0; i < this.tailleGrille; i++) {
                for (int j = 0; j < this.tailleGrille; j++) {
                    grilleFinale[i][j] = grille[i][j].getValue();
                }
            }
            afficheGrille(grilleFinale);
        }

        System.out.println("Nombre total de solutions trouvées : " + solutionCount);
    }

    /**
     * Ajoute les contraintes (lignes, colonnes, sous-grilles)
     * @param grille la grille de variables
     */
    private void ajoutContraintes(IntVar[][] grille) {
        // Ajoute les contraintes pour chaque ligne
        for (int i = 0; i < this.tailleGrille; i++) {
            model.allDifferent(grille[i]).post();
        }

        // Ajoute les contraintes pour chaque colonne
        for (int j = 0; j < this.tailleGrille; j++) {
            IntVar[] colonne = new IntVar[this.tailleGrille];
            for (int i = 0; i < this.tailleGrille; i++) {
                colonne[i] = grille[i][j];
            }
            model.allDifferent(colonne).post();
        }

        // Ajoute les contraintes pour chaque sous-grille
        for (int ligne = 0; ligne < this.tailleGrille; ligne += this.tailleSousGrille) {
            for (int col = 0; col < this.tailleGrille; col += this.tailleSousGrille) {
                IntVar[] sousGrille = new IntVar[this.tailleGrille];
                int index = 0;
                for (int i = 0; i < this.tailleSousGrille; i++) {
                    for (int j = 0; j < this.tailleSousGrille; j++) {
                        sousGrille[index++] = grille[ligne + i][col + j];
                    }
                }
                model.allDifferent(sousGrille).post();
            }
        }
    }

    //endregion

    //region RESOLUTION AVEC BACKTRACKING

    /**
     * Résout une grille de sudoku avec la méthode complète de backtracking
     */
    public boolean backtracking() {
        afficheGrille(grille);
        // Parcours de la grille
        for(int ligne = 0; ligne < this.tailleGrille; ligne++){
            for(int col = 0; col< this.tailleGrille; col++){
                // Dans le cas où la case est vide, on essaye de placer une valeur
                if (grille[ligne][col] == 0) {
                    for(int valeur = 1; valeur <= this.tailleGrille; valeur++) {
                        // Si la valeur est valide, on la place
                        if (isValid(ligne, col, valeur)) {
                            grille[ligne][col] = valeur;
                            if (backtracking()){
                                return true;
                            } else {
                                grille[ligne][col] = 0;
                            }
                        }
                    }
                    return false;
                }
            }
        }
        afficheGrille(grille);
        return true;
    }

    /**
     * Vérifie si une valeur est valide pour une case donnée
     * @param ligne la ligne
     * @param col la colonne
     * @param valeur la valeur à tester
     * @return true si la valeur est valide, false sinon
     */
    private boolean isValid(int ligne, int col, int valeur){
        // Vérifier la ligne et la colonne
        for(int i=0; i< this.tailleGrille; i++){
            if (grille[ligne][i] == valeur || grille[i][col] == valeur){
                return false;
            }
        }

        // Vérifier la sous-grille
        int startRow = ligne - ligne % tailleSousGrille;
        int startCol = col - col % tailleSousGrille;
        for (int i = 0; i < tailleSousGrille; i++) {
            for (int j = 0; j < tailleSousGrille; j++) {
                if (grille[startRow + i][startCol + j] == valeur) {
                    return false;
                }
            }
        }

        return true; // Le placement est sûr.

    }

    //endregion

    //region RESOLUTION AVEC LA RECHERCHE LOCALE

    /**
     * Résout une grille de sudoku avec la méthode incomplète de recherche locale
     */
    public void rechercheLocale() {
        // Génère une solution initiale fausse
        GenerateurSolutionFausse generateur = new GenerateurSolutionFausse(this.tailleGrille);
        int[][] solution = generateur.getGrille();
        afficheGrille(solution);

        // Evaluation de la solution initiale
        int evaluation = evaluationSolution(solution);
        System.out.println("Evaluation initiale: " + evaluation);

        // Recherche locale
        for (int i = 0; i < 10000000; i++) {
            int[][] solutionVoisine = genererSolutionVoisine(solution);
            int evaluationVoisine = evaluationSolution(solutionVoisine);

            // Si la solution voisine est meilleure, on la garde
            if (evaluationVoisine < evaluation) {
                solution = solutionVoisine;
                evaluation = evaluationVoisine;
            }

            // Si on a trouvé une solution valide, on s'arrête
            if (evaluation == 0) {
                System.out.println("Solution trouvée après " + i + " itérations");
                break;
            }
        }
        afficheGrille(solution);
        System.out.println("Evaluation finale: " + evaluation);
    }
    /**
     * Génère une solution voisine en changeant une case aléatoire
     * @param solution la solution actuelle
     * @return la solution voisine
     */
    private int[][] genererSolutionVoisine(int[][] solution) {
        // On copie la solution actuelle
        int[][] solutionVoisine = new int[this.tailleGrille][this.tailleGrille];
        for (int i = 0; i < this.tailleGrille; i++) {
            System.arraycopy(solution[i], 0, solutionVoisine[i], 0, this.tailleGrille);
        }

        // On choisit une case aléatoire
        int ligne = (int) (Math.random() * this.tailleGrille);
        int colonne = (int) (Math.random() * this.tailleGrille);

        // On choisit une valeur aléatoire
        int valeur = (int) (Math.random() * this.tailleGrille) + 1;

        // On change la valeur de la case choisie
        solutionVoisine[ligne][colonne] = valeur;

        return solutionVoisine;
    }

    /**
     * Fonction qui compte le nombre de contraintes non respectées
     * @param solution la solution à évaluer
     * @return le nombre de contraintes non respectées
     */
    private int evaluationSolution(int[][] solution) {
        int evaluation = 0;
        // Parcours des cases de la grille
        for (int i = 0; i < this.tailleGrille; i++) {
            for (int j = 0; j < this.tailleGrille; j++) {
                int valeur = solution[i][j];
                // Vérification de la colonne
                for (int k = 0; k < this.tailleGrille; k++) {
                    if (i != k && solution[k][j] == valeur) {
                        evaluation++;
                    }
                }

                // Vérification de la sous-grille
                int sousGrilleTaille = (int) Math.sqrt(this.tailleGrille);
                int debutLigne = i - i % sousGrilleTaille;
                int debutColonne = j - j % sousGrilleTaille;
                for (int k = 0; k < sousGrilleTaille; k++) {
                    for (int l = 0; l < sousGrilleTaille; l++) {
                        if (solution[debutLigne + k][debutColonne + l] == valeur && (debutLigne + k != i || debutColonne + l != j)) {
                            evaluation++;
                        }
                    }
                }
            }
        }
        return evaluation;
    }

    //endregion

    //region HEURISTIQUE GLOUTONNE

    /**
     * Compte le nombre de contraintes pour chaque case
     * @return un tableau de contraintes
     */
    private int[][] compteContraintes(){
        int[][] contraintes = new int[tailleGrille][tailleGrille];
        // Parcours de la grille
        for(int i = 0; i < this.tailleGrille; i++){
            for(int j = 0; j < this.tailleGrille; j++){
                // Si la case est vide
                if(grille[i][j] == 0){
                    // Parcours la ligne et compte le nombre de cases remplies
                    for(int k = 0; k < this.tailleGrille; k++){
                        contraintes[i][j] += grille[i][k] == 0 ? 0 : 1;
                    }
                    // Parcours la colonne et compte le nombre de cases remplies
                    for(int k = 0; k < this.tailleGrille; k++){
                        contraintes[i][j] += grille[k][j] == 0 ? 0 : 1;
                    }
                    // Parcours la sous-grille de compte les contraintes
                    int sousGrilleTaille = (int) Math.sqrt(this.tailleGrille);
                    int startRow = i - i % sousGrilleTaille;
                    int startCol = j - j % sousGrilleTaille;
                    for (int k = 0; k < sousGrilleTaille; k++) {
                        for (int l = 0; l < sousGrilleTaille; l++) {
                            int r = startRow + k;
                            int c = startCol + l;

                            // Vérifie que la case n'est pas sur la même ligne ou colonne
                            if (r != i && c != j) {
                                contraintes[i][j] += grille[r][c] == 0 ? 0 : 1;
                            }
                        }
                    }
                }
            }
        }
        return contraintes;
    }

    /**
     * Résout une grille de sudoku avec la méthode incomplète de l'heuristique gloutonne
     */
    public void gloutonPlusContraint(){
        for (int iteration = 0; iteration < 10000000; iteration++){
            // Compte le nombre de contraintes pour chaque case
            int[][] contraintes = compteContraintes();

            // On liste les cases vides
            List<int[]> cells = new ArrayList<>();
            for(int i = 0; i < this.tailleGrille; i++){
                for(int j = 0; j < this.tailleGrille; j++){
                    // Si la case est vide, on ajoute ses coordonnées et le nombre de contraintes
                    if(grille[i][j] == 0){
                        cells.add(new int[]{i, j, contraintes[i][j]});
                    }
                }
            }
            // Trie les cellules du plus grand nombre de contraintes au plus petit
            cells.sort((a, b) -> Integer.compare(b[2], a[2]));

            // Parcours des cases vides
            for(int[] cell : cells){
                int row = cell[0];
                int col = cell[1];
                if (grille[row][col] == 0){
                    for(int value = 1; value <= this.tailleGrille; value++) {
                        if (isValid(row, col, value)) {
                            grille[row][col] = value;
                            break;
                        }
                    }
                }
            }
        }
        afficheGrille(grille);
    }
    //endregion

    //region AFFICHAGE DE LA GRILLE

    /**
     * Affichage de la grille
     * @param grille la grille à afficher
     */
    private void afficheGrille(int[][] grille) {
        int sousGrilleTaille = (int) Math.sqrt(this.tailleGrille);
        
        String separator = "";
        for (int i = 0; i < this.tailleGrille * 2 + sousGrilleTaille + 1; i++) {
            separator += "-"; 
        }
    
        for (int i = 0; i < this.tailleGrille; i++) {
            // horizontal separator
            if (i % sousGrilleTaille == 0) {
                System.out.println(separator);
            }
    
            for (int j = 0; j < this.tailleGrille; j++) {
                // Vertical separator 
                if (j % sousGrilleTaille == 0) {
                    System.out.print("| ");
                }
    
                
                if (grille[i][j] == 0) {
                    System.out.print(" . "); // Empty cells 
                } else {
                    // alignment
                    System.out.printf("%2d ", grille[i][j]);
                }
            }
            //vertical border
            System.out.println("|");
        }
    
        // line of separation
        System.out.println(separator);
    }
    //endregion
}

