package src;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Récupère la taille de la grille donnée dans la ligne de commande
        Scanner scanner = new Scanner(System.in);
        System.out.print("Entrez la taille de la sous-grille: ");
        int subgridSize = scanner.nextInt();

        ResolveurSudoku resolveur = new ResolveurSudoku(subgridSize);

        System.out.println("Quelle méthode souhaitez-vous utiliser pour résoudre le sudoku ?");
        System.out.println("1. Résolution classique avec Choco Solver");
        System.out.println("2. Résolution avec backtracking");
        System.out.println("3. Résolution avec la recherche locale");
        System.out.println("4. Résolution avec l'heuristique globale");
        int choix = scanner.nextInt();
        long tempsDepart = System.currentTimeMillis();

        switch (choix) {
            case 1:
                resolveur.resolutionChoco();
                break;
            case 2:
                resolveur.backtracking();
                break;
            case 3:
                resolveur.rechercheLocale();
                break;
            case 4:
                resolveur.gloutonPlusContraint();
                break;
            default:
                System.out.println("Choix invalide");
                break;
        }

        long tempsFin = System.currentTimeMillis();
        System.out.println("Temps de résolution: " + (tempsFin - tempsDepart) + " ms");
    }
}
