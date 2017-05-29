package it.polito.nfdev.test;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import it.polito.nfdev.aclfirewall.AclFirewall;
import it.polito.nfdev.lib.Packet;
import it.polito.nfdev.lib.Packet.PacketField;
import it.polito.nfdev.lib.RoutingResult;
import it.polito.nfdev.lib.RoutingResult.Action;

public class TestAclFilrewall {
	
	private AclFirewall firewall;
	private Packet p;
	
	@Before
	public void setup(){
		
		firewall = new AclFirewall();
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
	public void testEmptyAcl(){
		
		RoutingResult result = firewall.onReceivedPacket(p, firewall.getInternalInterface());
		Assert.assertTrue("The packet was not forwarded", result.getAction().compareTo(Action.FORWARD)==0);
		Assert.assertTrue("The packet was forwarded to the wrong interface", !result.getIface().isInternal());
		
		result = firewall.onReceivedPacket(p, firewall.getExternalInterface());
		Assert.assertTrue("The packet was not forwarded", result.getAction().compareTo(Action.FORWARD)==0);
		Assert.assertTrue("The packet was forwarded to the wrong interface", result.getIface().isInternal());
		
		System.out.println("Test empty acl passed");
	}
	
	@Test
	public void testAcl(){
		
		boolean res = firewall.addAclRule("10.0.0.1", "8.8.8.8");
		Assert.assertTrue("Rule was not added", res);
		
		RoutingResult result = firewall.onReceivedPacket(p, firewall.getInternalInterface());
		Assert.assertTrue("Packet from internal interface was forwarded", result.getAction().compareTo(Action.DROP)==0);

		result = firewall.onReceivedPacket(p, firewall.getExternalInterface());
		Assert.assertTrue("Packet from external interface was forwarded", result.getAction().compareTo(Action.DROP)==0);
		
		res = firewall.removeAclRule("10.0.0.1", "8.8.8.7");
		Assert.assertFalse("Rule was removed", res);
		
		p.setField(PacketField.IP_DST, "8.8.8.7");
		result = firewall.onReceivedPacket(p, firewall.getInternalInterface());
		Assert.assertTrue("The packet was not forwarded", result.getAction().compareTo(Action.FORWARD)==0);
		Assert.assertTrue("The packet was forwarded to the wrong interface", !result.getIface().isInternal());
		
		result = firewall.onReceivedPacket(p, firewall.getExternalInterface());
		Assert.assertTrue("The packet was not forwarded", result.getAction().compareTo(Action.FORWARD)==0);
		Assert.assertTrue("The packet was forwarded to the wrong interface", result.getIface().isInternal());
		
		p.setField(PacketField.IP_DST, "8.8.8.8");
	
		res = firewall.removeAclRule("10.0.0.1", "8.8.8.8");
		Assert.assertTrue("Rule was not removed", res);
		
		result = firewall.onReceivedPacket(p, firewall.getInternalInterface());
		Assert.assertTrue("The packet was not forwarded", result.getAction().compareTo(Action.FORWARD)==0);
		Assert.assertTrue("The packet was forwarded to the wrong interface", !result.getIface().isInternal());
		
		result = firewall.onReceivedPacket(p, firewall.getExternalInterface());
		Assert.assertTrue("The packet was not forwarded", result.getAction().compareTo(Action.FORWARD)==0);
		Assert.assertTrue("The packet was forwarded to the wrong interface", result.getIface().isInternal());
		
		System.out.println("Test acl passed");
	}
	

}
