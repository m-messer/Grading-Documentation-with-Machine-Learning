import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * The Matrix is a data structure used within the
 * NeuralNetwork class to calculate desires for BreedableActors.
 * It is implemented through a 2D array of doubles
 *
 * @see NeuralNetwork
 * @see BreedableActor
 * @see <a href="https://towardsdatascience.com/understanding-and-implementing-neural-networks-in-java-from-scratch-61421bb6352c">
 *     https://towardsdatascience.com/understanding-and-implementing-neural-networks-in-java-from-scratch-61421bb6352c
 * </a>
 * @version 2021.03.01
 */
public class Matrix {
    private double[][] matrix;
    private int X; // Rows.
    private int Y; // Columns.

    /**
     * Create an empty matrix.
     *
     * @param X width of matrix.
     * @param Y height of matrix.
     */
    public Matrix(int X, int Y) {
        this.X = X;
        this.Y = Y;
        matrix = new double[X][Y];
    }

    /**
     * Create a matrix and set values to the results of calls to valueFunc.
     *
     * @param X width of matrix.
     * @param Y height of matrix.
     * @param valueFunc function which returns a double.
     */
    public Matrix(int X, int Y, Callable<Double> valueFunc) {
        this.X = X;
        this.Y = Y;
        matrix = new double[X][Y];
        for (int x = 0; x < X; x++) {
            for (int y = 0; y < Y; y++) {
                try {
                    setValue(x, y, valueFunc.call());
                } catch (Exception ignored) {
                }
            }
        }
    }

    /**
     * Set the value of an element in the matrix.
     *
     * @param x index in matrix.
     * @param y index in matrix.
     * @param value value to set element as.
     */
    public void setValue(int x, int y, double value) {
        matrix[x][y] = value;
    }

    /**
     * Set an array of values in the matrix.
     *
     * @param x index in matrix.
     * @param values array of values to set.
     */
    public void setValues(int x, double[] values) {
        matrix[x] = values;
    }

    /**
     * Get the value of an element in the matrix.
     *
     * @param x index in matrix.
     * @param y index in matrix.
     * @return value indexed by x & y in the matrix.
     */
    public double getValue(int x, int y) {
        return matrix[x][y];
    }

    /**
     * Get an array of values in the matrix.
     *
     * @param x index in matrix.
     * @return values indexed by x in the matrix.
     */
    public double[] getValues(int x) {
        return matrix[x];
    }

    /**
     * Add result of mutator function param on each value in the matrix.
     *
     * @param mutator function which returns a double.
     */
    public void mutateValues(Callable<Double> mutator) {
        for (int i = 0; i < getX(); i++) {
            for (int j = 0; j < getY(); j++) {
                try {
                    setValue(i, j, getValue(i, j) + mutator.call());
                } catch (Exception ignored) {

                }
            }
        }
    }

    /**
     * Get rows of the matrix.
     *
     * @return rows of matrix.
     */
    public int getX() {
        return X;
    }

    /**
     * Get columns of the matrix.
     *
     * @return columns of matrix.
     */
    public int getY() {
        return Y;
    }

    /**
     * Initialise new Matrix with random values.
     *
     * @param X width of matrix.
     * @param Y height of matrix.
     * @return new random Matrix.
     */
    public static Matrix Random(int X, int Y) {
        Random rand = Randomizer.getRandom();
        return new Matrix(X, Y, () -> rand.nextDouble() * 2.0 - 1.0);
    }

    /**
     * Initialise new Matrix with values of 0.0.
     *
     * @param X width of matrix.
     * @param Y height of matrix.
     * @return new empty Matrix.
     */
    public static Matrix Empty(int X, int Y) {
        return new Matrix(X, Y, () -> 0.0);
    }

    /**
     * Initialise new Matrix with values from matrixToCopy.
     *
     * @param matrixToCopy Matrix to retrieve values from.
     * @return new copied Matrix.
     */
    public static Matrix Copy(Matrix matrixToCopy) {
        Matrix newMatrix = Empty(matrixToCopy.getX(), matrixToCopy.getY());
        for (int i = 0; i < matrixToCopy.getX(); i++) {
            for (int j = 0; j < matrixToCopy.getY(); j++) {
                newMatrix.setValue(i, j, matrixToCopy.getValue(i, j));
            }
        }
        return newMatrix;
    }

    /**
     * Create Matrix from an array x.
     *
     * @param x array to create matrix from.
     * @return new Matrix from array.
     */
    public static Matrix FromArray(double[] x) {
        Matrix output = new Matrix(x.length, 1);
        for (int i = 0; i < x.length; i++) {
            output.setValue(i, 0, x[i]);
        }
        return output;
    }

