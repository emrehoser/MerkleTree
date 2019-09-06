package project;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;

import util.HashGeneration;

public class MerkleTree {
	private Node root;


	public MerkleTree(String path) {
		File file = new File(path);
		Scanner cs = null;
		try {
			cs = new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Queue<Node> dataBlock = new LinkedList<Node>();	
		Queue<Node> hashCode = new LinkedList<Node>();	
		while(cs.hasNextLine()) {
			//String s = cs.nextLine();
			//Node temp = new Node(s);
			File file2 = new File(cs.nextLine());
			String hash = "";
			try {
				hash = HashGeneration.generateSHA256(file2);
			} catch (NoSuchAlgorithmException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Node current = new Node(hash);
			//current.setLeft(temp);
			dataBlock.offer(current);
			
		}



		Node current = null;
		while(dataBlock.size()>1 || hashCode.size()>1){
			while(!dataBlock.isEmpty()) {
				Node temp = dataBlock.poll();
				Node temp2 = null;
				if(dataBlock.size()==0) {
					temp2 = new Node("");
					String sum = temp.getData() + temp2.getData() ;
					String hash = "";
					try {
						hash = HashGeneration.generateSHA256(sum);
					} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					current = new Node(hash);
					current.setLeft(temp);
					hashCode.offer(current);
				}
				else {
					temp2 = dataBlock.poll();
					String sum = temp.getData() + temp2.getData() ;
					String hash = "";
					try {
						hash = HashGeneration.generateSHA256(sum);
					} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					current = new Node(hash);
					current.setLeft(temp);
					current.setRight(temp2);
					hashCode.offer(current);
				}
				
			}
			
			if(hashCode.size()<2) break;
			
			while(!hashCode.isEmpty()) {
				Node temp = hashCode.poll();
				Node temp2 = null;
				if(hashCode.size()==0) {
					temp2 = new Node("");
					String sum = temp.getData() + temp2.getData();
					String hash = "";
					try {
						hash = HashGeneration.generateSHA256(sum);
					} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					current = new Node(hash);
					current.setLeft(temp);
					dataBlock.offer(current);
				}
				else {
					temp2 = hashCode.poll();
					String sum = temp.getData() + temp2.getData();
					String hash = "";
					try {
						hash = HashGeneration.generateSHA256(sum);
					} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					current = new Node(hash);
					current.setLeft(temp);
					current.setRight(temp2);
					dataBlock.offer(current);
				}
				
			}
		}
		root = current;

	}
	
	public boolean checkAuthenticity(String path) {
		File file = new File(path);
		Scanner cs=null;
		try {
			cs = new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return cs.nextLine().equals(root.getData());
	}
	public Node getRoot() {
		return root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	public ArrayList<Stack<String>> findCorruptChunks(String path) {
		ArrayList<Stack<String>> ctrl = new ArrayList<Stack<String>>();
		ArrayList<String> meta = new ArrayList<String>();
		Stack<String> s = new Stack<String>();
		File file = new File(path);
		Scanner cs = null;
		try {
			cs = new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(cs.hasNextLine()) {
			meta.add(cs.nextLine());
		}
		
		control(ctrl,this.root,meta,s);
		
		return ctrl;
	
	}
	private void control(ArrayList<Stack<String>> ctrl,Node root,ArrayList<String> q,Stack<String> s){
		if(!q.contains(root.getData())) {
			
			s.push(root.getData());
			
			if(root.getLeft()==null&&root.getRight()==null) {
				ctrl.add(s);
				return;
			}
			if(root.getLeft()!=null && !q.contains(root.getLeft().getData()) && q.contains(root.getRight().getData()) ) {
				control(ctrl,root.getLeft(),q,s);
			}
			else if(root.getRight()!=null && !q.contains(root.getRight().getData()) && q.contains(root.getLeft().getData())) {
				control(ctrl,root.getRight(),q,s);
			}
			else {
				Stack<String> sta = new Stack<String>();
				sta.addAll(s);
				control(ctrl,root.getLeft(),q,s);
				control(ctrl,root.getRight(),q,sta);
			}
		}
		
	}
	

}
