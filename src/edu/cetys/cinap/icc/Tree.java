package edu.cetys.cinap.icc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.apache.commons.collections15.map.HashedMap;

public class Tree implements Comparable<Tree>{
	
	public char c;
	public Integer f;
	public Tree Right;
	public Tree Left;
	private Map<Character,String> BinaryStrings;
	
	@Override
	public int compareTo(Tree arg0) {
		if (this.f > arg0.f)
			return 1;
		else if (this.f < arg0.f)
			return -1;
		else
			return 0;
	}
	
	public Map<Character,String> inOrder(){
		this.BinaryStrings = new HashMap<Character,String>();
		this.inOrder(this, "");
		return this.BinaryStrings;
	}
	private void inOrder(Tree t, String s){
		if (t != null){
			if (t.Left != null)
				this.inOrder(t.Left, s+"0");
			if (t.Right != null)
				this.inOrder(t.Right, s+"1");
			else
				this.BinaryStrings.put(t.c, s);
		}
	}
	
}
