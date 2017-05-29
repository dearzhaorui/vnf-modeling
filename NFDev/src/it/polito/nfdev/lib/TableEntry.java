package it.polito.nfdev.lib;

import java.util.ArrayList;
import java.util.List;

public class TableEntry {
	
	protected List<Object> values;
	
		
	public TableEntry(int length) {
		this.values = new ArrayList<Object>(length);
		for(int i=0; i<length; i++)
			values.add(new Object());
	}
	
	
	public Object getValue(int index){
		if(index < 0 || index >= values.size())
			return null;
		return values.get(index);
	}
	
	public void setValue(int index, Object value) {
		values.set(index, value);
	}
	
	public int size() {
		return values.size();
	}
	
}
