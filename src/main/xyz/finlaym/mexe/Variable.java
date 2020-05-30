package xyz.finlaym.mexe;

public class Variable implements Comparable<Variable>{
	private String name;
	private double value;
	
	public String getName() {
		return name;
	}

	public double getValue() {
		return value;
	}

	public Variable(String name, double value) {
		this.name = name;
		this.value = value;
	}

	@Override
	public int compareTo(Variable v) {
		return name.length()-v.name.length();
	}

}
