package src;

import java.util.Random;

public class GenerateurGrilleValide {

    private int tailleGrille;
    private int[][] grille;

    public GenerateurGrilleValide(int tailleGrille) {
        this.tailleGrille = tailleGrille;
        this.grille = new int[tailleGrille][tailleGrille];
        remplirGrille(0, 0); 
    }

    // backtracking
    private boolean remplirGrille(int i, int j) {
        if (i == this.tailleGrille) {
            return true;
        }
        
        int nextI = (j == this.tailleGrille - 1) ? i + 1 : i;
        int nextJ = (j + 1) % this.tailleGrille;
        
        if (this.grille[i][j] != 0) {
            return remplirGrille(nextI, nextJ);
        }
        
        for (int num = 1; num <= this.tailleGrille; num++) {
            if (respecteContraintes(i, j, num)) {
                this.grille[i][j] = num;
                
                if (remplirGrille(nextI, nextJ)) {
                    return true;
                }
                
                this.grille[i][j] = 0;
            }
        }
        
        return false;
    }

    private boolean respecteContraintes(int i, int j, int valeur) {
        for (int k = 0; k < this.tailleGrille; k++) {
            if (this.grille[i][k] == valeur || this.grille[k][j] == valeur) {
                return false;
            }
        }
        
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

    public void enleverElements(int nombreDeCases) {
        Random random = new Random();
        int casesEnlevees = 1;
        
        while (casesEnlevees < nombreDeCases) {
            int i = random.nextInt(this.tailleGrille);
            int j = random.nextInt(this.tailleGrille);
            
            if (this.grille[i][j] != 0) {
                this.grille[i][j] = 0;
                casesEnlevees++;
            }
        }
    }

    public int[][] getGrille() {
        return this.grille;
    }
}   
