package misc;

/**Utility class*/
public class Pair implements Comparable<Pair>{

	int index;
	double value;

	public Pair(int index, double value){
		this.index = index;
		this.value = value;
	}

	public int compareTo(Pair o) {
		if(value > o.value)
			return -1;
		else if(value == o.value)
			return 0;
		else
			return 1;
	}

	public String toString(){
		return "[" + index + "," + value + "]";
	}
}