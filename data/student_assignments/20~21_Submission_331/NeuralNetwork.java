import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * Neural Network of BreedableActor used to calculate desires, able to generate predictions from inputs
 * and train to minimise error between predictions and target outputs.
 *
 * @version 2021.03.01
 * @see BreedableActor
 * @see <a href="https://towardsdatascience.com/understanding-and-implementing-neural-networks-in-java-from-scratch-61421bb6352c">
 *     https://towardsdatascience.com/understanding-and-implementing-neural-networks-in-java-from-scratch-61421bb6352c
 * </a>
 */
public class NeuralNetwork {
    // Layers of neural network.
    private final Layer[] layers;

    private final Random rand = Randomizer.getRandom();

    // Controls how much each weight and bias mutates.
    private final double MUTATION_AMOUNT = 0.05;

    /**
     * Create Neural Network from lengths of nodes in each layer.
     *
     * @param layersNodes varArgs of layerNodes, defining how many nodes are in each layer.
     */
    public NeuralNetwork(int... layersNodes) {
        layers = new Layer[layersNodes.length-1];

        for (int i = 1; i < layersNodes.length; i++) {
            layers[i-1] = new Layer();

            // If not ending layer
            layers[i-1].setWeights(Matrix.Random(layersNodes[i], layersNodes[i-1]));
            layers[i-1].setBias(Matrix.Random(layersNodes[i], 1));
        }
    }

    /**
     * Create Neural Network from array of layers.
     *
     * @param layers layers to add to neural network.
     */
    public NeuralNetwork(Layer[] layers) {
        this.layers = layers;
    }

    /**
     * For each weight and bias of each layer, randomly select between this and parameter b.
     *
     * @param b neural network to crossover.
     * @return merged neural network.
     */
    public NeuralNetwork crossover(NeuralNetwork b) {
        Layer[] aLayers = getLayers();
        Layer[] bLayers = b.getLayers();
        Layer[] newLayers = new Layer[aLayers.length];

        // For each layer:
        for (int layerIndex = 0; layerIndex < aLayers.length; layerIndex++) {
            Layer currentLayer = new Layer();
            Layer aCurrentLayer = aLayers[layerIndex];
            Layer bCurrentLayer = bLayers[layerIndex];

            Matrix aCurrentLayerWeights = aCurrentLayer.getWeights();
            Matrix bCurrentLayerWeights = bCurrentLayer.getWeights();

            // Set each layer weight to either a's or b's weight.
            Matrix currentLayerWeights = new Matrix(aCurrentLayerWeights.getX(), aCurrentLayerWeights.getY());
            for (int weightsIndex = 0; weightsIndex < currentLayerWeights.getX(); weightsIndex++) {
                if (rand.nextBoolean()) {
                    currentLayerWeights.setValues(weightsIndex, aCurrentLayerWeights.getValues(weightsIndex));
                } else {
                    currentLayerWeights.setValues(weightsIndex, bCurrentLayerWeights.getValues(weightsIndex));
                }
            }
            currentLayer.setWeights(currentLayerWeights);

            // Set layer bias to either a's or b's bias.
            Matrix currentLayerBias = rand.nextBoolean() ? aCurrentLayer.getBias() : bCurrentLayer.getBias();
            currentLayer.setBias(currentLayerBias);

            // Set layer in position.
            newLayers[layerIndex] = currentLayer;
        }

        // Create new NeuralNetwork from newLayers.
        return new NeuralNetwork(newLayers);
    }

    /**
     * Manipulate each weight and bias by a random amount multiplied by MUTATION_AMOUNT.
     */
    public void mutate() {
        for (Layer layer: layers) {
            Callable<Double> mutator = () -> MUTATION_AMOUNT * (1 - (2 * rand.nextDouble()));
            Matrix weights = layer.getWeights();
            weights.mutateValues(mutator);

            Matrix bias = layer.getBias();
            bias.mutateValues(mutator);
        }
    }

    /**
     * Get the layers of the neural network in an array.
     *
     * @return neural network layers.
     */
    public Layer[] getLayers() {
        return layers;
    }

    /**
     * Generate neural network prediction from inputs.
     *
     * @param input to generate prediction from.
     * @return prediction of neural network.
     */
    public List<Double> predict(double[] input) {
        Matrix workingMatrix = Matrix.FromArray(input);
        for (Layer layer: layers) {
            if (layer.getWeights() != null) {
                workingMatrix = Matrix.multiply(layer.getWeights(), workingMatrix);
                workingMatrix.add(layer.getBias());
                workingMatrix.sigmoid();
            }
        }
        return workingMatrix.toArray();
    }

    /**
     * Manipulate layer weights and bias to minimise error between neural network output
     * and Y (target outputs).
     *
     * @param X inputs.
     * @param Y target outputs.
     */
    public void train(double[] X, double[] Y)
    {
        // Get model prediction
        Matrix workingMatrix = Matrix.FromArray(X);
        List<Matrix> layersMatrix = new ArrayList<>();
        for (Layer layer: layers) {
            if (layer.getWeights() != null) {
                workingMatrix = Matrix.multiply(layer.getWeights(), workingMatrix);
                workingMatrix.add(layer.getBias());
                workingMatrix.sigmoid();
                layersMatrix.add(Matrix.Copy(workingMatrix));
            }
        }

        Matrix target = Matrix.FromArray(Y);
        Matrix output = layersMatrix.get(layersMatrix.size()-1);

        Matrix error = null;

        // For each layer excluding inputs:
        for (int i = layers.length-1; i > 0; i--) {
            Layer currentLayer = layers[i];

            if (error == null) {
                error = Matrix.subtract(target, output);
            } else {
                Matrix weightsTransposed = Matrix.transpose(currentLayer.getWeights());
                error = Matrix.multiply(weightsTransposed, error);
            }

            Matrix currentLayerOutput = layersMatrix.get(i);

            // Calculate error gradient.
            Matrix gradient = Matrix.dsigmoid(currentLayerOutput);
            gradient.multiply(error);
            // Controls how much the learning gradient manipulates the layer outputs.
            double LRATE = 0.1;
            gradient.multiply(LRATE);

            // Calculate difference in weights and desired weights.
            Matrix previousLayerOutput = layersMatrix.get(i-1);
            Matrix previousLayerOutputTransposed = Matrix.transpose(previousLayerOutput);
            Matrix weightsDelta = Matrix.multiply(gradient, previousLayerOutputTransposed);

            currentLayer.getWeights().add(weightsDelta);
            currentLayer.getBias().add(gradient);
        }
    }

    /**
     * Layer of a neural network with weights and bias.
     */
    public class Layer {
        // Weights connecting current layer to next layer.
        Matrix weights;
        // Bias of current layer.
        Matrix bias;

        /**
         * Create Layer.
         */
        Layer() {}

        /**
         * Sets the weights of the layer.
         *
         * @param weights weights to set.
         */
        public void setWeights(Matrix weights) {
            this.weights = weights;
        }

        /**
         * Sets the bias of the layer.
         *
         * @param bias bias to set.
         */
        public void setBias(Matrix bias) {
            this.bias = bias;
        }

        /**
         * Gets the weights of the layer.
         *
         * @return weights of layer.
         */
        public Matrix getWeights() {
            return weights;
        }

        /**
         * Gets the bias of the layer.
         *
         * @return bias of layer.
         */
        public Matrix getBias() {
            return bias;
        }
    }
}
