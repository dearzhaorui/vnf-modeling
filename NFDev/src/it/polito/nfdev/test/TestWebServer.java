package it.polito.nfdev.test;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import it.polito.nfdev.lib.Packet;
import it.polito.nfdev.lib.Packet.PacketField;
import it.polito.nfdev.lib.RoutingResult.Action;
import it.polito.nfdev.lib.RoutingResult;
import it.polito.nfdev.webserver.WebServer;


public class TestWebServer {

	private WebServer server;
	private Packet p;

	
	@Before
	public void setup(){
		server = new WebServer();
		p =new Packet();
		p.setField(PacketField.IP_SRC, "10.0.0.1");
		p.setField(PacketField.IP_DST, "8.8.8.8");
		p.setField(PacketField.PORT_SRC, "2000");
		p.setField(PacketField.PORT_DST, "80");
		p.setField(PacketField.APPLICATION_PROTOCOL, Packet.HTTP_REQUEST);
				
	}
	
	@AfterClass
	public static void end() {
		System.out.println("\n+++ All tests executed +++\n");
	}
	
	@Test
	public void testDrop(){
		
		p.setField(PacketField.L7DATA, WebServer.WRONG_URL);
		RoutingResult result = server.onReceivedPacket(p, server.getExternalInterface());
		Assert.assertTrue("Packet was not dropped", result.getAction().compareTo(Action.DROP)==0);
		
		System.out.println("Test drop passed");
	}
	
	@Test
	public void testForward(){
		
		p.setField(PacketField.L7DATA, WebServer.URL);
		RoutingResult result = server.onReceivedPacket(p, server.getExternalInterface());
		Assert.assertTrue("Packet was not forwaded", result.getAction().compareTo(Action.FORWARD)==0);
		Assert.assertTrue("Wrong ip source", result.getPacket().getField(PacketField.IP_SRC).compareTo(p.getField(PacketField.IP_DST))==0);
		Assert.assertTrue("Wrong port source", result.getPacket().getField(PacketField.PORT_SRC).compareTo(p.getField(PacketField.PORT_DST))==0);
		Assert.assertTrue("Wrong ip destination", result.getPacket().getField(PacketField.IP_DST).compareTo(p.getField(PacketField.IP_SRC))==0);
		Assert.assertTrue("Wrong port destination", result.getPacket().getField(PacketField.PORT_DST).compareTo(p.getField(PacketField.PORT_SRC))==0);
		Assert.assertTrue("Worng application protocol", result.getPacket().getField(PacketField.APPLICATION_PROTOCOL).compareTo(Packet.HTTP_RESPONSE)==0);
		
		System.out.println("Test forward passed");
		
	}
}
