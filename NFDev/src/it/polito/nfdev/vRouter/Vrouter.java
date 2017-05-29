package it.polito.nfdev.vRouter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import it.polito.nfdev.lib.Interface;
import it.polito.nfdev.lib.NetworkFunction;
import it.polito.nfdev.lib.Packet;
import it.polito.nfdev.lib.RoutingResult;
import it.polito.nfdev.lib.Table;
import it.polito.nfdev.lib.TableEntry;
import it.polito.nfdev.lib.Packet.PacketField;
import it.polito.nfdev.lib.RoutingResult.Action;
import it.polito.nfdev.verification.Verifier;


public class Vrouter extends NetworkFunction {

	private Table arpTable;
	private Table routeTable;
	
	public Vrouter(List<Interface> interfaces) {
		super(interfaces);
		
		this.arpTable = new Table(2,0);   // Ip, MAC. Assume that this table is Static, so no TimeStamp.
		this.routeTable = new Table(5,0); // network, mask, interface, nextHop, hopCount;
		this.routeTable.setTypes(Table.TableTypes.Ip,Table.TableTypes.Ip,Table.TableTypes.Ip,Table.TableTypes.Ip,Table.TableTypes.Generic);
	}

	@Override
	public RoutingResult onReceivedPacket(Packet packet, Interface iface) {
		Packet packet_in = null;
		try {
			/* The function may provide the same (modified) packet as output or clone the input one */
			packet_in = packet.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return new RoutingResult(Action.DROP, null, null); 
		}
		
		List<TableEntry> multipleRoutes = new ArrayList<>();  // in case of multiple matched entries, choose the longest matching and the lowest hop counte 
		TableEntry matchedEntry=null;
		
		String dstIp = packet_in.getField(PacketField.IP_DST);
		String[] ipAddrParts = dstIp.split("\\.");
		
		for(TableEntry entry : routeTable.getAllTableEntry()){
			String network = (String)entry.getValue(0);
			String mask = (String)entry.getValue(1);
			
			String networkAddr="";
	        
	        String[] maskParts=mask.split("\\.");
	        for(int i=0;i<4;i++){
	        int x=Integer.parseInt(ipAddrParts[i]);
	        int y=Integer.parseInt(maskParts[i]);
	        int z=x&y;
	        networkAddr+=z+".";
	        }
			
			if(networkAddr.compareTo(network)==0){
				multipleRoutes.add(entry);
			}
		}
		
		if(multipleRoutes.size()==0){
			return new RoutingResult(Action.DROP, null, null);
		}else if(multipleRoutes.size()==1){
			matchedEntry = multipleRoutes.get(0);
		}else{
			List<TableEntry> longestMatches = new ArrayList<>();
			try {
				longestMatches = findLongestMatches(multipleRoutes);
			} catch (UnknownHostException e) {				
				e.printStackTrace();
				return new RoutingResult(Action.DROP, null, null);
			}
			if(longestMatches.size()==1)
				matchedEntry = longestMatches.get(0);
			else
				matchedEntry = findShortestPath(longestMatches).get(0);			
		}
		
		// after get the best matched entry, check arpTable and forward the packet
		String interfaceIp = (String)matchedEntry.getValue(2); 
		Interface forwardInterface = null;  // must exist, because it has been checked when add routeTable....
		for(Interface i : interfaces){
			if(i.IP_ADRESS.compareTo(interfaceIp)==0){
				forwardInterface = i;
				break;
			}
		}	
		
		
		TableEntry arpEntry = arpTable.matchEntry((String)matchedEntry.getValue(3),Verifier.ANY_VALUE);
		if(arpEntry==null)   //if no mapping between IP and MAC address, drop the packet....
			return new RoutingResult(Action.DROP, null, null);
		else{
			packet_in.setField(PacketField.ETH_DST, (String)arpEntry.getValue(1));
		    return new RoutingResult(Action.FORWARD, packet_in, forwardInterface);
		    
		}
	}
	

	public boolean addArpRule(String ip, String mac){
		TableEntry entry = new TableEntry(2);
		entry.setValue(0, ip.trim());
		entry.setValue(1, mac.trim());
		
		return arpTable.storeEntry(entry);
	}
	
	
	
	public boolean addRouteRule(String network, String mask, String inface, String nextHop, Integer hopCount){
		assert validInface(inface);  // this interface must belong to this router
		
		TableEntry entry = new TableEntry(5);
		entry.setValue(0, network.trim());
		entry.setValue(1, mask.trim());
		entry.setValue(2, inface.trim());
		entry.setValue(3, nextHop.trim());
		entry.setValue(4, new Integer(hopCount));
		
		return routeTable.storeEntry(entry);
	}
	
	
	public boolean validInface(String inface){   // this Ip must belong to this router
		for(Interface iface : interfaces){
			if(iface.IP_ADRESS.compareTo(inface)==0)
				return true;
		}
		return false;
	}
	
	
	public List<TableEntry> findLongestMatches(List<TableEntry> multipleRoutes) throws UnknownHostException{
		List<TableEntry> longestMatches = new ArrayList<>();
		
		Map<TableEntry,Integer> map = new HashMap<>();
		for(TableEntry entry : multipleRoutes){
			Integer prefixLength = convertNetmaskToCIDR((String)entry.getValue(1));  // net mask
			map.put(entry, prefixLength);
		}
		
		int maxLength=0;
		for(Integer i : map.values()){
			if(maxLength<i)
				maxLength=i;
		}
		
		for(Entry<TableEntry, Integer> entry : map.entrySet()){
			if(entry.getValue().intValue()==maxLength)
				longestMatches.add((TableEntry)entry.getKey());
		}
			
		return longestMatches;
	}
	
	
	private List<TableEntry> findShortestPath(List<TableEntry> longestMatches) {
		List<TableEntry> paths = new ArrayList<>();
		int min = Integer.MAX_VALUE;
		for(TableEntry entry : longestMatches){
			int hopCount = ((Integer)entry.getValue(4)).intValue();  // 4th field is the hop count
			if(min > hopCount)
				min = hopCount;				
		}
		
		for(TableEntry entry : longestMatches){
			int hopCount = ((Integer)entry.getValue(4)).intValue();
			if(hopCount==min)
				paths.add(entry);				
		}
		
		return paths;
	}
	
	 public int convertNetmaskToCIDR(String submask) throws UnknownHostException {

		 InetAddress netmask = InetAddress.getByName(submask);
	        byte[] netmaskBytes = netmask.getAddress();
	        int cidr = 0;
	        boolean zero = false;
	        for(byte b : netmaskBytes){
	            int mask = 0x80;

	            for(int i = 0; i < 8; i++){
	                int result = b & mask;
	                if(result == 0){
	                    zero = true;
	                }else if(zero){
	                    throw new IllegalArgumentException("Invalid netmask.");
	                } else {
	                    cidr++;
	                }
	                mask >>>= 1;
	            }
	        }
	        return cidr;
	    }
	
	public boolean removeArpRule(String ip){
		TableEntry entry  = arpTable.matchEntry(ip);
		
		return arpTable.removeEntry(entry);  // if return false, --> entry is empty
	}
	
	public boolean removeRouteRule(String network, String mask){
		TableEntry entry  = routeTable.matchEntry(network, mask);
		
		return routeTable.removeEntry(entry);  // if return false, --> entry is empty
	}
	
	public void clearArpTable(){
		arpTable.clear();
	}

	public void clearRouteTable(){
		routeTable.clear();
	}
}
