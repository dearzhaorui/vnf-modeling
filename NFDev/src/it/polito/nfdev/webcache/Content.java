package it.polito.nfdev.webcache;

import java.net.URL;
import java.util.Date;

public class Content implements Cachable {
	
	private URL url;
	private Date timestamp;
	
	public Content(URL url) {
		this.url = url;
		this.timestamp = new Date();
	}

	@Override
	public byte[] getPayload() {
		return new byte[0];
	}

	@Override
	public URL getUrl() {
		return url;
	}

	@Override
	public Date getTimestamp() {
		return timestamp;
	}

}
