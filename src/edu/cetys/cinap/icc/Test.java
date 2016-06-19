package edu.cetys.cinap.icc;

public class Test {
	public static void main(String[] args){
		compress();
		uncompress();
	}
	
	private static void compress() {
		Huffman h = new Huffman("test.txt");
		if (h.compressFile()) {
			System.out.println("Sucessfully compressed file.");
		} else {
			System.out.println("Failed to compress file.");
		}
	}
	
	private static void uncompress() {
		Huffman h = new Huffman();
		if (h.uncompressFile("compressed-test.txt")) {
			System.out.println("Sucessfully uncompressed file.");
		} else {
			System.out.println("Failed to uncompress file.");
		}
	}
	
}