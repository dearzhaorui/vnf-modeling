package it.polito.nfdev.lib;

import java.util.ArrayList;
import java.util.List;

public class Interface {
	
	public static final String INTERNAL_ATTR = "INTERNAL";
	public static final String EXTERNAL_ATTR = "EXTERNAL";
	
	public String IP_ADRESS; //--used by arpTable and routeTable in 'vRouter' function
	
	public enum Type{INTERNAL, EXTERNAL};
	
	
	private Type interfaceType;
	
	private final Integer id;
	private List<String> attributes;
	
	public Interface(Integer id, Type type) {
		this.id = id;
		this.interfaceType = type;
		this.attributes = new ArrayList<>();
	}
	
	public Integer getId() {
		return id;
	}
	public List<String> getAttributes() {
		return attributes;
	}
	public void addAttribute(String attribute) {
		this.attributes.add(attribute);
	}
	
	public boolean isInternal(){
		if(interfaceType == Type.INTERNAL)
			return true;
		
		return false;
	}
	
	

}
