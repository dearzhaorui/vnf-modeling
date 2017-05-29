package it.polito.nfdev.nat;

import java.util.ArrayList;
import java.util.List;

import it.polito.nfdev.lib.Interface;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<Interface> l=new ArrayList<>();
		Nat n=new Nat(l,"sds",10);
		n.printNatTable();
	}

}
