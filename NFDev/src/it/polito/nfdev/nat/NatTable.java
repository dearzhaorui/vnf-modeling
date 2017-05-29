package it.polito.nfdev.nat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.polito.nfdev.lib.Table;
import it.polito.nfdev.lib.TableEntry;

public class NatTable extends Table {
	
	public NatTable(int primaryFields, int secondaryFields) {
		super(primaryFields, secondaryFields);
	}

	public void checkForTimeout(Integer timeout) {
		List<TableEntry> toBeRemoved = new ArrayList<>();
		for(TableEntry entry : entries)
		{
			Date d = (Date) entry.getValue(6);
			Calendar now = Calendar.getInstance();
			now.setTime(new Date());
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			c.add(Calendar.SECOND, timeout);
			if(c.before(now))
			{
				// Entry expired
				toBeRemoved.add(entry);
			}
		}
		entries.removeAll(toBeRemoved);
	}

}
