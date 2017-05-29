package it.polito.nfdev.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import it.polito.nfdev.lib.Interface;
import it.polito.nfdev.lib.Packet;
import it.polito.nfdev.lib.RoutingResult;
import it.polito.nfdev.lib.Packet.PacketField;
import it.polito.nfdev.lib.RoutingResult.Action;
import it.polito.nfdev.nat.Nat;

import org.junit.AfterClass;
import org.junit.Assert;

public class TestNat {
	
	private Nat nat;
	private Interface internalFace;
	private Interface externalFace;
	private final String natIp = "130.192.225.180";
	private final Integer timeout = 1;
	
	@Before
	public void setup() {
		List<Interface> interfaces = new ArrayList<>();
		internalFace = new Interface(1,Interface.Type.INTERNAL);
		internalFace.addAttribute("INTERNAL");
		externalFace = new Interface(2, Interface.Type.EXTERNAL);
		externalFace.addAttribute("EXTERNAL");
		interfaces.add(internalFace);
		interfaces.add(externalFace);
		nat = new Nat(interfaces, natIp, timeout);
	}
	
	@AfterClass
	public static void end() {
		System.out.println("\n+++ All tests executed +++\n");
	}
	
	@Test
	public void testSameFlow() {
		Packet p1 = new Packet();
		p1.setField(PacketField.IP_SRC, "10.0.0.1");
		p1.setField(PacketField.IP_DST, "8.8.8.8");
		p1.setField(PacketField.PORT_SRC, "2000");
		p1.setField(PacketField.PORT_DST, "80");
		RoutingResult result = nat.onReceivedPacket(p1, internalFace);
		Assert.assertTrue("Nat did not forward the packet.", result.getAction().equals(Action.FORWARD));
		Assert.assertTrue("Nat forwarded the packet on the wrong face", !result.getIface().isInternal());
		
		RoutingResult result2 = nat.onReceivedPacket(p1, internalFace);
		Assert.assertTrue("Nat did not forward the packet.", result.getAction().equals(Action.FORWARD));
		Assert.assertTrue("Nat forwarded the packet on the wrong face", !result.getIface().isInternal());
		Assert.assertTrue("Same flow does not work properly.", result2.getPacket().getField(PacketField.PORT_SRC).equals(result.getPacket().getField(PacketField.PORT_SRC)));
		Assert.assertTrue("Same flow does not work properly.", result2.getPacket().getField(PacketField.IP_SRC).equals(result.getPacket().getField(PacketField.IP_SRC)));
		System.out.println("Test same flow passed");
	}
	
	@Test
	public void testForwardRequest() {
		Packet p1 = new Packet();
		p1.setField(PacketField.IP_SRC, "10.0.0.1");
		p1.setField(PacketField.IP_DST, "8.8.8.8");
		p1.setField(PacketField.PORT_SRC, "1000");
		p1.setField(PacketField.PORT_DST, "80");
		RoutingResult result = nat.onReceivedPacket(p1, internalFace);
		Assert.assertTrue("Nat did not forward the packet.", result.getAction().equals(Action.FORWARD));
		Assert.assertTrue("Nat forwarded the packet on the wrong face", !result.getIface().isInternal());
		
		Packet p2 = new Packet();
		p2.setField(PacketField.IP_SRC, "10.0.0.2");
		p2.setField(PacketField.IP_DST, "8.8.8.8");
		p2.setField(PacketField.PORT_SRC, "1000");
		p2.setField(PacketField.PORT_DST, "80");
		RoutingResult result2 = nat.onReceivedPacket(p2, internalFace);
		Assert.assertTrue("Nat did not forward the packet.", result2.getAction().equals(Action.FORWARD));
		Assert.assertTrue("Two flows are mapped into the same port!", !result.getPacket().getField(PacketField.PORT_SRC).equals(result2.getPacket().getField(PacketField.PORT_SRC)));
		Assert.assertTrue("Nat forwarded the packet on the wrong face", !result2.getIface().isInternal());
		Assert.assertTrue("Wrong port", result2.getPacket().getField(PacketField.PORT_DST).equals(p2.getField(PacketField.PORT_DST)));
		Assert.assertTrue("IP not masqueraded", result2.getPacket().getField(PacketField.IP_SRC).equals(natIp));
		System.out.println("Test forward request passed");
	}
	
