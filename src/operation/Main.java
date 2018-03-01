package operation;

import java.util.List;
import java.util.Scanner;

import entity.point;

public class Main {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String fileName=args[0];
		List<point> ls=readData.getData(fileName);
		boolean changeInput=true;
		clustering c=new clustering(ls,changeInput);
		int K, On,Os, L,I,Oc,Nc,key=-1;
		float k;

		do {
			K=Integer.parseInt(args[1]);
			On=Integer.parseInt(args[2]);
			Os=Integer.parseInt(args[3]);
			L=Integer.parseInt(args[4]);
			I=Integer.parseInt(args[5]);
			Oc=Integer.parseInt(args[6]);
			Nc=Integer.parseInt(args[7]);
			k=(float)Float.parseFloat(args[8]);
			key=c.run(K, On, Os, L, I,Oc, Nc, k);
		}while(key!=0);
		
		if(key==0)
		c.printResult();
	}
}
