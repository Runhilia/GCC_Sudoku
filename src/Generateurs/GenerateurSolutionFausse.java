package src.Generateurs;

import org.chocosolver.solver.variables.IntVar;

import java.util.*;

public class GenerateurSolutionFausse {

    private int tailleGrille;
    private int[][] grille;
    private ArrayList<Integer> valeursPossibles = new ArrayList<>();

    /**
     * Constructeur d'une solution fausse pour la recherche locale
     * @param tailleGrille la taille de la grille souhaitée
     */
    public GenerateurSolutionFausse(int tailleGrille) {
        this.tailleGrille = tailleGrille;
        this.grille = new int[tailleGrille][tailleGrille];
        this.remplirCaseAleatoire();
    }

    /**
     * Rempli chaque ligne de la grille avec des valeurs au hasard
     */
    private void remplirCaseAleatoire() {
        for (int ligne = 0; ligne < this.tailleGrille; ligne++) {
            this.genererListeValeursPossibles(); // On récupère dans une liste les valeurs possibles pour chaque case
            for (int colonne = 0; colonne < this.tailleGrille; colonne++) {
                this.tirerValeurAleatoire(ligne, colonne, this.valeursPossibles); // On tire une valeur aléatoire parmi les valeurs possibles
            }
        }
    }

    /**
     * Génère la liste des valeurs possibles pour chaque case
     */
    private void genererListeValeursPossibles() {
        this.valeursPossibles = new ArrayList<>();
        for (int i = 1; i <= this.tailleGrille; i++) {
            this.valeursPossibles.add(i);
        }
    }

    /**
     * Tire une valeur aléatoire parmi les valeurs possibles
     * @param ligne la ligne
     * @param colonne la colonne
     * @param valeursPossibles les valeurs possibles
     */
    private void tirerValeurAleatoire(int ligne, int colonne, ArrayList<Integer> valeursPossibles) {
        Random random = new Random();
        int index = random.nextInt(valeursPossibles.size());
        this.grille[ligne][colonne] = valeursPossibles.get(index);
        valeursPossibles.remove(index);
    }


    /**
     * Récupère la grille générée
     * @return la grille générée
     */
    public int[][] getGrille() {
        return this.grille;
    }
}
