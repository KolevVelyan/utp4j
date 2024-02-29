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
            final byte[] bulk = new byte[10 * 1024];
            Arrays.fill(bulk, (byte) 0xAF);

            // final InetSocketAddress local = new
            // InetSocketAddress(InetAddress.getAllByName("145.94.139.225")[0], 12341);
            final InetSocketAddress local = new InetSocketAddress(InetAddress.getLocalHost(), 12341);

            // The Client.
            UtpSocketChannel channel = UtpSocketChannel.open();
            UtpConnectFuture cFut = channel.connect(local);
            cFut.block();
            ByteBuffer buffer = ByteBuffer.allocate(bulk.length);
            UtpReadFuture readFuture = channel.read(buffer);
            readFuture.block();
            channel.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}