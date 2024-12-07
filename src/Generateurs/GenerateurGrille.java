package src.Generateurs;

import java.util.Random;

public class GenerateurGrille {

    private int tailleGrille;
    private int[][] grille;

    /**
     * Constructeur d'une grille d'une taille donnée
     * @param tailleGrille la taille de la grille souhaitée
     */
    public GenerateurGrille(int tailleGrille) {
        this.tailleGrille = tailleGrille;
        this.grille = new int[tailleGrille][tailleGrille];
        this.remplirCaseAleatoire();
    }

    /**
     * Choisi aléatoirement des cases de la grille à remplir
     */
    private void remplirCaseAleatoire() {
        Random random = new Random();
        for (int i = 0; i < this.tailleGrille; i++) {
            for (int j = 0; j < this.tailleGrille; j++) {
                // On remplit une case sur 4 en respectant les contraintes
                double valeurAleatoire = random.nextDouble();
                if (valeurAleatoire < 0.25) {
                    this.grille[i][j] = this.tireNombreAleatoire(i, j);
                }
            }
        }
    }

    /**
     * Tire un nombre aléatoire en respectant les contraintes
     * @param i indice de la ligne
     * @param j indice de la colonne
     * @return la valeur de la case (i, j)
     */
    private int tireNombreAleatoire(int i, int j) {
        Random random = new Random();
        int valeurAleatoire = random.nextInt(this.tailleGrille) + 1;

        // On vérifie que la valeur respecte les contraintes
        if (this.respecteContraintes(i, j, valeurAleatoire)) {
            return valeurAleatoire;
        } else {
            return this.tireNombreAleatoire(i, j);
        }
    }

    /**
     * Vérifie si une valeur respecte les contraintes
     * @param i indice de la ligne
     * @param j indice de la colonne
     * @param valeur la valeur à tester
     * @return true si la valeur respecte les contraintes, false sinon
     */
    private boolean respecteContraintes(int i, int j, int valeur) {
        // On vérifie que la valeur n'est pas déjà présente dans la ligne
        for (int k = 0; k < this.tailleGrille; k++) {
            if (this.grille[i][k] == valeur) {
                return false;
            }
        }

        // On vérifie que la valeur n'est pas déjà présente dans la colonne
        for (int k = 0; k < this.tailleGrille; k++) {
            if (this.grille[k][j] == valeur) {
                return false;
            }
        }

        // On vérifie que la valeur n'est pas déjà présente dans la sous-grille
        int sousGrilleTaille = (int) Math.sqrt(this.tailleGrille);
        int debutLigne = i - i % sousGrilleTaille;
        int debutColonne = j - j % sousGrilleTaille;
        for (int k = 0; k < sousGrilleTaille; k++) {
            for (int l = 0; l < sousGrilleTaille; l++) {
                if (this.grille[debutLigne + k][debutColonne + l] == valeur) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Récupère la grille générée
     * @return la grille générée
     */
    public int[][] getGrille() {
        return this.grille;
    }
}
