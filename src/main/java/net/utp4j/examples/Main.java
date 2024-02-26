package net.utp4j.examples;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

import net.utp4j.channels.UtpSocketChannel;
import net.utp4j.channels.futures.UtpConnectFuture;
import net.utp4j.channels.futures.UtpWriteFuture;

import net.utp4j.channels.UtpServerSocketChannel;
import net.utp4j.channels.futures.UtpAcceptFuture;
import net.utp4j.channels.futures.UtpReadFuture;

public class Main {
    public static void main(String[] args) {
        try {
            // stub data to send
            final byte[] bulk = new byte[1];
            Arrays.fill(bulk, (byte) 0xAF);

            final InetSocketAddress local = new InetSocketAddress(InetAddress.getLocalHost(), 12341);

            // The Server.
            Thread server = new Thread() {
                @Override
                public void run() {
                    try {
                        UtpServerSocketChannel server = UtpServerSocketChannel.open();
                        server.bind(local);
                        UtpAcceptFuture acceptFuture = server.accept();
                        acceptFuture.block();
                        UtpSocketChannel channel = acceptFuture.getChannel();
                        ByteBuffer out = ByteBuffer.allocate(bulk.length);
                        out.put(bulk);
                        // Send data
                        UtpWriteFuture fut = channel.write(out);
                        fut.block();
                        channel.close();
                    } catch (Exception e) {
                        e.printStackTrace(System.err);
                    }
                }
            };
            server.start();

            // The Client.
            UtpSocketChannel channel = UtpSocketChannel.open();
            UtpConnectFuture cFut = channel.connect(local);
            cFut.block();
            ByteBuffer buffer = ByteBuffer.allocate(bulk.length);
            // Receive data. --EVERYTHING BREAKS UP HERE--
            UtpReadFuture readFuture = channel.read(buffer);
            readFuture.block();
            channel.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
