package net.utp4j.examples;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

import net.utp4j.channels.UtpSocketChannel;
import net.utp4j.channels.futures.UtpConnectFuture;
import net.utp4j.channels.futures.UtpWriteFuture;

import net.utp4j.channels.UtpServerSocketChannel;
import net.utp4j.channels.futures.UtpAcceptFuture;
import net.utp4j.channels.futures.UtpReadFuture;

public class MainSend {
    public static void main(String[] args) {
        try {
            // stub data to send
            final byte[] bulk = new byte[1 * 1024 * 1024 * 1024];
            Arrays.fill(bulk, (byte) 0xAF);

            long beforeOpenTime;
            long beforeWriteTime;
            long afterWriteTime;
            long afterCloseTime;
            // 1752 bytes per packets

            // final InetSocketAddress local = new
            // InetSocketAddress(InetAddress.getAllByName("145.94.139.225")[0], 12341);
            // final InetSocketAddress local = new
            // InetSocketAddress(InetAddress.getByName("145.94.188.69"), 12351);
            final InetSocketAddress local = new InetSocketAddress(InetAddress.getLocalHost(), 12350);

            // The Server.
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
                server.close();
                afterCloseTime = System.currentTimeMillis();

                System.out.println("Open time SV: " + (beforeWriteTime - beforeOpenTime));
                System.out.println("Write time SV: " + (afterWriteTime - beforeWriteTime));
                System.out.println("Close time SV: " + (afterCloseTime - afterWriteTime));
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}
