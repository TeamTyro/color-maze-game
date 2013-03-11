package Net;

public class Connection {
	double weight = 0;
	double prevDeltaWeight = 0; // for momentum
	double deltaWeight = 0;

	final public Neuron leftNeuron;
	final public Neuron rightNeuron;
	static public int counter = 0;
	final public int id; // auto increment, starts at 0

	public Connection(Neuron fromN, Neuron toN) {
		leftNeuron = fromN;
		rightNeuron = toN;
		id = counter;
		counter++;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double w) {
		weight = w;
	}

	public void setDeltaWeight(double w) {
		prevDeltaWeight = deltaWeight;
		deltaWeight = w;
	}

	public double getPrevDeltaWeight() {
		return prevDeltaWeight;
	}

	public Neuron getFromNeuron() {
		return leftNeuron;
	}

	public Neuron getToNeuron() {
		return rightNeuron;
	}
}
