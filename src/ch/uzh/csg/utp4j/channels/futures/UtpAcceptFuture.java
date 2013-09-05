package ch.uzh.csg.utp4j.channels.futures;

import ch.uzh.csg.utp4j.channels.UtpSocketChannel;
import ch.uzh.csg.utp4j.channels.impl.UtpSocketChannelImpl;

public class UtpAcceptFuture extends UtpBlockableFuture {

	protected volatile UtpSocketChannelImpl channel = null;
	
	public UtpSocketChannel getChannel() {
		return channel;
	}

}
