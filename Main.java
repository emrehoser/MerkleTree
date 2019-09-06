package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;



import project.MerkleTree;
import project.Node;
import util.HashGeneration;

public class Main {

	public static void main(String[] args){
		
		
		MerkleTree m0 = new MerkleTree("data/1.txt");		
		bfsTraversal(m0.getRoot());
		
		
		
		
		boolean valid = m0.checkAuthenticity("data/1meta.txt");
		System.out.println(valid);
		
		
		// The following just is an example for you to see the usage. 
		// Although there is none in reality, assume that there are two corrupt chunks in this example.
		ArrayList<Stack<String>> corrupts = m0.findCorruptChunks("data/1meta.txt");
		//System.out.println("Corrupt hash of first corrupt chunk is: " + corrupts.get(0).pop());
		//System.out.println("Corrupt hash of second corrupt chunk is: " + corrupts.get(1).pop());
		
		
		download("secondaryPart/data/download_from_trusted.txt");
		
	}
	
	public static  void bfsTraversal(Node root) {
		ArrayList<String> bfsOrder = new ArrayList<String>();
		LinkedList<Node> queue = new LinkedList<Node>();
		Node p;
		
		queue.add(root);
		int i = 0;
		while (! queue.isEmpty()) {
			p = queue.remove();
			bfsOrder.add(p.getData());
			System.out.println(bfsOrder.get(i));
			i++;
			if (p.getLeft() != null)
				queue.add(p.getLeft());
			if (p.getRight() != null)
				queue.add(p.getRight());			
		}
		
		
	}
	
	public static void fileCreater(String s,String i) {
		URL url = null;
		try {
			url = new URL(s);
		} catch (MalformedURLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} 
		InputStream in = null;
		try {
			in = url.openStream();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		try {
			Files.copy(in,Paths.get("secondaryPart/"+i), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		try {
			in.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	
	public static void download(String path) {
		
		
		File file = new File(path);
		Scanner cs = null;
		try {
			cs = new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Queue<String> meta = new LinkedList<String>();
		Queue<String> alt = new LinkedList<String>();
		Queue<String> sor = new LinkedList<String>();
		Queue<String> numb = new LinkedList<String>();
		Queue<String> numb2 = new LinkedList<String>();
		while(cs.hasNextLine()) {
			String s = cs.nextLine();
			if(s.contains("http")) {
				if(s.contains("meta")) {
					meta.offer(s);
				}
				else if(s.contains("alt")) {
					alt.offer(s);
				}
				else {
					sor.offer(s);
				}
			}
		}
		
		
		while(!meta.isEmpty()) {
			String s =meta.poll();
			String num = s.substring(s.lastIndexOf("/")+1, s.indexOf("meta"));
			numb.offer(num);
			String s2 = num+"meta.txt";
			fileCreater(s,s2);
			
		}
		while(!sor.isEmpty()) {
			String s =sor.poll();
			String num = s.substring(s.lastIndexOf("/")+1, s.lastIndexOf("."));
			String s2 = num+".txt";
			fileCreater(s,s2);
			
		}
		while(!alt.isEmpty()) {
			String s =alt.poll();
			String num = s.substring(s.lastIndexOf("/")+1, s.indexOf("alt"));
			String s2 = num+"alt.txt";
			fileCreater(s,s2);
			
		}
		try {
			Files.createDirectories(Paths.get("secondaryPart/data/","split"));
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		while(!numb.isEmpty()) {
			String num = numb.poll();
			try {
				Files.createDirectories(Paths.get("secondaryPart/data/split/",num));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			numb2.offer(num);
		}


		
		while(!numb2.isEmpty()) {
			String num = numb2.poll();
			
			ArrayList<String> meta2 = new ArrayList<String>();
			Scanner cs3 =null;
			try {
				cs3 = new Scanner(new File("secondaryPart/"+num+"meta.txt"));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while(cs3.hasNextLine()) {
				meta2.add(cs3.nextLine());
			}
			File file2 = new File("secondaryPart/"+num+".txt");
			File file4 = new File("secondaryPart/"+num+"alt.txt");
			Scanner cs2 = null;
			Scanner cs4 = null;
			try {
				cs2 = new Scanner(file2);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				cs4 = new Scanner(file4);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println();
			
			
			
			String note = "";
			
			while(cs2.hasNextLine()) {
				String s = cs2.nextLine();
				String s4 = cs4.nextLine();
				String name = "data/split/"+num+"/" + s.substring(s.lastIndexOf('/')+1);
				
				note+="secondaryPart/"+name+"\n";
				fileCreater(s,name);
				File file3 = new File("secondaryPart/"+name);
				String hash ="";
				try {
					hash = HashGeneration.generateSHA256(file3);
				} catch (NoSuchAlgorithmException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(!meta2.contains(hash)) {
					fileCreater(s4,name);
					System.out.println(name);
				}
			}
			PrintStream txt=null;
			try {
				txt = new PrintStream(new File("secondaryPart/" + num + ".txt"));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			txt.println(note);
		}
		
		
		
		
			
				
		}
		
	

}
