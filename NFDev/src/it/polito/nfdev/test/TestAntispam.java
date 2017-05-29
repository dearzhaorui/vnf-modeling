package it.polito.nfdev.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import it.polito.nfdev.antispam.Antispam;
import it.polito.nfdev.lib.Interface;
import it.polito.nfdev.lib.Packet;
import it.polito.nfdev.lib.RoutingResult;
import it.polito.nfdev.lib.Packet.PacketField;
import it.polito.nfdev.lib.RoutingResult.Action;

public class TestAntispam {
	
	private Antispam antiSpam;
	private Interface incomingInterface;
	
	
	@Before
	public void setup(){
		List<String> keywords = new ArrayList<>();
		
		keywords.add("AFFARE");
		keywords.add("GUADAGNO");
		keywords.add("ISCRIVITI");
		
		
		incomingInterface = new Interface(1,Interface.Type.EXTERNAL);
		antiSpam = new Antispam(keywords);
	}
	
	@AfterClass
	public static void end() {
		System.out.println("\n+++ All tests executed +++\n");
	}
	
	@Test
	public void testRequest(){
		Packet p = new Packet();
		p.setField(PacketField.APPLICATION_PROTOCOL, Packet.POP3_REQUEST);
		p.setField(PacketField.L7DATA, "AFFARE");
		
		RoutingResult result = antiSpam.onReceivedPacket(p, incomingInterface);
		Assert.assertTrue("Antispam did not forward the packet.", result.getAction().equals(Action.FORWARD));	
		Assert.assertTrue("Antispam keyword was not removed.",antiSpam.removeKeyword("AFFARE"));
		
		result = antiSpam.onReceivedPacket(p, incomingInterface);
		Assert.assertTrue("Antispam did not forward the packet.", result.getAction().equals(Action.FORWARD));	
		Assert.assertTrue("Antispam keyword was not inserted.",antiSpam.addKeyword("AFFARE"));
		System.out.println("Request test passed");
	}
	
	@Test
	public void testResponse(){
		Packet p = new Packet();
		p.setField(PacketField.APPLICATION_PROTOCOL, Packet.POP3_RESPONSE);
		p.setField(PacketField.L7DATA, "AFFARE");
		
		RoutingResult result = antiSpam.onReceivedPacket(p, incomingInterface);
		Assert.assertTrue("Antispam did forward the packet.", result.getAction().equals(Action.DROP));
		Assert.assertTrue("Antispam keyword was not removed.",antiSpam.removeKeyword("AFFARE"));
		
		result = antiSpam.onReceivedPacket(p, incomingInterface);
		Assert.assertTrue("Antispam did not forward the packet.", result.getAction().equals(Action.FORWARD));
		Assert.assertTrue("Antispam keyword was not inserted.",antiSpam.addKeyword("AFFARE"));
		
		p.setField(PacketField.L7DATA, "CAMOMILLA");
		result = antiSpam.onReceivedPacket(p, incomingInterface);
		Assert.assertTrue("Antispam did not forward the packet.", result.getAction().equals(Action.FORWARD));
		
		p.setField(PacketField.APPLICATION_PROTOCOL, Packet.HTTP_REQUEST);
		result = antiSpam.onReceivedPacket(p, incomingInterface);
		Assert.assertTrue("Antispam did forward the packet.", result.getAction().equals(Action.DROP));
		
		System.out.println("Response test passed");
	}
	

}
