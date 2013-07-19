package unionFind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UnionFind<X> {

	List<Item> items = new ArrayList<Item>();

	Map<X, Item> itemMap = new HashMap<X,Item>();


	Map<Item, X> xMap = new HashMap<Item,X>();
	
	public void add(X value){
		Item n = new Item( null, 0);
		n.parent = n;
		items.add(n);
		itemMap.put(value, n);
		xMap.put(n,value);
	}
	
	public boolean connected(X n1, X n2){
		return find( itemMap.get(n1) ).equals(find( itemMap.get(n2) ));
	}
		
	public Item find(Item x){
		if( x.parent.equals(x))
			return x;
		else{
			x.parent = find(x.parent);
			return x.parent;
		}
	}
	
	public X find(X n){
		Item item = find(itemMap.get(n));
		return xMap.get(item);
	}
	
	public void connect(X n1, X n2){
		union( itemMap.get(n1), itemMap.get(n2) );
	}
	
	public void union(Item x, Item y){
		Item xRoot = find(x);
		Item yRoot = find(y);
		if(xRoot.rank > yRoot.rank)
			yRoot.parent = xRoot;
		else if(xRoot != yRoot){//unless x and y are already in same set, merge them
			xRoot.parent = yRoot;
			if(xRoot.rank == yRoot.rank)
				yRoot.rank = yRoot.rank+1;
		}
	}
	
	public void reset(){
		items.clear();
		itemMap.clear();
		xMap.clear();
	}
	
	public static void main(String[] args){
		UnionFind<String> uf = new UnionFind<String>();
		String x = "X";
		String y = "Y";
		
		uf.add(x);
		uf.add(y);
		
		uf.connect(x,y);
		
		System.out.println(uf.find(x));
		System.out.println(uf.find(y));
		
		
	}

}
