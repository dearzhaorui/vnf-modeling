package it.polito.nfdev.nat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NatTable {
	
	private List<TableEntry> entries;
	
	public NatTable() {
		this.entries = new ArrayList<>();
	}
	
	public TableEntry matchInternalIpPort(String internalIp, String internalPort)
	{
		for(TableEntry entry : entries)
		{
			if(entry.getInternalIp().equals(internalIp) &&
			   entry.getInternalPort().equals(internalPort))
				return entry;
		}
		return null;
	}
	
	public TableEntry matchExternalIpPort(String externalIp, String externalPort)
	{
		for(TableEntry entry : entries)
		{
			if(entry.getExternalIp().equals(externalIp) &&
			   entry.getExternalPort().equals(externalPort))
				return entry;
		}
		return null;
	}
	
	public void addEntry(TableEntry entry)
	{
		entries.add(entry);
	}
	
	public void clear()
	{
		this.entries.clear();
	}
	
	public void checkForTimeout(Integer timeout) {
		List<TableEntry> toBeRemoved = new ArrayList<>();
		for(TableEntry entry : entries) {
			Calendar now = Calendar.getInstance();
			now.setTime(new Date());
			Calendar c = Calendar.getInstance();
			c.setTime(entry.getTimestamp());
			c.add(Calendar.SECOND, timeout);
			if(c.before(now))
			{
				// Entry expired
				toBeRemoved.add(entry);
			}
		}
		entries.removeAll(toBeRemoved);
	}
	
	@Override
	public String toString() {
		String result = "**************** NAT TABLE ****************\n";
		for(TableEntry entry : entries)
		{
			result += entry.getInternalIp() + " | " + entry.getInternalPort() + " | " + entry.getExternalIp() + " | " + entry.getExternalPort() + " | " + entry.getTimestamp() + "\n";
		}
		result += "*******************************************";
		return result;
	}

}
