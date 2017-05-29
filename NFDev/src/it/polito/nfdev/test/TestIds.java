package it.polito.nfdev.test;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import it.polito.nfdev.ids.Ids;
import it.polito.nfdev.lib.Packet;
import it.polito.nfdev.lib.RoutingResult;
import it.polito.nfdev.lib.Packet.PacketField;
import it.polito.nfdev.lib.RoutingResult.Action;

public class TestIds {
	
	public static final String NOGO = "NOGO";
	public static final String GO = "GO";
	public static final String TEST = "TEST";
	
	private Ids ids;
	private Packet p;
	
	@Before
	public void setup(){
		
		ids = new Ids();
		p = new Packet();
		p.setField(PacketField.IP_SRC, "10.0.0.1");
		p.setField(PacketField.IP_DST, "8.8.8.8");
		p.setField(PacketField.PORT_SRC, "2000");
		p.setField(PacketField.PORT_DST, "80");
	}
	
	@AfterClass
	public static void end() {
		System.out.println("\n+++ All tests executed +++\n");
	}
	
	@Test
	public void testNoRules(){
		
		p.setField(PacketField.APPLICATION_PROTOCOL, Packet.HTTP_REQUEST);
		RoutingResult result = ids.onReceivedPacket(p, ids.getInternalInterface());
		Assert.assertTrue("The packet was not forwarded", result.getAction().compareTo(Action.FORWARD)==0);
	
		p.setField(PacketField.APPLICATION_PROTOCOL, Packet.HTTP_RESPONSE);
		result = ids.onReceivedPacket(p, ids.getExternalInterface());
		Assert.assertTrue("The packet was not forwarded", result.getAction().compareTo(Action.FORWARD)==0);
		
		p.setField(PacketField.APPLICATION_PROTOCOL, Packet.POP3_REQUEST);
		result = ids.onReceivedPacket(p, ids.getExternalInterface());
		Assert.assertTrue("The packet was forwarded", result.getAction().compareTo(Action.DROP)==0);
		
		System.out.println("Test no rules passed");
	}
	
	@Test
	public void testIds(){
		
		p.setField(PacketField.APPLICATION_PROTOCOL, "");
		boolean res = ids.addIdsRule(NOGO);
		Assert.assertTrue("Rule was not added", res);
		
		res = ids.removeIdsRule(TEST);
		Assert.assertFalse("Rule was removed", res);
		
		p.setField(PacketField.APPLICATION_PROTOCOL, Packet.HTTP_REQUEST);
		p.setField(PacketField.L7DATA, GO);
		RoutingResult result = ids.onReceivedPacket(p, ids.getInternalInterface());
		Assert.assertTrue("The packet was not forwarded", result.getAction().compareTo(Action.FORWARD)==0);
	
		res = ids.removeIdsRule(NOGO);
		Assert.assertTrue("Rule was not removed", res);
		
		ids.addIdsRule(NOGO);
		p.setField(PacketField.L7DATA, NOGO);
		
		result = ids.onReceivedPacket(p, ids.getInternalInterface());
		Assert.assertTrue("The packet was not forwarded", result.getAction().compareTo(Action.DROP)==0);
		
		System.out.println("Test Ids passed");
	}
	
}
