package src.Generateurs;

import java.util.*;

public class GenerateurSolutionFausse {

    private int tailleGrille;
    private int[][] grille;
    private final ArrayList<int[]> listeCasesModifiables = new ArrayList<>();

    /**
     * Constructeur d'une solution fausse pour la recherche locale
     * @param tailleGrille la taille de la grille souhaitée
     */
    public GenerateurSolutionFausse(int tailleGrille) {
        this.tailleGrille = tailleGrille;
        this.grille = new int[tailleGrille][tailleGrille];
        // On génère une grille valide
        GenerateurGrilleValide generateurGrilleValide = new GenerateurGrilleValide(tailleGrille);
        this.grille = generateurGrilleValide.getGrille();
        // On ajoute des valeurs aléatoires dans la grille pour constituer une solution fausse
        this.remplirCaseAleatoire();
    }

    /**
     * Rempli chaque ligne de la grille avec des valeurs au hasard
     */
    private void remplirCaseAleatoire() {
        for (int ligne = 0; ligne < this.tailleGrille; ligne++) {
            for (int colonne = 0; colonne < this.tailleGrille; colonne++) {
                if (this.grille[ligne][colonne] == 0) {
                    this.listeCasesModifiables.add(new int[]{ligne, colonne});
                    Random random = new Random();
                    this.grille[ligne][colonne] = random.nextInt(this.tailleGrille) + 1;
                }
            }
        }
    }


    /**
     * Récupère la grille générée
     * @return la grille générée
     */
    public int[][] getGrille() {
        return this.grille;
    }

    public ArrayList<int[]> getListeCasesModifiables() {
        return this.listeCasesModifiables;
    }
}
