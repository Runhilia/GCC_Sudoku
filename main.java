import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;


public class main {
    public static void main(String[] args) {
        int n = 16;
        int subgridSize = 4;

        Model model = new Model("16x16 Sudoku Solver");

        // Create variables
        IntVar[][] grid = new IntVar[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                grid[i][j] = model.intVar("cell_" + i + "_" + j, 1, n);
            }
        }

        // Row constraints
        for (int i = 0; i < n; i++) {
            model.allDifferent(grid[i]).post();
        }

        // Column constraints
        for (int j = 0; j < n; j++) {
            IntVar[] column = new IntVar[n];
            for (int i = 0; i < n; i++) {
                column[i] = grid[i][j];
            }
            model.allDifferent(column).post();
        }

        // Subgrid constraints
        for (int row = 0; row < n; row += subgridSize) {
            for (int col = 0; col < n; col += subgridSize) {
                IntVar[] subgrid = new IntVar[n];
                int index = 0;
                for (int i = 0; i < subgridSize; i++) {
                    for (int j = 0; j < subgridSize; j++) {
                        subgrid[index++] = grid[row + i][col + j];
                    }
                }
                model.allDifferent(subgrid).post();
            }
        }

        // Example initial grid (replace 0 with given values)
        int[][] initialGrid = new int[][] {
                {  4,  7,  0,  0, 15,  8,  0,  0, 16,  0,  0, 12,  0,  0, 14,  8 },
                {  0,  7, 10,  0,  1,  0,  0, 15,  3,  0,  0,  0,  0, 11,  9,  5 },
                {  2,  0,  0,  0,  0,  3,  0, 12,  9,  0,  0,  0,  0,  0,  0,  4 },
                {  0,  7,  0, 11,  0,  0,  0, 12,  6,  0,  0,  0,  9,  0,  8,  1 },

                {  7,  4,  0, 11, 15, 14,  0,  0,  4,  0, 15, 13,  3,  1,  0,  8 },
                {  0,  0,  6,  0,  4, 12,  3,  8,  0,  0, 14,  2,  0,  5,  0,  3 },
                {  0,  0,  0,  0,  2, 13,  0, 14,  0,  0,  7,  0, 15,  0,  0, 16 },
                {  7,  0,  0,  0,  0,  0, 15,  0,  0,  0,  0,  0,  0,  9, 11,  0 },

                {  8,  0, 14,  0,  0,  3,  5,  2, 14,  0,  0,  0,  0,  8,  0,  0 },
                { 13,  0,  0,  0,  0,  0,  8,  0,  0,  0,  0,  4, 10,  0, 12,  5 },
                {  0,  0,  5,  4,  0,  0,  0, 13,  0,  7,  0,  0,  0,  0, 12,  0 },
                {  0, 13,  0,  0,  6,  0,  0,  0, 14,  9, 15,  0,  0,  3, 14, 15 },
                
                { 10,  0,  0, 13,  5,  3,  0,  0, 14, 16,  0,  0,  0,  8,  0,  4 },
                {  0,  8,  0,  0,  0, 14,  0,  0, 12,  0,  0, 16, 10,  0,  6, 15 },
                {  0,  5,  4,  0,  0,  0,  0,  0, 13,  7,  0,  0, 12,  0,  0,  0 }
        };

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (initialGrid[i][j] > 0) {
                    model.arithm(grid[i][j], "=", initialGrid[i][j]).post();
                }
            }
        }

        // Solve the problem
        if (model.getSolver().solve()) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    System.out.print(grid[i][j].getValue() + " ");
                }
                System.out.println();
            }
        } else {
            System.out.println("No solution found.");
        }
    }
}
