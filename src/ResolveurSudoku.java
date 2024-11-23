package src;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class ResolveurSudoku {

    private final int tailleSousGrille;
    private final int tailleGrille;
    private final Model model;

    /**
     * Constructeur d'un résolveur de sudoku
     * @param tailleSousGrille la taille de la sous-grille
     */
    public ResolveurSudoku(int tailleSousGrille) {
        this.tailleSousGrille = tailleSousGrille; // Taille de la sous-grille
        this.tailleGrille = tailleSousGrille * tailleSousGrille; // Taille de la grille
        this.model = new Model("Sudoku"); // Crée le modèle
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

        // Résout le problème et affiche la solution si elle existe
        if (model.getSolver().solve()) {
            int[][] grilleFinale = new int[this.tailleGrille][this.tailleGrille];
            for (int i = 0; i < this.tailleGrille; i++) {
                for (int j = 0; j < this.tailleGrille; j++) {
                    grilleFinale[i][j] = grille[i][j].getValue();
                }
            }
            afficheGrille(grilleFinale);
        } else {
            System.out.println("Pas de solution");
        }
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

    /**
     * Affichage de la grille
     * @param grille la grille à afficher
     */
    private void afficheGrille(int[][] grille) {
        for (int i = 0; i < this.tailleGrille; i++) {
            for (int j = 0; j < this.tailleGrille; j++) {
                System.out.print(grille[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
