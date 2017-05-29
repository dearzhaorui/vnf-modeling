package it.polito.nfdev.test;

import org.junit.Assert;
import org.junit.Test;

import it.polito.nfdev.lib.Packet;
import it.polito.nfdev.lib.Packet.PacketField;

public class TestPacket {

	@Test
	public void testClone() throws Exception {
		Packet p1 = new Packet();
		p1.setField(PacketField.ETH_SRC, "00:11:22:33:44:55");
		p1.setField(PacketField.ETH_DST, "55:44:33:22:11:00");
		p1.setField(PacketField.IP_SRC, "10.0.0.1");
		p1.setField(PacketField.IP_DST, "10.0.0.2");
		p1.setField(PacketField.PORT_SRC, "1000");
		p1.setField(PacketField.PORT_DST, "1001");
		
		Packet p2 = p1.clone();
		if(!p1.getField(PacketField.ETH_SRC).equals(p2.getField(PacketField.ETH_SRC)) ||
		   !p1.getField(PacketField.ETH_DST).equals(p2.getField(PacketField.ETH_DST)) ||
		   !p1.getField(PacketField.IP_SRC).equals(p2.getField(PacketField.IP_SRC)) ||
		   !p1.getField(PacketField.IP_DST).equals(p2.getField(PacketField.IP_DST)) ||
		   !p1.getField(PacketField.PORT_SRC).equals(p2.getField(PacketField.PORT_SRC)) ||
		   !p1.getField(PacketField.PORT_DST).equals(p2.getField(PacketField.PORT_DST)))
			Assert.fail("Packets are not equal");
		
		System.out.println("Test on cloned packet passed!");
	}

}
