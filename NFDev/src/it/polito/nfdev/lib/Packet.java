package it.polito.nfdev.lib;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Packet {
	
	public enum PacketField { 
		ETH_SRC, 
		ETH_DST, 
		IP_SRC, 
		IP_DST, 
		PORT_SRC, 
		PORT_DST,
		TRANSPORT_PROTOCOL,
		APPLICATION_PROTOCOL,
		L7DATA
	};
	
	public static final String HTTP_REQUEST = "HTTP_REQ";
	public static final String HTTP_RESPONSE = "HTTP_RESP";
	public static final String POP3_REQUEST = "POP3_REQ";
	public static final String POP3_RESPONSE = "POP3_RESP";  // if app_protocol=pop3_resp && l7data contains specified keyword, must DROP the packet
	public static final String DNS_REQUEST = "DNS_REQ";
	public static final String DNS_RESPONSE = "POP3_RESP";
	
	public static final String L2_TCP = "TCP";
	public static final String L2_UDP = "UDP";
	
	
	private Map<PacketField, String> fields;
	
	public Packet() {
		this.fields = new HashMap<>();
	}
	
	public Packet(Packet p) {
		
	}
	
	public void setField(PacketField pField, String value)
	{
		this.fields.put(pField, value);
	}
	
	public String getField(PacketField pField)
	{
		return this.fields.get(pField);
	}
	
	public boolean equalsField(PacketField field, String value){
		
		String temp = this.fields.get(field);
		if(temp!=null){
			if(value.compareTo(temp)==0)
				return true;
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		String out =" [IP_SRC] " +
			fields.get(PacketField.IP_SRC.name()) +
			" [IP_DST] " +
			fields.get(PacketField.IP_DST.name()) +
			" [PORT_SRC] " +
			fields.get(PacketField.PORT_SRC.name()) +
			" [PORT_DST] " +
			fields.get(PacketField.PORT_DST.name()) +
			" [TRANSPORT_PROTOCOL] " +
			fields.get(PacketField.TRANSPORT_PROTOCOL.name()) +
			" [APPLICATION_PROTOCOL] " +
			fields.get(PacketField.APPLICATION_PROTOCOL.name()) +
			" [L7DATA] " +
			fields.get(PacketField.L7DATA.name());
		return out;
	}
	
	@Override
	public Packet clone() throws CloneNotSupportedException {
		Packet p = new Packet();
		for(Entry<PacketField, String> entry : fields.entrySet())
			p.setField(entry.getKey(), entry.getValue());
		return p;
	}
	
}
