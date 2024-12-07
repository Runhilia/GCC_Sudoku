package src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class GenerateurGrilleValide {

    private int tailleGrille;
    private int[][] grille;

    public GenerateurGrilleValide(int tailleGrille) {
        this.tailleGrille = tailleGrille;
        this.grille = new int[tailleGrille][tailleGrille];
        this.remplirGrille(0, 0);
        this.enleverElements(tailleGrille * tailleGrille / 4);
    }

    private boolean remplirGrille(int i, int j) {
        if (i == this.tailleGrille) {
            return true;
        }
        
        int nextI = (j == this.tailleGrille - 1) ? i + 1 : i;
        int nextJ = (j + 1) % this.tailleGrille;
        
        List<Integer> valeurs = genererValAleatoire();
        
        for(int num : valeurs) {
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

    private List<Integer> genererValAleatoire() {
        List<Integer> valeurs = new ArrayList<>();
        for (int i = 1; i <= this.tailleGrille; i++) {
            valeurs.add(i);
        }
        
        Collections.shuffle(valeurs);
        return valeurs;
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
        int casesEnlevees = 0;
        
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
