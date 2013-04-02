

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

import Net.ReadSolutions;
import Net.Connection;
import Net.Neuron;

public class NeuralNetwork {
	static {
		Locale.setDefault(Locale.ENGLISH);
	}

	final boolean isTrained = false;
	final DecimalFormat df;
	final Random rand = new Random();
	final ArrayList<Neuron> inputLayer = new ArrayList<Neuron>();
	final ArrayList<Neuron> hiddenLayer = new ArrayList<Neuron>();
	final ArrayList<Neuron> outputLayer = new ArrayList<Neuron>();
	final Neuron bias = new Neuron();
	final int[] layers;
	final int randomWeightMultiplier = 1;

	final double epsilon = 0.00000000001;

	final double learningRate = 0.9f;
	final double momentum = 0.7f;
	// Inputs for xor problem
	public double[][] inputs;					//inputs[][] = {	{bUp, bDown, bLeft, bRight, lMov }, {bUp, bDown, bLeft, bRight, lMov }	} an example of an array with two input sets
	// Corresponding outputs, xor training data
	public double[][] expectedOutputs;// = { { 0,0,0,1}, { 0,0,1,0}, { 0,1,0,0 }, { 1,0,0,0} };	//
	double resultOutputs[][];
	double output[];
	// for weight update all
	final HashMap<String, Double> weightUpdate = new HashMap<String, Double>();
	//The following is modifications that I have made to the program.
	ReadSolutions r;

	public NeuralNetwork(int input, int hidden, int output, float percent, int maxRuns, String map) {
		
		r = new ReadSolutions(map);
		
		inputs = 	r.getInputs(percent);	//Finds random inputs, a percent amount of total data. It then sets the output array, to be pulled in getOutputs()
		expectedOutputs = new double[inputs.length][2];
		resultOutputs = new double[inputs.length][2];
		for(int i = 0; i < expectedOutputs.length; i++){
			for(int j = 0; j < expectedOutputs[i].length; j++){
				expectedOutputs[i][j] = 0;
			}
		}
		expectedOutputs = r.getOutputs();
		
		this.layers = new int[] { input, hidden, output };
		df = new DecimalFormat("#.0#");

		
		//Create all neurons and connections Connections are created in the neuron class
		for (int i = 0; i < layers.length; i++) {
			if (i == 0) { // input layer
				for (int j = 0; j < layers[i]; j++) {
					Neuron neuron = new Neuron();
					inputLayer.add(neuron);
				}
			} else if (i == 1) { // hidden layer
				for (int j = 0; j < layers[i]; j++) {
					Neuron neuron = new Neuron();
					neuron.addInConnectionsS(inputLayer);
					neuron.addBiasConnection(bias);
					hiddenLayer.add(neuron);
				}
			}

			else if (i == 2) { // output layer
				for (int j = 0; j < layers[i]; j++) {
					Neuron neuron = new Neuron();
					neuron.addInConnectionsS(hiddenLayer);
					neuron.addBiasConnection(bias);
					outputLayer.add(neuron);
				}
			} else {
				System.out.println("!Error NeuralNetwork init");
			}
		}

		// initialize random weights
		for (Neuron neuron : hiddenLayer) {			//For each neuron
			ArrayList<Connection> connections = neuron.getAllInConnections();
			for (Connection conn : connections) {	//For each connection, set up the weights between connections from inputs to hidden layer.
				double newWeight = getRandom();		//Sets weight to a number between -1 and 1
				conn.setWeight(newWeight);
			}
		}
		for (Neuron neuron : outputLayer) {			//Sets up weights between connections from hidden layer to output.
			ArrayList<Connection> connections = neuron.getAllInConnections();
			for (Connection conn : connections) {
				double newWeight = getRandom();		//Random number between -1 and 1
				conn.setWeight(newWeight);
			}
		}

		// reset id counters
		Neuron.counter = 0;
		Connection.counter = 0;

		if (isTrained) {
			trainedWeights();
			updateAllWeights();
		}
		
		double minErrorCondition = 0.001;
		run(maxRuns, minErrorCondition);
	}

