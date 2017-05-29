package it.polito.parser;

import it.polito.nfdev.lib.Packet.PacketField;

public class FieldValuePair {
	
	private PacketField packetField;
	private Object value;
	
	public FieldValuePair(PacketField packetField, Object value) {
		this.packetField = packetField;
		this.value = value;
	}
	
	public PacketField getPacketField() {
		return packetField;
	}
	public Object getValue() {
		return value;
	}

}
