package xyz.finlaym.mexe;

public interface FunctionCallback {
	public abstract double execute(String name, double[] arguments);
	public abstract boolean hasCallback(String name);
}
