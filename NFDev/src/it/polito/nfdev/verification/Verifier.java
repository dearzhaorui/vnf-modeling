package it.polito.nfdev.verification;

import it.polito.nfdev.lib.Packet.PacketField;

public class Verifier {
	
	public enum EXPR { FROM_INTERNAL, TO_INTERNAL, FROM_EXTERNAL, TO_EXTERNAL };
	
	public enum PacketType { PACKET_IN, PACKET_OUT };
	
	public static final String ANY_VALUE = "ANY";
	
	public static class Network {
		public static void sendPacket(EXPR exp) { }
	}
	
	public static class Packet {
		public static void setPacketField(PacketType type, PacketField packetField, Object value) { };
		public static void satisfy(PacketType type, String str) { }
		public static void satisfy(PacketType type, EXPR exp) { }
	}
	
	public static class State {
		public static Packet matchPacket(Object... fields) { return new Packet(); }
		public static Packet matchPacket(Expression... fields) { return new Packet(); }
		public static void notMatchPacket(Object... fields) { }
		
		public static class Table {
			public static void storeEntry(Object... fields) { }
			public static Entry matchEntry(Object... fields) { return new Entry(); }
		}
		
		public static class Entry {
			public Object getField(int index) { return new Object(); }
		}
		
	}
	
	public static void ignore() { }
	
	public static void requireConstaint(Expression exp) { }
	
}
