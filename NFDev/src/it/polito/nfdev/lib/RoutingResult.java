package it.polito.nfdev.lib;

public class RoutingResult {
	
	public enum Action { FORWARD, DROP, UNKNOW };
	
	private Action action;
	private Packet packet;
	private Interface iface;
	
	public RoutingResult(Action action, Packet packet, Interface iface) {
		setAction(action);
		setPacket(packet);
		setIface(iface);
	}
	
	public Action getAction() {
		return action;
	}
	public void setAction(Action action) {
		this.action = action;
	}
	public Packet getPacket() {
		return packet;
	}
	public void setPacket(Packet packet) {
		this.packet = packet;
	}
	public Interface getIface() {
		return iface;
	}
	public void setIface(Interface iface) {
		this.iface = iface;
	}

}