    /**
     * Multiply two matrices together.
     *
     * @param a matrix to multiply.
     * @param b matrix to multiply.
     * @return new Matrix of the matrix product.
     */
    public static Matrix multiply(Matrix a, Matrix b) {
        // Set the new matrix to have the number of rows as a and number of columns as b.
        Matrix output = Empty(a.getX(), b.getY());

        for (int i=0; i < output.getX(); i++)
        {
            for (int j=0; j < output.getY(); j++)
            {
                double sum = 0;
                for (int k=0 ; k < b.getX(); k++)
                {
                    sum += a.getValue(i, k) * b.getValue(k, j);
                }
                output.setValue(i, j, sum);
            }
        }
        return output;
    }

    /**
     * Multiply this matrix by b.
     *
     * @param b matrix to multiply.
     */
    public void multiply(Matrix b) {
        for (int i = 0; i < getX(); i++)
        {
            for (int j = 0; j < b.getY(); j++)
            {
                double sum = 0;
                for (int k = 0 ; k < getY(); k++)
                {
                    sum += getValue(i, k) * b.getValue(k, j);
                }
                setValue(i, j, sum);
            }
        }
    }

    /**
     * Multiply this matrix by a single value.
     *
     * @param a single value to multiply.
     */
    public void multiply(double a) {
        for (int i = 0; i < getX(); i++)
        {
            for (int j = 0; j < getY(); j++)
            {
                setValue(i, j, getValue(i, j)*a);
            }
        }
    }

    /**
     * Add this matrix by a.
     *
     * @param a matrix to add.
     * @throws IllegalArgumentException if a is not the same shape as this.
     */
    public void add(Matrix a) {
        if(getX() != a.getX() || getY() != a.getY()) {
            throw new IllegalArgumentException("Shape Mismatch");
        }

        for (int i = 0; i < getX(); i++) {
            for (int j = 0; j < getY(); j++) {
                setValue(i, j, getValue(i, j)+a.getValue(i, j));
            }
        }
    }

    /**
     * Subtract this matrix by a.
     *
     * @param a matrix to subtract.
     * @throws IllegalArgumentException if a is not the same shape as this.
     */
    public void subtract(Matrix a) {
        if(getX() != a.getX() || getY() != a.getY()) {
            throw new IllegalArgumentException("Shape Mismatch");
        }

        for (int i = 0; i < getX(); i++) {
            for (int j = 0; j < getY(); j++) {
                setValue(i, j, getValue(i, j)-a.getValue(i, j));
            }
        }
    }

    /**
     * Transpose(/flip) this matrix.
     */
    public void transpose() {
        Matrix temp = new Matrix(getY(), getX());
        for(int i = 0; i < getX(); i++)
        {
            for(int j = 0; j < getY(); j++)
            {
                temp.setValue(j, i, getValue(i, j));
            }
        }

        X = temp.getX();
        Y = temp.getY();
        matrix = temp.matrix;
    }

    /**
     * Apply the sigmoid function to each value in this matrix.
     */
    public void sigmoid() {
        for (int i = 0; i < getX(); i++) {
            for (int j = 0; j < getY(); j++) {
                setValue(i, j, 1/(1+Math.exp(-getValue(i, j))));
            }
        }
    }

    /**
     * Apply the derivative of the sigmoid function to each value in this matrix.
     */
    public void dsigmoid() {
        for (int i = 0; i < getX(); i++) {
            for (int j = 0; j < getY(); j++) {
                setValue(i, j, getValue(i, j) * (1-getValue(i, j)));
            }
        }
    }

    /**
     * Add two matrices together.
     *
     * @param a to add.
     * @param b to add.
     * @return result matrix of addition.
     */
    public static Matrix add(Matrix a, Matrix b) {
        Matrix tempA = Matrix.Copy(a);
        tempA.add(b);
        return tempA;
    }

    /**
     * Subtract matrix a by matrix b.
     *
     * @param a initial matrix.
     * @param b matrix to subtract.
     * @return result matrix of subtraction.
     */
    public static Matrix subtract(Matrix a, Matrix b) {
        Matrix tempA = Matrix.Copy(a);
        tempA.subtract(b);
        return tempA;
    }

    /**
     * Apply the derivative of the sigmoid function to each value in the matrix.
     *
     * @param a matrix to apply function to.
     * @return result matrix of function.
     */
    public static Matrix dsigmoid(Matrix a) {
        Matrix tempA = Matrix.Copy(a);
        tempA.dsigmoid();
        return tempA;
    }

    /**
     * Transpose(/flip) the matrix.
     *
     * @param a matrix to transpose.
     * @return transposed matrix.
     */
    public static Matrix transpose(Matrix a) {
        Matrix tempA = Matrix.Copy(a);
        tempA.transpose();
        return tempA;
    }

    /**
     * Flatten matrix into 1D list.
     *
     * @return flattened matrix as list.
     */
    public List<Double> toArray() {
        List<Double> output = new ArrayList<>();
        for (int i = 0; i < getX(); i++) {
            for (int j = 0; j < getY(); j++) {
                output.add(getValue(i, j));
            }
        }
        return output;
    }
}
