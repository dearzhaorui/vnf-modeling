package it.polito.nfdev.antispam;

import java.util.ArrayList;
import java.util.List;

import it.polito.nfdev.lib.*;
import it.polito.nfdev.lib.Packet.PacketField;
import it.polito.nfdev.lib.RoutingResult.Action;

public class Antispam extends NetworkFunction {
	
	private Table antiSpamTable;
	private List<String> keywords;

	public Antispam(List<String> keywords){
		super(new ArrayList<Interface>());
		
		this.keywords = new ArrayList<String>(keywords);
		this.antiSpamTable = new Table(1,0);
		this.antiSpamTable.setTypes(Table.TableTypes.ApplicationData);
		for(String key : this.keywords){
			TableEntry e = new TableEntry(1);
			e.setValue(0, key);
			antiSpamTable.storeEntry(e);
		}	
		
	}
	
	@Override
	public RoutingResult onReceivedPacket(Packet packet, Interface iface) {
		
		TableEntry e = antiSpamTable.matchEntry(packet.getField(PacketField.L7DATA));
		
		if(packet.equalsField(PacketField.APPLICATION_PROTOCOL,Packet.POP3_REQUEST) || (packet.equalsField(PacketField.APPLICATION_PROTOCOL,Packet.POP3_RESPONSE) && e == null)){		
			return new RoutingResult(Action.FORWARD, packet, iface);
		}else			
			return new RoutingResult(Action.DROP, null, null);
		
	}
	
	public boolean addKeyword(String word){
		TableEntry entry = new TableEntry(1);
		entry.setValue(0, word);
		
		return keywords.add(word) && antiSpamTable.storeEntry(entry);
	}
	
	public boolean removeKeyword(String word){
		TableEntry e = antiSpamTable.matchEntry(word);
		
		return keywords.remove(word) && antiSpamTable.removeEntry(e);
	}
	
	public void clearKeywords(){
		antiSpamTable.clear();
		keywords.clear();
	}

}