	@Test
	public void testForwardResponse() {
		Packet p1 = new Packet();
		p1.setField(PacketField.IP_SRC, "10.0.0.1");
		p1.setField(PacketField.IP_DST, "8.8.8.8");
		p1.setField(PacketField.PORT_SRC, "1000");
		p1.setField(PacketField.PORT_DST, "80");
		RoutingResult result = nat.onReceivedPacket(p1, internalFace);
		Assert.assertTrue("Nat did not forward the packet.", result.getAction().equals(Action.FORWARD));
		Assert.assertTrue("Nat forwarded the packet on the wrong face", !result.getIface().isInternal());
		Assert.assertNotNull("Packet is null!", result.getPacket());
		
		Packet p2 = new Packet();
		p2.setField(PacketField.IP_SRC, p1.getField(PacketField.IP_DST));
		p2.setField(PacketField.IP_DST, result.getPacket().getField(PacketField.IP_SRC));
		p2.setField(PacketField.PORT_SRC, p1.getField(PacketField.PORT_DST));
		p2.setField(PacketField.PORT_DST, result.getPacket().getField(PacketField.PORT_SRC));
		result = nat.onReceivedPacket(p2, externalFace);
		Assert.assertNotNull("Packet is null!", result.getPacket());
		Assert.assertTrue("Nat did not forward the packet.", result.getAction().equals(Action.FORWARD));
		Assert.assertTrue("Nat forwarded the packet on the wrong face", result.getIface().isInternal());
		Assert.assertTrue("Wrong DST IP", result.getPacket().getField(PacketField.IP_DST).equals(p1.getField(PacketField.IP_SRC)));
		Assert.assertTrue("Wrong SRC IP", result.getPacket().getField(PacketField.IP_SRC).equals(p1.getField(PacketField.IP_DST)));
		Assert.assertTrue("Wrong DST PORT", result.getPacket().getField(PacketField.PORT_DST).equals(p1.getField(PacketField.PORT_SRC)));
		Assert.assertTrue("Wrong SRC PORT", result.getPacket().getField(PacketField.PORT_DST).equals(p1.getField(PacketField.PORT_SRC)));
		System.out.println("Test forward response passed");
	}
	
	@Test
	public void testTimeout() throws Exception {
		Packet p1 = new Packet();
		p1.setField(PacketField.IP_SRC, "10.0.0.1");
		p1.setField(PacketField.IP_DST, "8.8.8.8");
		p1.setField(PacketField.PORT_SRC, "1000");
		p1.setField(PacketField.PORT_DST, "80");
		RoutingResult result = nat.onReceivedPacket(p1, internalFace);
		Assert.assertTrue("Nat did not forward the packet.", result.getAction().equals(Action.FORWARD));
		Assert.assertTrue("Nat forwarded the packet on the wrong face", !result.getIface().isInternal());
		
		// timeout is in seconds so we convert it
		Thread.sleep((timeout*1000) + 1);
		nat.checkForTimeout();
		
		Packet p3 = new Packet();
		p3.setField(PacketField.IP_SRC, "8.8.8.8");
		p3.setField(PacketField.IP_DST, natIp);
		p3.setField(PacketField.PORT_SRC, "80");
		p3.setField(PacketField.PORT_DST, "10000");
		result = nat.onReceivedPacket(p3, externalFace);
		Assert.assertTrue("Nat did not drop the packet.", result.getAction().equals(Action.DROP));
		Assert.assertNull("Packet is different from null.", result.getPacket());
		Assert.assertNull("Interface is different from null.", result.getIface());
		
		System.out.println("Test timeout passed");
	}

}
