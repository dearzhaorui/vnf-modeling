package it.polito.nfdev.nat;

import java.util.Date;

import it.polito.nfdev.lib.TableEntry;

public class NatTableEntry extends TableEntry {
	
	
	public NatTableEntry(int length) {
		super(length);   // initial tableentry
	}

	public void setTimestamp(Date d) {
		values.set(6, d);
	}


}
