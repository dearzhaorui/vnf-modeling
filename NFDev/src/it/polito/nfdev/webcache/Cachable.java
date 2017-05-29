package it.polito.nfdev.webcache;

import java.net.URL;
import java.util.Date;

public interface Cachable {
	
	public URL getUrl();
	public byte[] getPayload();
	public Date getTimestamp();
	
}
