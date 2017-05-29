package it.polito.nfdev.test;

import org.junit.Before;
import org.junit.Test;

import it.polito.nfdev.lib.Interface;
import it.polito.nfdev.lib.Packet;
import it.polito.nfdev.lib.Packet.PacketField;
import it.polito.nfdev.lib.RoutingResult.Action;
import it.polito.nfdev.lib.RoutingResult;
import it.polito.nfdev.webcache.WebCache;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;

public class TestWebCache {
	
	private Interface internalFace;
	private Interface externalFace;
	private WebCache webCache;
	
	@Before
	public void setup() {	
		List<Interface> interfaces = new ArrayList<>();
		webCache = new WebCache(interfaces);
		internalFace = webCache.getInternalInterface();//new Interface(1,Interface.Type.INTERNAL);
		internalFace.addAttribute("INTERNAL");
		externalFace = webCache.getExternalInterface();//new Interface(2,Interface.Type.EXTERNAL);
		externalFace.addAttribute("EXTERNAL");
		//interfaces.add(internalFace);
		//interfaces.add(externalFace);
		
	}
	
	@AfterClass
	public static void end() {
		System.out.println("\n+++ All tests executed +++\n");
	}
	
	@Test
	public void testForwardRequest()
	{
		Packet p1 = new Packet();
		p1.setField(PacketField.IP_SRC, "10.0.0.1");
		p1.setField(PacketField.IP_DST, "8.8.8.8");
		p1.setField(PacketField.PORT_SRC, "2000");
		p1.setField(PacketField.PORT_DST, "80");
		p1.setField(PacketField.APPLICATION_PROTOCOL, Packet.HTTP_REQUEST);
		p1.setField(PacketField.L7DATA, "http://www.google.it");
		RoutingResult result = webCache.onReceivedPacket(p1, internalFace);
		Assert.assertTrue("WebCache did not forward the packet.", result.getAction().equals(Action.FORWARD));
		Assert.assertTrue("WebCache forwarded the packet on the wrong face", result.getIface().getId().equals(externalFace.getId()));
		
		System.out.println("Test forward request passed");
	}
	
	@Test
	public void testForwardResponse()
	{
		Packet p1 = new Packet();
		p1.setField(PacketField.IP_SRC, "8.8.8.8");
		p1.setField(PacketField.IP_DST, "10.0.0.1");
		p1.setField(PacketField.PORT_SRC, "80");
		p1.setField(PacketField.PORT_DST, "2000");
		p1.setField(PacketField.APPLICATION_PROTOCOL, Packet.HTTP_RESPONSE);
		p1.setField(PacketField.L7DATA, "http://www.google.it");
		RoutingResult result = webCache.onReceivedPacket(p1, externalFace);
		Assert.assertTrue("WebCache did not forward the packet.", result.getAction().equals(Action.FORWARD));
		Assert.assertTrue("WebCache forwarded the packet on the wrong face", result.getIface().getId().equals(internalFace.getId()));
		
		System.out.println("Test forward response passed");
	}
	
	@Test
	public void testCacheHasContent()
	{
		Packet p1 = new Packet();
		p1.setField(PacketField.IP_SRC, "8.8.8.8");
		p1.setField(PacketField.IP_DST, "10.0.0.1");
		p1.setField(PacketField.PORT_SRC, "80");
		p1.setField(PacketField.PORT_DST, "2000");
		p1.setField(PacketField.APPLICATION_PROTOCOL, Packet.HTTP_RESPONSE);
		p1.setField(PacketField.L7DATA, "http://www.google.it");
		RoutingResult result = webCache.onReceivedPacket(p1, externalFace);
		Assert.assertTrue("WebCache did not forward the packet.", result.getAction().equals(Action.FORWARD));
		Assert.assertTrue("WebCache forwarded the packet on the wrong face", result.getIface().getId().equals(internalFace.getId()));
		
		Packet p2 = new Packet();
		p2.setField(PacketField.IP_SRC, "10.0.0.1");
		p2.setField(PacketField.IP_DST, "8.8.8.8");
		p2.setField(PacketField.PORT_SRC, "2000");
		p2.setField(PacketField.PORT_DST, "80");
		p2.setField(PacketField.APPLICATION_PROTOCOL, Packet.HTTP_REQUEST);
		p2.setField(PacketField.L7DATA, "http://www.google.it");
		result = webCache.onReceivedPacket(p2, internalFace);
		Assert.assertTrue("WebCache did not forward the packet.", result.getAction().equals(Action.FORWARD));
		Assert.assertTrue("WebCache forwarded the packet on the wrong face", result.getIface().getId().equals(internalFace.getId()));
		
		System.out.println("Test cache has content passed");
	}

}
