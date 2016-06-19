package edu.cetys.cinap.icc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Huffman {
	private String sourceFile;
	private Reader reader;
	private Map<Character,Integer> charsFrequency;
	private Map<Character,String> BinaryStrings;
	private Map<String,Character> Characters;
	private List<BitSet> BitSets;
	private ArrayList<Tree> Trees;
	private Tree tree;
	private Heap<Tree> heap;


	public Huffman() {

	}
	public Huffman(String filename) {
		this.sourceFile = filename;
		try {
			this.reader = new FileReader(filename);
		} catch (Exception e) {

		}

		this.charsFrequency = new HashMap<Character,Integer>();
		setFrequencies(charsFrequency, reader);

		Trees = new ArrayList<Tree>();
		setTrees(charsFrequency, Trees);
		heap = new Heap<Tree>();
		heap.setArray(Trees);
		BuildTree(Trees);
		this.BinaryStrings = new HashMap<Character,String>();
		setBinaryStrings(tree);
	}

	private void setFrequencies(Map<Character,Integer> chars, Reader r){
		try{
			int data = r.read();
			while (data != -1){
				if (chars.containsKey((char)data)){
					int n = (Integer)chars.get((char)data);
					n++;
					chars.put((char)data,n);
				}
				else{
					chars.put((char)data, 1);
				}
				data = r.read();
			}
		} catch(Exception e){}
		finally{
			try { r.close(); } catch (Exception e) { }
		}
	}

	private void setTrees(Map<Character,Integer> cf, List<Tree> t){
		for (char c : cf.keySet()){
			Tree nt = new Tree();
			nt.c = c;
			nt.f = cf.get(c);
			t.add(nt);
		}
	}

	private void BuildTree(List<Tree> trees){
		if (trees.size() > 1){
			heap.HeapSortMax();
			Tree P = new Tree();
			P.Left = trees.get(0);
			P.Right = trees.get(1);
			P.f = P.Left.f + P.Right.f;
			heap.ExtractMinHeap();
			heap.ExtractMinHeap();
			trees.add(P);
			BuildTree(trees);
		}
		else
			this.tree = trees.get(0);
	}

	private void setBinaryStrings(Tree t){
		this.BinaryStrings = t.inOrder();
		if (this.BinaryStrings.size() == 1)
			for (char c : this.BinaryStrings.keySet())
				this.BinaryStrings.put(c, "1");
	}

	public Boolean compressFile() {
		return this.compressFile(this.BinaryStrings, this.sourceFile);
	}
	private Boolean compressFile(Map<Character,String> bs, String filename){
		Boolean success = false;
		try {
			File outFile = new File("compressed-" + filename);
			//OutputStream output = new BufferedOutputStream(new FileOutputStream(outFile));
			FileOutputStream fos = new FileOutputStream(outFile);
			ObjectOutputStream output = new ObjectOutputStream(fos);
			Reader r = new FileReader(filename);
			try {
				output.writeObject(this.BinaryStrings);

				int data = r.read();
				String s = bs.get((char) data);
				//In case the very first bit is 0. It turns it into 1 and when file is readen will turn back to 0.
				if (s.indexOf('0') == 0) {
					output.writeBoolean(true);
					s = new StringBuilder(s).replace(0, 1, "1").toString();
				} else {
					output.writeBoolean(false);
				}

				data = r.read();
				while (data != -1) {
					char c = (char) data;
					s += bs.get(c);
					if (s.length() >= 64 && s.lastIndexOf('1') > s.indexOf('1')){
						int n = s.lastIndexOf('1');
						while (n > 64) {
							n = s.substring(0,n).lastIndexOf('1');
						}
						BitSet b = new BitSet();
						for (int i = 0; i < n; i++) {
							if (s.charAt(i) == '1')
								b.set( (n-1) - i );
						}
						output.write(b.toByteArray());
						s = s.substring(n);
					}
					data = r.read();
				}

				BitSet b = BitSetFromString(s);
				output.write(b.toByteArray());
				success = true;
			} catch (Exception e) {

			} finally {
				r.close();
				output.close();
				fos.close();
			}
		} catch (Exception e) {

		}

		return success;
	}

	public Boolean uncompressFile(String filename){
		Boolean success = false;
		Boolean one2zero = false;
		byte[] b = null;

		try {
			File inputFile = new File(filename);
			//InputStream input = new BufferedInputStream(new FileInputStream(inputFile));
			FileInputStream fis = new FileInputStream(inputFile);
			ObjectInputStream input = new ObjectInputStream(fis);
			int totalRead = 0;

			this.BinaryStrings = (Map<Character,String>) input.readObject();
			//Reverse Map Columns
			this.Characters = new HashMap<String,Character>();
			for (char c : BinaryStrings.keySet()){
				Characters.put(BinaryStrings.get(c), c);
			}

			try {
				// Look if the 1st bit should really be 1 or 0.
				one2zero = input.readBoolean();

				b = new byte[(int) inputFile.length()];
				while (totalRead < b.length) {
					try {
						b[totalRead] = input.readByte();
						totalRead++;
					} catch (Exception e) {
						break;
					}
				}
			} catch (Exception e) {
			} finally {
				input.close(); fis.close();
			}

			//Create BitSets of 8 bytes each or less.
			this.BitSets = new ArrayList<BitSet>();
			int buffer = totalRead >= 8? 8 : totalRead;
			for (int i = 0; i < b.length; i+=buffer){
				byte[] t = new byte[buffer];
				int n = 0;
				while (n < buffer && i+n < b.length){
					t[n] = b[i+n];
					n++;
				}
				BitSet bs = BitSet.valueOf(t);
				this.BitSets.add(bs);
			}

			writeNewFile(ReadBitSets(BitSets, one2zero), filename);
			success = true;
		} catch (Exception e) {

		}

		return success;
	}

	private List<String> ReadBitSets(List<BitSet> bs, Boolean o2z){
		List<String> stringL = new ArrayList<String>();
		String s = "";
		//Turn the 1 into 0 as should be.
		if (o2z){
			s += "0";
			for (int i = bs.get(0).length()-2; i >= 0; i--){
				s += bs.get(0).get(i) ? "1" : "0";
			}
		}
		else{
			for (int i = bs.get(0).length()-1; i >= 0; i--){
				s += bs.get(0).get(i) ? "1" : "0";
			}
		}
		stringL.add(s);
		//Parse the rest of de BitSets
		for (int i = 1; i < bs.size(); i++){
			s = "";
			for (int  j = bs.get(i).length()-1; j >= 0; j--){
				s += bs.get(i).get(j) ? "1" : "0";
			}
			stringL.add(s);
		}

		return stringL;
	}

	private void writeNewFile(List<String> list, String filename) throws IOException{
		File file = new File("uncompressed-" + filename);
		Writer w = null;

		try {
			w = new FileWriter(file);
			String s = "";
			for (int i = 0; i < list.size(); i++) {
				for (int j = 0; j < list.get(i).length(); j++){
					s += list.get(i).charAt(j);
					if (Characters.get(s) != null){
						w.write(this.Characters.get(s));
						s = "";
					}
				}
			}
		} catch (Exception e) {

		} finally {
			w.close();
		}
	}

	private static BitSet BitSetFromString(final String s) {
        return BitSet.valueOf(new long[] { Long.parseLong(s, 2) });
    }

    private static String BitSetToString(BitSet bs) {
        return Long.toString(bs.toLongArray()[0], 2);
    }

}
