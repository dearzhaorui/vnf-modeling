package it.polito.nfdev.nat;

import java.util.Date;
import java.util.List;

import it.polito.nfdev.lib.Interface;
import it.polito.nfdev.lib.NetworkFunction;
import it.polito.nfdev.lib.Packet;
import it.polito.nfdev.lib.Packet.PacketField;
import it.polito.nfdev.lib.RoutingResult.Action;
import it.polito.nfdev.lib.TableEntry;
import it.polito.nfdev.verification.Configuration;
import it.polito.nfdev.verification.Table;
import it.polito.nfdev.verification.Verifier;
import it.polito.nfdev.lib.RoutingResult;

/* The NAT implementation */
public class Nat extends NetworkFunction {
	
	private final Integer TIMEOUT; // in seconds
	
	@Table(fields = {"INTERNAL_IP", "INTERNAL_PORT", "EXTERNAL_IP", "EXTERNAL_PORT"})
	private NatTable natTable;
	
	private PortPool portPool;
	private Interface internalFace;
	private Interface externalFace;
	@Configuration
	private String natIp;
	
	public Nat(List<Interface> interfaces, String natIp, Integer timeout) {
		super(interfaces);
		assert interfaces.size() == 2;
		assert timeout > 0;
		assert natIp != null && !natIp.isEmpty();
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
		this.natTable = new NatTable(6, 1);
		this.natIp = natIp;
		this.portPool = new PortPool(10000, 1024); // starting port, # of ports
		this.TIMEOUT = timeout;
		this.natTable.setDataDriven();
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
		if(iface.isInternal())
		{
			// Packet coming from the internal network
			/* Let's look if we already have an entry for this IP_SRC/PORT_SRC */
			TableEntry entry = natTable.matchEntry(packet_in.getField(PacketField.IP_SRC), packet_in.getField(PacketField.PORT_SRC), Verifier.ANY_VALUE, Verifier.ANY_VALUE);
			if(entry != null)
			{
				packet_in.setField(PacketField.IP_SRC, (String)entry.getValue(4));
				packet_in.setField(PacketField.PORT_SRC, (String)entry.getValue(5));
				((NatTableEntry)entry).setTimestamp(new Date());  // if has record, just need to chang the date in Nat table
				return new RoutingResult(Action.FORWARD, packet_in, externalInterface);
			}
			else
			{
				/* New connection: let's pick a new available port */
				Integer new_port = portPool.getAvailablePort();
				if(new_port == null)
					return new RoutingResult(Action.DROP, null, null);	// No available ports, discard new connections
				TableEntry e = new NatTableEntry(7);
				e.setValue(0, packet_in.getField(PacketField.IP_SRC));
				e.setValue(1, packet_in.getField(PacketField.PORT_SRC));
				e.setValue(2, packet_in.getField(PacketField.IP_DST));
				e.setValue(3, packet_in.getField(PacketField.PORT_DST));
				e.setValue(4, natIp);
				e.setValue(5, String.valueOf(new_port));
				e.setValue(6, new Date());
				/* Rewrite the fields of the packet */
				packet_in.setField(PacketField.IP_SRC, natIp);
				packet_in.setField(PacketField.PORT_SRC, String.valueOf(new_port));

				/* Save this entry */
				natTable.storeEntry(e);
				/* Forward the packet upstream */
				return new RoutingResult(Action.FORWARD, packet_in, externalInterface);
			}
		}
		else  // packet from external interface
		{
			// Packet coming from the external network
			TableEntry entry = natTable.matchEntry(Verifier.ANY_VALUE, Verifier.ANY_VALUE, packet_in.getField(PacketField.IP_SRC), packet_in.getField(PacketField.PORT_SRC), packet_in.getField(PacketField.IP_DST), packet_in.getField(PacketField.PORT_DST));
			//ConnectionDescriptor descriptor = natTable.get(new NatEntryOld(packet.getField(PacketField.IP_DST), packet.getField(PacketField.PORT_DST)));
			if(entry == null)
				return new RoutingResult(Action.DROP, null, null);
			// Here we should implement more checks (paranoid NAT):
			// 	- the packet origin (source ip/port) is the allowed one
			packet_in.setField(PacketField.IP_DST, (String) entry.getValue(0));
			packet_in.setField(PacketField.PORT_DST, (String) entry.getValue(1));
			((NatTableEntry)entry).setTimestamp(new Date());
			return new RoutingResult(Action.FORWARD, packet_in, internalInterface);
		}
	}
	
	public void reset() {
		/* Clear the NAT table */
		this.natTable.clear();
	}
	
	public void checkForTimeout() {
		/* Purge expired entries */
		natTable.checkForTimeout(TIMEOUT);
	}
	
	public void printNatTable() {
		/* Print for debug */
		System.out.println(natTable);
	}

}
