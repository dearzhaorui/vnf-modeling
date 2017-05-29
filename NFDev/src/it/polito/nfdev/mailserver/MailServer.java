package it.polito.nfdev.mailserver;

import java.util.ArrayList;

import it.polito.nfdev.lib.Interface;
import it.polito.nfdev.lib.NetworkFunction;
import it.polito.nfdev.lib.Packet;
import it.polito.nfdev.lib.RoutingResult;
import it.polito.nfdev.lib.RoutingResult.Action;
import it.polito.nfdev.lib.Packet.PacketField;

public class MailServer extends NetworkFunction {
	
	public static final String RESPONSE = "Response";
	
	public MailServer(){
		super(new ArrayList<Interface>());  //-- it will automatically create 2 default interfaces for hi mailserver;
	}

	@Override
	public RoutingResult onReceivedPacket(Packet packet, Interface iface) {
		Packet p = null;
		try {
			p = packet.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		if(packet.equalsField(PacketField.APPLICATION_PROTOCOL, Packet.POP3_REQUEST)){   //--> just consider the packet's ip & port & return response
			p.setField(PacketField.IP_SRC, packet.getField(PacketField.IP_DST));
			p.setField(PacketField.PORT_SRC, packet.getField(PacketField.PORT_DST));
			p.setField(PacketField.IP_DST, packet.getField(PacketField.IP_SRC));
			p.setField(PacketField.PORT_DST, packet.getField(PacketField.PORT_SRC));
			p.setField(PacketField.APPLICATION_PROTOCOL, Packet.POP3_RESPONSE);
			p.setField(PacketField.L7DATA, RESPONSE);
			
			return new RoutingResult(Action.FORWARD,p,iface);
		}
		
		return new RoutingResult(Action.DROP,null,null);
	}

}
