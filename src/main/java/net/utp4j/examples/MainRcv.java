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

public class MainRcv {
    public static void main(String[] args) {
        try {
            // stub data to send
            final byte[] bulk = new byte[1 * 1024 * 1024 * 1024];
            Arrays.fill(bulk, (byte) 0xAF);

            long beforeOpen;
            long beforeRead;
            long afterRead;
            long afterClose;

            // final InetSocketAddress local = new
            // InetSocketAddress(InetAddress.getAllByName("145.94.139.225")[0], 12341);
            // final InetSocketAddress local = new
            // InetSocketAddress(InetAddress.getByName("169.254.13.6"), 12351);
            beforeOpen = System.currentTimeMillis();
            final InetSocketAddress local = new InetSocketAddress(InetAddress.getLocalHost(), 12350);

            // The Client.
            UtpSocketChannel channel = UtpSocketChannel.open();
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