	void run(int maxSteps, double minError) {
		int i;
		double error = 1;
		
		for (i = 0; i < maxSteps && error > minError; i++) { 	// Train neural network until minError reached or maxSteps exceeded
			error = 0;
			for (int p = 0; p < inputs.length; p++) {			//Goes through each input set. 
				setInput(inputs[p]);							//Sets each input neuron x to input[p][x] (sets each neuron to the current input set)
				activate();										//Goes through each hidden and output layer neuron, and calculates output

				output = getOutput();							//returns output[], an array holding the output of each neuron.						
				resultOutputs[p] = output;						//this adds the second part of the array to resultOutputs[][], in part [p][]

				for (int j = 0; j < expectedOutputs[p].length; j++) {				//Should only go through once
					double err = Math.pow(output[j] - expectedOutputs[p][j], 2);
					error += err;
				}

				applyBackpropagation(expectedOutputs[p]);
			}
		}

		printResult();
		
		System.out.println("Sum of squared errors = " + error);
		System.out.println("##### EPOCH " + i+"\n");
		if (i == maxSteps) {
			System.out.println("!Error training try again");
		} else {
			//printAllWeights();
			//printWeightUpdate();
			System.out.println("TotalRuns: "+i);
		}
	
	}

	public int testNet(double[] in){	//Runs an input array through the net, converts the output neurons to 0=up; 1=down; 2=left; 3=right.
		int o = -1;
		
		setInput(in);							//Sets each input neuron x to input[p][x] (sets each neuron to the current input set)
		activate();										//Goes through each hidden and output layer neuron, and calculates output

		output = getOutput();							//returns output[], an array holding the output of each neuron.	

		for(int i = 0; i < output.length; i++){
			System.out.println(output[i]);
		}
		
		return o;
	}
	
	double getRandom() {	//Gets random number between -1 and 1
		return randomWeightMultiplier * (rand.nextDouble() * 2 - 1); // [-1;1[
	}

	/**
	 * 
	 * @param inputs
	 *            There is equally many neurons in the input layer as there are
	 *            in input variables
	 */
	public void setInput(double inputs[]) {
		for (int i = 0; i < inputLayer.size(); i++) {
			inputLayer.get(i).setOutput(inputs[i]);	//Sets input neuron 1 to input[p][1], sets input neuron 2 to input[p][2].
		}
	}

	public double[] getOutput() {	//goes through the output neurons, and returns an array output[x] with the output of each neuron.
		double[] outputs = new double[outputLayer.size()];
		for (int i = 0; i < outputLayer.size(); i++)
			outputs[i] = outputLayer.get(i).getOutput();
		return outputs;
	}

	/**
	 * Calculate the output of the neural network based on the input The forward
	 * operation
	 */
	public void activate() {
		for (Neuron n : hiddenLayer)
			n.calculateOutput();
		for (Neuron n : outputLayer)
			n.calculateOutput();
	}

	/**
	 * all output propagate back
	 * 
	 * @param expectedOutput
	 *            first calculate the partial derivative of the error with
	 *            respect to each of the weight leading into the output neurons
	 *            bias is also updated here
	 */
	public void applyBackpropagation(double expectedOutput[]) {

		// error check, normalize value ]0;1[
		for (int i = 0; i < expectedOutput.length; i++) {
			double d = expectedOutput[i];
			if (d < 0 || d > 1) {
				if (d < 0)
					expectedOutput[i] = 0 + epsilon;
				else
					expectedOutput[i] = 1 - epsilon;
			}
		}

		int i = 0;
		for (Neuron n : outputLayer) {											//For each output neuron, it will: (sets the weights for each connection.
			ArrayList<Connection> connections = n.getAllInConnections();
			for (Connection con : connections) {								//For each connection between any neuron
				double ak = n.getOutput();										
				double ai = con.leftNeuron.getOutput();
				double desiredOutput = expectedOutput[i];

				double partialDerivative = -ak * (1 - ak) * ai					
						* (desiredOutput - ak);									//((-neuronOutput) * (1 - neuronOutput)) * hiddenLayerNeuron * desiredOutput
				double deltaWeight = -learningRate * partialDerivative;			//delta = Multiplies by the negative learning rate.
				double newWeight = con.getWeight() + deltaWeight;				//newWeight = current connection weight + deltaWeight (that we figured out above.)
				con.setDeltaWeight(deltaWeight);								//Records this as the previous deltaWeight. Used for figuring out momentum in learning.
				con.setWeight(newWeight + momentum * con.getPrevDeltaWeight());	
			}
			i++;
		}

		// update weights for the hidden layer
		for (Neuron n : hiddenLayer) {
			ArrayList<Connection> connections = n.getAllInConnections();
			for (Connection con : connections) {
				double aj = n.getOutput();
				double ai = con.leftNeuron.getOutput();
				double sumKoutputs = 0;
				int j = 0;
				for (Neuron out_neu : outputLayer) {
					double wjk = out_neu.getConnection(n.id).getWeight();
					double desiredOutput = (double) expectedOutput[j];
					double ak = out_neu.getOutput();
					j++;
					sumKoutputs = sumKoutputs
							+ (-(desiredOutput - ak) * ak * (1 - ak) * wjk);
				}

				double partialDerivative = aj * (1 - aj) * ai * sumKoutputs;
				double deltaWeight = -learningRate * partialDerivative;
				double newWeight = con.getWeight() + deltaWeight;
				con.setDeltaWeight(deltaWeight);
				con.setWeight(newWeight + momentum * con.getPrevDeltaWeight());
			}
		}
	}

