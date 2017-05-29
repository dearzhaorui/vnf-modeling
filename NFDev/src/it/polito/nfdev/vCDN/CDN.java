package it.polito.nfdev.vCDN;

import java.util.List;

import it.polito.nfdev.lib.Interface;
import it.polito.nfdev.lib.NetworkFunction;
import it.polito.nfdev.lib.Packet;

import it.polito.nfdev.lib.Table;
import it.polito.nfdev.lib.TableEntry;
import it.polito.nfdev.lib.Packet.PacketField;
import it.polito.nfdev.lib.RoutingResult;
import it.polito.nfdev.lib.RoutingResult.Action;
import it.polito.nfdev.verification.Verifier;

public class CDN extends NetworkFunction {

	
	private Table cdnTable;
	
	public CDN(List<Interface> interfaces) {
		super(interfaces);
		
		this.cdnTable = new Table(3,0);  // serverName, srcIP, cacheIP
		this.cdnTable.setTypes(Table.TableTypes.Generic, Table.TableTypes.Ip,Table.TableTypes.Ip);
		this.cdnTable.setDataDriven();
	}
	
	@Override
	public RoutingResult onReceivedPacket(Packet packet, Interface iface) {
		
		if(iface.isInternal())
		{
			if(packet.equalsField(PacketField.APPLICATION_PROTOCOL,Packet.DNS_REQUEST)){
				TableEntry entry = cdnTable.matchEntry(packet.getField(PacketField.L7DATA), packet.getField(PacketField.IP_SRC), Verifier.ANY_VALUE); // content can be any value
				if(entry != null)   //--if no this cache, do nothing..... 
				{
					Packet p = null;
					try {
						p = packet.clone();
						p.setField(PacketField.IP_DST, packet.getField(PacketField.IP_SRC));
						p.setField(PacketField.IP_SRC, packet.getField(PacketField.IP_DST));
						p.setField(PacketField.PORT_DST, packet.getField(PacketField.PORT_SRC));
						p.setField(PacketField.PORT_SRC, packet.getField(PacketField.PORT_DST));
						p.setField(PacketField.APPLICATION_PROTOCOL, Packet.DNS_RESPONSE);
						p.setField(PacketField.L7DATA, (String)entry.getValue(2));   // SuperDNS returns cacheIP
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
		{		
			// if packet is from external network, it may be a proxy of other server, just forward
			return new RoutingResult(Action.FORWARD, packet, internalInterface);
			
		}
		
	}
	
	
	public boolean addRouteRule(String serverName, String srcIp, String cacheIp){
		
		TableEntry entry = new TableEntry(3);
		entry.setValue(0, serverName.trim());
		entry.setValue(1, srcIp.trim());
		entry.setValue(2, cacheIp.trim());
			
		return cdnTable.storeEntry(entry);
	}
	
	public boolean removeRouteRule(String serverName, String srcIp, String cacheIp){
		TableEntry entry  = cdnTable.matchEntry(serverName, srcIp, cacheIp);
		
		return cdnTable.removeEntry(entry);  
	}

	public void clearCdnTable(){
		cdnTable.clear();
	}

}
