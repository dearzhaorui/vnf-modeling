package it.polito.nfdev.nat;

import java.util.LinkedList;
import java.util.Queue;

public class PortPool {
	
	private Queue<Integer> availablePorts;
	private Integer maxPorts;
	
	public PortPool(Integer startingPort, Integer numberOfPorts) {
		assert startingPort > 1024;
		assert numberOfPorts > 0;
		this.availablePorts = new LinkedList<>();  //Queue is an interface, can not be instance, so must use linkedlist
		this.maxPorts = numberOfPorts;
		for(int i=startingPort; i<startingPort+maxPorts; i++)
			this.availablePorts.offer(i);
	}
	
	public Integer getAvailablePort() {
		if(availablePorts.size() == 0)
			return null;
		return availablePorts.poll();  // delete and return first element from Queue  (FIFO)
	}
	
	public void freePort(Integer port) {
		assert availablePorts.size() < maxPorts;
		boolean result = availablePorts.offer(port);
		assert result == true;  // return false if queue is full;
	}

}
