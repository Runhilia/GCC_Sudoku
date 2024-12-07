package src;

import java.util.ArrayList;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.IntVar;

import java.util.List;
import javax.swing.GroupLayout;

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
        GenerateurGrille generateur = new GenerateurGrille(this.tailleGrille);
        this.grille = generateur.getGrille();
    }

    /**
     * Résout une grille de sudoku
     */
    public void resolution() {
        // Crée les variables
        IntVar[][] grille = new IntVar[this.tailleGrille][this.tailleGrille];
        for (int i = 0; i < this.tailleGrille; i++) {
            for (int j = 0; j < this.tailleGrille; j++) {
                grille[i][j] = model.intVar("grid[" + i + "," + j + "]", 1, this.tailleGrille);
            }
        }

        // Ajoute les contraintes
        ajoutContraintes(grille);

        // Génère la grille initiale
        int[][] grilleInitiale = genereGrilleInitiale(grille);
        afficheGrille(grilleInitiale);


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

    /**
     * Génère une grille initiale
     * @param grille la grille de variables
     * @return la grille initiale
     */
    private int[][] genereGrilleInitiale(IntVar[][] grille) {
        GenerateurGrille generateur = new GenerateurGrille(this.tailleGrille);
        int[][] grilleInitiale = generateur.getGrille();

        for (int i = 0; i < tailleGrille; i++) {
            for (int j = 0; j < tailleGrille; j++) {
                if (grilleInitiale[i][j] > 0) {
                    model.arithm(grille[i][j], "=", grilleInitiale[i][j]).post();
                }
            }
        }
        return grilleInitiale;
    }


    public boolean backtracking(){
        for(int row = 0; row < this.tailleGrille; row++){
            for(int col = 0; col< this.tailleGrille; col++){
                if (grille[row][col] == 0){
                    for(int value = 1; value <= this.tailleGrille; value++){
                        if (isValid(row, col, value)){
                            grille[row][col] = value;
                            if (backtracking()){
                                return true;
                            } else {
                                grille[row][col] = 0;
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

    private boolean isValid(int row, int col, int value){
        for(int i=0; i< this.tailleGrille; i++){
            if (grille[row][i] == value || grille[i][col] == value){
                return false;
            }
        }

        // Vérifier la sous-grille
        int startRow = row - row % tailleSousGrille;
        int startCol = col - col % tailleSousGrille;
        for (int i = 0; i < tailleSousGrille; i++) {
            for (int j = 0; j < tailleSousGrille; j++) {
                if (grille[startRow + i][startCol + j] == value) {
                    return false;
                }
            }
        }

        return true; // Le placement est sûr
    }

    private int[][] compteContraintes(){
        int[][] contraintes = new int[tailleGrille][tailleGrille];
        for(int i = 0; i < this.tailleGrille; i++){
            for(int j = 0; j < this.tailleGrille; j++){
                if(grille[i][j] == 0){
                    // parcours la ligne de compte les contraintes
                    for(int k = 0; k < this.tailleGrille; k++){
                        contraintes[i][j] += grille[i][k] == 0 ? 0 : 1;
                    }
                    //parcours la colonne de compte les contraintes
                    for(int k = 0; k < this.tailleGrille; k++){
                        contraintes[i][j] += grille[k][j] == 0 ? 0 : 1;
                    }
                    //parcours la sous-grille de compte les contraintes
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

    public boolean gloutonPlusContraint(){
        int[][] contraintes = compteContraintes();
        
        //sort the cells by the number of constraints
        List<int[]> cells = new ArrayList<>();
        for(int i = 0; i < this.tailleGrille; i++){
            for(int j = 0; j < this.tailleGrille; j++){
                if(grille[i][j] == 0){
                    cells.add(new int[]{i, j, contraintes[i][j]});
                }
            }
        }
        //trier du plus grand nombre de contraintes au plus petit
        cells.sort((a, b) -> Integer.compare(b[2], a[2]));

        for(int[] cell : cells){
            int row = cell[0];
            int col = cell[1];
            if (grille[row][col] == 0){
                for(int value = 1; value <= this.tailleGrille; value++){
                    if (isValid(row, col, value)){
                        grille[row][col] = value;
                        if (gloutonPlusContraint()){
                            return true;
                        } else {
                            grille[row][col] = 0;
                        }
                    }
                }
                return false;
            }
        }


        afficheGrille(grille);
        return true;
    }

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
                    System.out.print(String.format("%2d ", grille[i][j]));
                }
            }
            //vertical border
            System.out.println("|");
        }
    
        // line of separation
        System.out.println(separator);
    }}
