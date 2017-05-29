package it.polito.nfdev.vRouter;

import java.util.List;

import it.polito.nfdev.lib.*;

import it.polito.nfdev.lib.Packet.PacketField;
import it.polito.nfdev.lib.RoutingResult.Action;

public class Router extends NetworkFunction {

	private Table routeTable;
	
	public Router(List<Interface> interfaces) {
		super(interfaces);
	
		this.routeTable = new Table(2,0); // dstIP, forwardInterface;   no consider the field 'nextHop' and 'hopCount',because related to arpTable(MAC) and shortest path
		this.routeTable.setTypes(Table.TableTypes.Ip,Table.TableTypes.Ip);
	}

	@Override
	public RoutingResult onReceivedPacket(Packet packet, Interface iface) {
		Packet packet_in = null;
		try {			
			packet_in = packet.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return new RoutingResult(Action.DROP, null, null); 
		}
	
		TableEntry entry = routeTable.matchEntry(packet_in.getField(PacketField.IP_DST));
	
		if(entry==null){
			return new RoutingResult(Action.DROP, null, null);
		}
		
		String interfaceIp = (String)entry.getValue(1); 
		Interface forwardInterface = null;  // must exist, because it has been checked when add routeTable rules....
		for(Interface i : interfaces){
			if(i.IP_ADRESS.compareTo(interfaceIp)==0){
				forwardInterface = i;
				break;
			}
		}
		
		return new RoutingResult(Action.FORWARD, packet_in, forwardInterface);
	}	
	
	public boolean addRouteRule(String dstIp, String inface){
		assert validInface(inface);  // this interface must belong to this router
		
		TableEntry entry = new TableEntry(2);
		entry.setValue(0, dstIp.trim());
		entry.setValue(1, inface.trim());
			
		return routeTable.storeEntry(entry);
	}
		
	public boolean validInface(String inface){   // this Ip must belong to this router
		for(Interface iface : interfaces){
			if(iface.IP_ADRESS.compareTo(inface)==0)
				return true;
		}
		return false;
	}
	
	
	public boolean removeRouteRule(String dstIp){
		TableEntry entry  = routeTable.matchEntry(dstIp);
		
		return routeTable.removeEntry(entry);  // if return false, --> entry is empty
	}

	public void clearRouteTable(){
		routeTable.clear();
	}
}