	void printResult()
	{
		System.out.println("NN example with xor training");
		for (int p = 0; p < inputs.length; p++) {
			System.out.print("INPUTS: ");
			for (int x = 0; x < layers[0]; x++) {
				System.out.print(inputs[p][x] + " ");
			}

			System.out.print("EXPECTED: ");
			for (int x = 0; x < layers[2]; x++) {
				System.out.print(expectedOutputs[p][x] + " ");
			}

			System.out.print("ACTUAL: ");
			for (int x = 0; x < layers[2]; x++) {
				System.out.print(resultOutputs[p][x] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}

	String weightKey(int neuronId, int conId) {
		return "N" + neuronId + "_C" + conId;
	}

	/**
	 * Take from hash table and put into all weights
	 */
	public void updateAllWeights() {
		// update weights for the output layer
		for (Neuron n : outputLayer) {
			ArrayList<Connection> connections = n.getAllInConnections();
			for (Connection con : connections) {
				String key = weightKey(n.id, con.id);
				double newWeight = weightUpdate.get(key);
				con.setWeight(newWeight);
			}
		}
		// update weights for the hidden layer
		for (Neuron n : hiddenLayer) {
			ArrayList<Connection> connections = n.getAllInConnections();
			for (Connection con : connections) {
				String key = weightKey(n.id, con.id);
				double newWeight = weightUpdate.get(key);
				con.setWeight(newWeight);
			}
		}
	}

	// trained data
	void trainedWeights() {
		weightUpdate.clear();
		
		weightUpdate.put(weightKey(3, 0), 1.03);
		weightUpdate.put(weightKey(3, 1), 1.13);
		weightUpdate.put(weightKey(3, 2), -.97);
		weightUpdate.put(weightKey(4, 3), 7.24);
		weightUpdate.put(weightKey(4, 4), -3.71);
		weightUpdate.put(weightKey(4, 5), -.51);
		weightUpdate.put(weightKey(5, 6), -3.28);
		weightUpdate.put(weightKey(5, 7), 7.29);
		weightUpdate.put(weightKey(5, 8), -.05);
		weightUpdate.put(weightKey(6, 9), 5.86);
		weightUpdate.put(weightKey(6, 10), 6.03);
		weightUpdate.put(weightKey(6, 11), .71);
		weightUpdate.put(weightKey(7, 12), 2.19);
		weightUpdate.put(weightKey(7, 13), -8.82);
		weightUpdate.put(weightKey(7, 14), -8.84);
		weightUpdate.put(weightKey(7, 15), 11.81);
		weightUpdate.put(weightKey(7, 16), .44);
	}

	public void printWeightUpdate() {
		System.out.println("printWeightUpdate, put this i trainedWeights() and set isTrained to true");
		// weights for the hidden layer
		for (Neuron n : hiddenLayer) {
			ArrayList<Connection> connections = n.getAllInConnections();
			for (Connection con : connections) {
				String w = df.format(con.getWeight());
				System.out.println("weightUpdate.put(weightKey(" + n.id + ", "
						+ con.id + "), " + w + ");");
			}
		}
		// weights for the output layer
		for (Neuron n : outputLayer) {
			ArrayList<Connection> connections = n.getAllInConnections();
			for (Connection con : connections) {
				String w = df.format(con.getWeight());
				System.out.println("weightUpdate.put(weightKey(" + n.id + ", "
						+ con.id + "), " + w + ");");
			}
		}
		System.out.println();
	}

	public void printAllWeights() {
		System.out.println("printAllWeights");
		// weights for the hidden layer
		for (Neuron n : hiddenLayer) {
			ArrayList<Connection> connections = n.getAllInConnections();
			for (Connection con : connections) {
				double w = con.getWeight();
				System.out.println("n=" + n.id + " c=" + con.id + " w=" + w);
			}
		}
		// weights for the output layer
		for (Neuron n : outputLayer) {
			ArrayList<Connection> connections = n.getAllInConnections();
			for (Connection con : connections) {
				double w = con.getWeight();
				System.out.println("n=" + n.id + " c=" + con.id + " w=" + w);
			}
		}
		System.out.println();
	}
}
