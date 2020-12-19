import Jama.Matrix;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        int[][] chutes_ladders = {{2,8},{6,3}};
        int[][] actual_chutes_ladders = {{1, 38}, {4, 14}, {9, 31}, {16, 6}, {21, 42}, {28, 84}, {36, 44}, {47, 26}, {49, 11},
                {51, 67}, {56, 53}, {62, 19}, {64, 60}, {71, 91}, {80, 100}, {87, 24}, {93, 73}, {95, 75},{98, 78}};


        for (int i = 2; i < 9; i++) {
            System.out.println(finalCalculation(9,i,chutes_ladders));
        }

        System.out.println(" ");

        for (int i = 2; i < 101; i++) {
            System.out.println(i + ":" + finalCalculation(100,i,actual_chutes_ladders));

        }
    }


    private static double[][] initialMatrix (int board, int dice) {
        int size = board + 1;
        int count = dice - 1;
        double probability = (double) 1/dice;
        double[][] matrix = new double[size][size];

        for (int i = 0; i < size - dice; i++) {
            for (int j = 0; j < size; j++) {
                if(i+1 <= j && j <= i+dice) { // for i(i,i+j) entries
                    matrix[i][j] = probability; // calculate probability
                } else {
                    matrix[i][j] = 0; // fill with zeros for remaining
                }
            }
        }

        for (int i = size - dice; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if(i == j) {
                    double x = (double)count/ (dice);
                    matrix[i][j] = 1 - x; // calculate probability for entry (i,i)
                } else if (j > i) {
                    matrix[i][j] = probability; // calculate probability for (i,i+j)
                } else {
                    matrix[i][j] = 0; // fill with zeros for remaining
                }
            }
            count--; // Adjust probability regarding the location
        }
        return matrix;

    }

    private static double[][] finalMatrix(int[][]changes,int sides, int board) {
        double[][] matrix = initialMatrix(board,sides); // Create initial matrix
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < changes.length; j++) {
                matrix[i][changes[j][1]] = matrix[i][changes[j][1]] + matrix[i][changes[j][0]]; // Add up probability of landing on i
                matrix[i][changes[j][0]] = 0; // Fill row i with zeros

            }
        }
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < changes.length; j++) {
                matrix[changes[j][0]][i] = 0; // Fill column i with zeros
            }
        }
        return matrix;
    }

    private static double[][] reducedMatrix(double[][] arrays) {
        double[][] newMatrix = new double[arrays.length-1][arrays.length-1];
        for (int i = 0; i < newMatrix.length; i++) {
            for (int j = 0; j < newMatrix[i].length; j++) {
                newMatrix[i][j] = arrays[i][j];
            }

        }
        return newMatrix;
    }

    private static Matrix transientMatrix(double[][] arrays) {
        Matrix transientMatrix = new Matrix(reducedMatrix(arrays));
        return transientMatrix;
    }

    private static Matrix identityMatrix(int size) {
        double[][] matrix = new double[size][size];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (i == j) {
                    matrix[i][j] = 1;
                } else {
                    matrix[i][j] = 0;
                }

            }

        }
        Matrix identityMatrix = new Matrix(matrix);
        return identityMatrix;
    }

    private static Matrix fundamentalMatrix(Matrix transientMatrix, Matrix identityMatrix) {
        Matrix subtractedMatrix = identityMatrix.minus(transientMatrix);
        Matrix fundamentalMatrix = subtractedMatrix.inverse();
        return fundamentalMatrix;
    }

    private static Matrix vector(int size) {
        double[][] vector = new double[size][1];
        for (int i = 0; i < vector.length; i++) {
            vector[i][0] = 1;
        }
        Matrix vectorMatrix = new Matrix(vector);
        return vectorMatrix;

    }

    private static double getExpectedTurns(double[][] arrays) {
        return arrays[0][0];
    }

    private static double finalCalculation(int board, int sides, int[][] chutes_ladders) {
        double[][] finalMatrix = finalMatrix(chutes_ladders,sides,board); // Create  transition matrix P
        Matrix transientMatrix = transientMatrix(finalMatrix); // Create submatrix Q
        Matrix identityMatrix = identityMatrix(board); // Create identity matrix
        Matrix fundamentalMatrix = fundamentalMatrix(transientMatrix,identityMatrix); // Calculate fundamental matrix
        Matrix vector = vector(board); // Create column vector
        Matrix resultMatrix = fundamentalMatrix.times(vector); // Multiply column vector with fundamental matrix
        return getExpectedTurns(resultMatrix.getArray()); // Get the first row of the calculated matrix
    }



    public static void print2D(double[][] mat)
    {
        for (double[] row : mat)
            System.out.println(Arrays.toString(row));
    }




}


