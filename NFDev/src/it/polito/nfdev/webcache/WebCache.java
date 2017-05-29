package it.polito.nfdev.webcache;

import java.net.URL;
import java.util.List;

import it.polito.nfdev.lib.Interface;
import it.polito.nfdev.lib.NetworkFunction;
import it.polito.nfdev.lib.Packet;
import it.polito.nfdev.lib.Packet.PacketField;
import it.polito.nfdev.lib.RoutingResult.Action;
import it.polito.nfdev.lib.TableEntry;
import it.polito.nfdev.verification.Verifier;
import it.polito.nfdev.verification.Table;
import it.polito.nfdev.lib.RoutingResult;

public class WebCache extends NetworkFunction {
	
	private Interface internalFace;
	private Interface externalFace;

	
	@Table( fields = {"URL", "CONTENT"} )  // 
	private CacheTable cacheTable;
	
	public WebCache(List<Interface> interfaces) {
		super(interfaces);
		assert interfaces.size() == 2;
		internalFace = null;
		externalFace = null;
		for(Interface i : interfaces)
		{
			if(i.getAttributes().contains(Interface.INTERNAL_ATTR))
				internalFace = i;
			if(i.getAttributes().contains(Interface.EXTERNAL_ATTR))
				externalFace = i;
		}
		assert internalFace != null;
		assert externalFace != null;
		assert internalFace.getId() != externalFace.getId();
		cacheTable = new CacheTable(2,0);
		cacheTable.setDataDriven();  //== true
	}

	@Override
	public RoutingResult onReceivedPacket(Packet packet, Interface iface) {
		if(iface.isInternal())
		{
			if(packet.equalsField(PacketField.APPLICATION_PROTOCOL,Packet.HTTP_REQUEST)){
				TableEntry entry = cacheTable.matchEntry(packet.getField(PacketField.L7DATA), Verifier.ANY_VALUE); // content can be any value
				if(entry != null)   //--if no this cache, do nothing..... 
				{
					Packet p = null;
					try {
						p = packet.clone();
						p.setField(PacketField.IP_DST, packet.getField(PacketField.IP_SRC));
						p.setField(PacketField.IP_SRC, packet.getField(PacketField.IP_DST));
						p.setField(PacketField.PORT_DST, packet.getField(PacketField.PORT_SRC));
						p.setField(PacketField.PORT_SRC, packet.getField(PacketField.PORT_DST));
						p.setField(PacketField.APPLICATION_PROTOCOL, Packet.HTTP_RESPONSE);
						p.setField(PacketField.L7DATA, (String)entry.getValue(0));
						return new RoutingResult(Action.FORWARD, p, internalInterface); // receive http_request from internal network, reply from specific internal interface
						
					} catch (CloneNotSupportedException e) {
						e.printStackTrace();
						return new RoutingResult(Action.DROP, null, null);
					}
				}
			}
			return new RoutingResult(Action.FORWARD, packet, externalInterface);  // if the packet is from internal network and not http_request, FORWARD outside the network directly 
			
		}
		else  //-- ! iface.isInternal()
		{		//-- if WebCache receive a packet from outside network, it means this webcache proxy need to update the cache Content from external Server 
			if(packet.equalsField(PacketField.APPLICATION_PROTOCOL,Packet.HTTP_RESPONSE)){
				try {
					Content content = new Content(new URL(packet.getField(PacketField.L7DATA)));
					CacheTableEntry cacheEntry = new CacheTableEntry(2);
					cacheEntry.setValue(0, packet.getField(PacketField.L7DATA));
					cacheEntry.setValue(1, content);
					cacheTable.storeEntry(cacheEntry);
					return new RoutingResult(Action.FORWARD, packet, internalInterface);  // after cache the web content, the cache proxy still need to forward the reply packet to internal network
				} catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
			// if packet is not a http_request, no need to cache it, but just FORWARD it to internal network
			return new RoutingResult(Action.FORWARD, packet, internalInterface);
			
		}
		/*if(iface.isInternal())
			return new RoutingResult(Action.FORWARD, packet, externalInterface);
		else
			return new RoutingResult(Action.FORWARD, packet, internalInterface);*/
	}

}
