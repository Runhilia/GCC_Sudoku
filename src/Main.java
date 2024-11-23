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
        long tempsDepart = System.currentTimeMillis();
        resolveur.resolution();
        long tempsFin = System.currentTimeMillis();
        System.out.println("Temps de résolution: " + (tempsFin - tempsDepart) + " m4s");
    }
}
