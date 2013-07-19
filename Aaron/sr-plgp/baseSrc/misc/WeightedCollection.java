package misc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/** Represents a collection of type T and provides methods which allow elements to be added 
 or randomly selected according to its weight. This class does not manage the memory for 
 these pointers and sometimes segfaults on pointers. It's clearly imperfect but it is
 sufficient.*/
public class WeightedCollection<T> {

	private ArrayList<Pair<T>> elements;
	private double weightSum;

	//MUTATIONJ
	public WeightedCollection(){
		elements = new ArrayList<Pair<T>>();
		weightSum=0;
	}
	public WeightedCollection(WeightedCollection<T> wc){
		elements = new ArrayList<Pair<T>>();

		//performs a deep clone of the elements list.
		for(int i = 0; i < wc.elements.size(); i++){
			elements.add(new Pair<T>(wc.elements.get(i)));
		}

		weightSum = wc.weightSum;
	}


	public void addElement(T elem){
		addElement(elem, 1);
	}

	public void clear(){
		elements.clear();
		weightSum = 0;
	}

	public void addElement(T elem, double weight){
		if(weight < 0) {
			try {
				throw new Exception("VUWLGP::WeightedCollection::AddElement - negative weight not allowed");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		elements.add(new Pair<T>(weight, elem));
		weightSum += weight;
	}

	/** Gets an element randomly according to the weights. An element's probability of being
     selected is equal to the proportion of the sum of all weights. Zero weights are ok but
     elements with this weight will never be selected. Negative weights are not allowed and
     will throw an exception.*/
	public T getRandomElement(){

		double entrySum = Rand.uniform() * weightSum;
		double sumSoFar = 0;

		for(int i = 0; i < elements.size(); i++){
			sumSoFar += elements.get(i).weight;
			if (sumSoFar > entrySum){
				return elements.get(i).value;
			}
		}

		// We got to here, and we shouldn't have done, so just return the final element:
		throw new RuntimeException("WeightedCollectionException");
	}
	
	public int getRandomIndex(){
		double entrySum = Rand.uniform() * weightSum;
		double sumSoFar = 0;

		for(int i = 0; i < elements.size(); i++){
			sumSoFar += elements.get(i).weight;
			if (sumSoFar > entrySum){
				return i;
			}
		}

		// We got to here, and we shouldn't have done, so just return the final element:
		throw new RuntimeException("WeightedCollectionException");
	}

	public Set<T> getRandomElements(int num){
		
		double sumSoFar = 0;
		double sumRemoved = 0;
		
		Set<T> results = new HashSet<T>();

		ArrayList<Pair<T>> remainder = new ArrayList<Pair<T>>();
		for(int i = 0; i < elements.size(); i++){
			remainder.add(elements.get(i));
		}

		for(int i = 0;i < num; i++){
			double random = Math.random()*(weightSum - sumRemoved);
			sumSoFar=0;
			for(int j = 0; j < remainder.size(); j++){
				sumSoFar += remainder.get(j).weight;
				if (sumSoFar > random){
					results.add(remainder.get(j).value);
					sumRemoved += remainder.get(j).weight;
					remainder.remove(j);
					break;
				}
			}
		}
		return results;
	}
	
	


	// Misc helper/inspection methods:
	public int NumberOfElements() { 
		return elements.size(); 
	}
	double WeightSum(){ 
		return weightSum;
	}

	/**TODO this is bad. for testing only*/
	public T getValue(int index){
		return elements.get(index).value;
	}

	public double getWeight(int index){
		return elements.get(index).weight;
	}

	private class Pair<E>{

		public Pair( double weight, E value){
			this.value = value;
			this.weight = weight;
		}

		public Pair(Pair<E> p2){
			value = p2.value;
			weight = p2.weight;
		}

		public E value;
		public double weight;

	}

}


