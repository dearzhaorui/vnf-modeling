package it.polito.nfdev.lib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import it.polito.nfdev.verification.Verifier;

public class Table {
	
	public static enum TableTypes{
		Ethernet,
		Ip,
		Port,
		TransportProtocol,
		ApplicationProtocol,
		ApplicationData,
		Generic
	}
	
	protected List<TableEntry> entries;
	protected int primaryFields;
	protected int secondaryFields;
	
	protected boolean dataDriven = false;
	
	protected List<TableTypes> typeList = new ArrayList<>();
	
	public Table(int primaryFields, int secondaryFields) {
		entries = new ArrayList<>();
		this.primaryFields = primaryFields;
		this.secondaryFields = secondaryFields;
		
	}
	
	public void setTypes(TableTypes... types){
		assert types.length == primaryFields + secondaryFields;
		
		for(TableTypes type : types){
			typeList.add(type);
		}
	}
	
	public boolean storeEntry(TableEntry entry) {
		return entries.add(entry);
	}
	
	public boolean removeEntry(TableEntry entry){
		return entries.remove(entry);
	}
	
	public TableEntry matchEntry(Object... fields) {
		for(TableEntry entry : entries)
		{
			int i;
			boolean flag = true;
			for(i=0; i<fields.length && i<entry.size(); i++)
				if(isValid(fields[i]) && !fields[i].equals(entry.getValue(i)))
					flag = false;
			
			if(flag)
				return entry;
		}
		return null;
	}
	
	protected boolean isValid(Object object) {
		if(!(object instanceof String))
			return false;
		String s = (String) object;
		if(s.equals(Verifier.ANY_VALUE))
			return false;
		else
			return true;
	}
	
	public void clear() {
		entries.clear();
	}

	public boolean setDataDriven() {
		return dataDriven = true;
	}

	
	public List<TableEntry> getAllTableEntry(){
		return entries;
	}

}
