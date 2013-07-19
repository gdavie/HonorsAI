package unionFind;

public class Item {

	public int rank;

	public Item parent;
	
	public Item( Item parent, int rank){
		this.parent = parent;
		this.rank = rank;
	}

}
