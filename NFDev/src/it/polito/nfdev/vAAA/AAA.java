package it.polito.nfdev.vAAA;

import java.util.ArrayList;

import it.polito.nfdev.lib.Interface;
import it.polito.nfdev.lib.NetworkFunction;
import it.polito.nfdev.lib.Packet;
import it.polito.nfdev.lib.RoutingResult;
import it.polito.nfdev.lib.Packet.PacketField;
import it.polito.nfdev.lib.RoutingResult.Action;
import it.polito.nfdev.nat.NatTableEntry;
import it.polito.nfdev.lib.Table;
import it.polito.nfdev.lib.TableEntry;

public class AAA extends NetworkFunction {

	public static final String ACCOUNTING_RESPONSE = "Accounting_Response";
	public static final String ACCESS_REJECT = "Access_Reject";
	public static final String AUTHORIZATION_RESPONSE = "Authorization_Response";  // including the network configurations
	
	private Table userTable;
	
	public AAA() {
		super(new ArrayList<Interface>());
		
		this.userTable = new Table(3,0);   // (UserName, Password, ByteCount(Initial value = 0 ))
		this.userTable.setTypes(Table.TableTypes.Generic , Table.TableTypes.Generic, Table.TableTypes.Generic);
		
	}

	@Override
	public RoutingResult onReceivedPacket(Packet packet, Interface iface) {

		Packet p = null;
		try {
			p = packet.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		if(packet.equalsField(PacketField.TRANSPORT_PROTOCOL, Packet.L2_UDP) && packet.equalsField(PacketField.PORT_DST, "1812")){ 
			 	// if it's a Authentication Packet from NAS Client, CZ authentication port is 1812
			String Uname_Pwd = packet.getField(PacketField.L7DATA);
			String[] parts = Uname_Pwd.split(":");   // assume L7DATA is 'Username:Password'
			String userName = parts[0];
			String password = parts[1];
			
			TableEntry entry = userTable.matchEntry(userName , password);
			
			if(entry==null){
				p.setField(PacketField.L7DATA, ACCESS_REJECT);
			}else{
				p.setField(PacketField.L7DATA, AUTHORIZATION_RESPONSE);
			}
			
			p.setField(PacketField.IP_SRC, packet.getField(PacketField.IP_DST));
			p.setField(PacketField.PORT_SRC, packet.getField(PacketField.PORT_DST));
			p.setField(PacketField.IP_DST, packet.getField(PacketField.IP_SRC));
			p.setField(PacketField.PORT_DST, packet.getField(PacketField.PORT_SRC));
		
			return new RoutingResult(Action.FORWARD,p,iface);
		}
		else if(packet.equalsField(PacketField.TRANSPORT_PROTOCOL, Packet.L2_UDP) && packet.equalsField(PacketField.PORT_DST, "1813")){
					// if it's a Accounting Packet from NAS Client , CZ accounting port is 1813
			
			String Uname_Pwd = packet.getField(PacketField.L7DATA);
			String[] parts = Uname_Pwd.split(":");   // assume L7DATA is 'Username:	Periodic number of Bytes'
			String userName = parts[0];
			Long byteCount = Long.parseLong(parts[1]);
			
			TableEntry entry = userTable.matchEntry(userName);
			
			if(entry==null){
				return new RoutingResult(Action.DROP, null, null);
			}
			else{		// Update the total number of Bytes
				Long totalBytes = byteCount + (Long)entry.getValue(2);
				TableEntry e = new NatTableEntry(3);
				e.setValue(0, entry.getValue(0));  // Username
				e.setValue(1, entry.getValue(1));  // Password
				e.setValue(2, totalBytes);		   // ByteCount
				userTable.removeEntry(entry);
				userTable.storeEntry(e);
				
				p.setField(PacketField.IP_SRC, packet.getField(PacketField.IP_DST));
				p.setField(PacketField.PORT_SRC, packet.getField(PacketField.PORT_DST));
				p.setField(PacketField.IP_DST, packet.getField(PacketField.IP_SRC));
				p.setField(PacketField.PORT_DST, packet.getField(PacketField.PORT_SRC));
				p.setField(PacketField.L7DATA, ACCOUNTING_RESPONSE);
				return new RoutingResult(Action.FORWARD,p,iface);
			}
		}
		else{
			return new RoutingResult(Action.DROP,null,null);
		}
		
	}
	
	
	public boolean addAclRule(String username, String password){
		
		TableEntry e = userTable.matchEntry(username.trim());
		
		if(e!=null)      // can not have two same Usernames
			return false;
		
		TableEntry entry = new TableEntry(3);
		entry.setValue(0, username.trim());
		entry.setValue(1, password.trim());
		entry.setValue(3, new Long(0));
	
		return userTable.storeEntry(entry);
	}
	
	public boolean removeAclRule(String username, String password){
		TableEntry entry  = userTable.matchEntry(username,password);
		
		return userTable.removeEntry(entry);  // if return false, --> entry is empty
	}
	
	
	public void clearUserTable() {
		
		this.userTable.clear();
	}

}
