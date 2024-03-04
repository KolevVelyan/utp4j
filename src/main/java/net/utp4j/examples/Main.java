package net.utp4j.examples;

import static net.utp4j.channels.UtpSocketState.CLOSED;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

import net.utp4j.channels.UtpSocketChannel;
import net.utp4j.channels.futures.UtpConnectFuture;
import net.utp4j.channels.futures.UtpWriteFuture;
import net.utp4j.channels.impl.UtpSocketChannelImpl;
import net.utp4j.channels.UtpServerSocketChannel;
import net.utp4j.channels.futures.UtpAcceptFuture;
import net.utp4j.channels.futures.UtpReadFuture;

public class Main {
    public static void main(String[] args) {
        try {
            // stub data to send
            final byte[] bulk = new byte[1 * 1024 * 1024 * 1024];
            Arrays.fill(bulk, (byte) 0xAF);

            System.out.println(InetAddress.getLocalHost());
            final InetSocketAddress local = new InetSocketAddress(InetAddress.getLocalHost(), 12342);

            // The Server.
            Thread server = new Thread() {
                @Override
                public void run() {
                    long beforeOpenTime;
                    long beforeWriteTime;
                    long afterWriteTime;
                    long afterCloseTime;
                    try {
                        beforeOpenTime = System.currentTimeMillis();
                        UtpServerSocketChannel server = UtpServerSocketChannel.open();
                        server.bind(local);
                        UtpAcceptFuture acceptFuture = server.accept();
                        acceptFuture.block();
                        UtpSocketChannel channel = acceptFuture.getChannel();
                        ByteBuffer out = ByteBuffer.allocate(bulk.length);
                        out.put(bulk);
                        // Send data
                        beforeWriteTime = System.currentTimeMillis();
                        UtpWriteFuture fut = channel.write(out);
                        fut.block();
                        afterWriteTime = System.currentTimeMillis();
                        channel.close();
                        afterCloseTime = System.currentTimeMillis();

                        System.out.println("Open time SV: " + (beforeWriteTime - beforeOpenTime));
                        System.out.println("Write time SV: " + (afterWriteTime - beforeWriteTime));
                        System.out.println("Close time SV: " + (afterCloseTime - afterWriteTime));
                    } catch (Exception e) {
                        e.printStackTrace(System.err);
                    }
                }
            };
            server.start();

            long beforeOpen;
            long beforeRead;
            long afterRead;
            long afterClose;

            // The Client.
            // UtpSocketChannel channel = UtpSocketChannel.open();

            beforeOpen = System.currentTimeMillis();
            UtpSocketChannelImpl c = new UtpSocketChannelImpl();
            try {
                c.setDgSocket(new DatagramSocket(12221));
                c.setState(CLOSED);
            } catch (IOException exp) {
                throw new IOException("Could not open UtpSocketChannel: "
                        + exp.getMessage());
            }
            UtpSocketChannel channel = c;

            UtpConnectFuture cFut = channel.connect(local);
            cFut.block();
            ByteBuffer buffer = ByteBuffer.allocate(bulk.length);
            beforeRead = System.currentTimeMillis();
            UtpReadFuture readFuture = channel.read(buffer);
            readFuture.block();
            afterRead = System.currentTimeMillis();
            channel.close();
            afterClose = System.currentTimeMillis();

            System.out.println("Open time CL: " + (beforeRead - beforeOpen));
            System.out.println("Read time CL: " + (afterRead - beforeRead));
            System.out.println("Close time CL: " + (afterClose - afterRead));
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
