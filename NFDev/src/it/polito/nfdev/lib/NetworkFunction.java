package it.polito.nfdev.lib;

import java.util.List;

public abstract class NetworkFunction {
	
	protected List<Interface> interfaces;
	
	protected Interface internalInterface;
	protected Interface externalInterface;
	
	public NetworkFunction(List<Interface> interfaces) {
		this.interfaces = interfaces;
		
		this.internalInterface = new Interface(0, Interface.Type.INTERNAL);
		this.externalInterface = new Interface(1, Interface.Type.EXTERNAL);
		
	}
	
	public abstract RoutingResult onReceivedPacket(Packet packet, Interface iface);
	
	public Interface getInternalInterface(){
		return this.internalInterface;
	}
	
	public Interface getExternalInterface(){
		return this.externalInterface;
	}

}
