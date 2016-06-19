package edu.cetys.cinap.icc;

import java.util.ArrayList;
import java.util.List;

public class Heap<T> implements Comparable<T>{
	
	@Override
	public int compareTo(T arg0) {
		if (this.compareTo(arg0) == 1)
			return 1;
		else if (this.compareTo(arg0) == -1)
			return -1;
		else
			return 0;
	}
	
	private ArrayList<T> Array;
	public ArrayList<T> getArray(){
		return this.Array;
	}
	public void setArray(ArrayList<T> A){
		this.Array = A;
	}
	
	private void MaxHeapify(ArrayList<T> A, int i, int n){
		int l = (2 * i) + 1;
		int r = (2 * i) + 2;
		int largest = i;
		
		if (l <= n){
			if ( ((Comparable<T>) A.get(l)).compareTo(A.get(largest)) == 1)
				largest = l;
		}
		if (r <= n){
			if ( ((Comparable<T>) A.get(r)).compareTo(A.get(largest)) == 1)
				largest = r;
		}
		
		if ( ((Comparable<T>) A.get(largest)).compareTo(A.get(i)) == 1){
			T tmp = A.get(i);
			A.set(i, A.get(largest));
			A.set(largest, tmp);
			MaxHeapify(A, largest, n);
		}
	}
	
	private void MinHeapify(ArrayList<T> A, int i, int n){
		int l = (2 * i) + 1;
		int r = (2 * i) + 2;
		int shortest = i;
		
		if (l <= n){
			if ( ((Comparable<T>) A.get(l)).compareTo(A.get(shortest)) == -1)
				shortest = l;
		}
		if (r <= n){
			if ( ((Comparable<T>) A.get(r)).compareTo(A.get(shortest)) == -1)
				shortest = r;
		}
		
		if ( ((Comparable<T>) A.get(shortest)).compareTo(A.get(i)) == -1){
			T tmp = A.get(i);
			A.set(i, A.get(shortest));
			A.set(shortest, tmp);
			MinHeapify(A, shortest, n);
		}
	}
	
	public void BuildMaxHeap(){
		int n = (Array.size() / 2) - 1;
		for (int i = n; i >= 0; i--){
			MaxHeapify(Array, i, Array.size()-1);
		}
	}

	public void BuildMinHeap(){
		int n = (Array.size() / 2) - 1;
		for (int i = n; i >= 0; i--){
			MinHeapify(Array, i, Array.size()-1);
		}
	}
	
	public void ExtractMaxHeap(){
		BuildMaxHeap();
		Array.remove(0);
	}
	
	public void ExtractMinHeap(){
		BuildMinHeap();
		Array.remove(0);
	}
	
	public void HeapSortMax(){
		BuildMaxHeap();
		for (int i = Array.size()-1; i > 0; i--){
			T tmp = Array.get(0);
			Array.set(0, Array.get(i));
			Array.set(i, tmp);
			MaxHeapify(Array, 0, i - 1);
		}
	}
	
	public void HeapSortMin(){
		BuildMinHeap();
		for (int i = Array.size()-1; i > 0; i--){
			T tmp = Array.get(0);
			Array.set(0, Array.get(i));
			Array.set(i, tmp);
			MinHeapify(Array, 0, i - 1);
		}
	}

}
