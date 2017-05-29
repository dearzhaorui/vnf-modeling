package it.polito.nfdev.aclfirewall;

import java.util.ArrayList;

import it.polito.nfdev.lib.Interface;
import it.polito.nfdev.lib.NetworkFunction;
import it.polito.nfdev.lib.Packet;
import it.polito.nfdev.lib.Packet.PacketField;
import it.polito.nfdev.lib.RoutingResult.Action;
import it.polito.nfdev.lib.RoutingResult;
import it.polito.nfdev.lib.Table;
import it.polito.nfdev.lib.TableEntry;

public class AclFirewall extends NetworkFunction {
	
	private Table aclTable;
	
	public AclFirewall(){
		super(new ArrayList<Interface>());
		
		this.aclTable = new Table(2, 0);
		this.aclTable.setTypes(Table.TableTypes.Ip,Table.TableTypes.Ip);
	}

	@Override
	public RoutingResult onReceivedPacket(Packet packet, Interface iface) {	// in ACL, the matched packet is not allowed to be forwored 
		
		if(iface.isInternal()){
			TableEntry entry = aclTable.matchEntry(packet.getField(PacketField.IP_SRC),packet.getField(PacketField.IP_DST));
			if(entry!=null)
				return new RoutingResult(Action.DROP,null,null);

			return new RoutingResult(Action.FORWARD,packet,externalInterface);
			
		}else{
			TableEntry entry = aclTable.matchEntry(packet.getField(PacketField.IP_SRC),packet.getField(PacketField.IP_DST));
			if(entry!=null)
				return new RoutingResult(Action.DROP,null,null);

			return new RoutingResult(Action.FORWARD,packet,internalInterface);
		}
		
	}
	
	public boolean addAclRule(String ipSrc, String ipDst){
		TableEntry entry = new TableEntry(2);
		entry.setValue(0, ipSrc.trim());
		entry.setValue(1, ipDst.trim());
	
		return aclTable.storeEntry(entry);
	}
	
	public boolean removeAclRule(String ipSrc, String ipDst){
		TableEntry entry  = aclTable.matchEntry(ipSrc,ipDst);
		
		return aclTable.removeEntry(entry);  // if return false, --> entry is empty
	}
	
	public void clearAclTable(){
		aclTable.clear();
	}

}
