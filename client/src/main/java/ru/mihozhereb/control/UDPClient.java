package ru.mihozhereb.control;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

public class UDPClient {
    private final DatagramSocket ds;
    private static final ReentrantLock locker = new ReentrantLock();

    public UDPClient(String address, int port) throws SocketException {
        ds = new DatagramSocket();
//        ds.connect(new InetSocketAddress("helios.cs.ifmo.ru", port));
        ds.connect(new InetSocketAddress(address, port));
        ds.setSoTimeout(10000);
    }

    public Response sendRequest(Request req) throws IOException {
        locker.lock();

        String msg = req.toJson();

//        byte[] outMetaBuffer = ByteBuffer.allocate(4).putInt(msg.getBytes().length).array();
//        DatagramPacket outMetaPacket = new DatagramPacket(outMetaBuffer, outMetaBuffer.length);
//        ds.send(outMetaPacket);

//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            throw new IOException(e);
//        }

        byte[] outBuffer = msg.getBytes();
        DatagramPacket outPacket = new DatagramPacket(outBuffer, outBuffer.length);
        ds.send(outPacket);

//        byte[] inMetaBuffer = new byte[4];
//        DatagramPacket metaPacket = new DatagramPacket(inMetaBuffer, inMetaBuffer.length);
//        ds.receive(metaPacket);

//        int size = ByteBuffer.wrap(metaPacket.getData()).getInt();
//        byte[] inBuffer = new byte[size];
        byte[] inBuffer = new byte[64 * 1024];
        DatagramPacket inPacket = new DatagramPacket(inBuffer, inBuffer.length);
        ds.receive(inPacket);

//        String res = new String(inPacket.getData());
        String res = new String(inPacket.getData()).trim();

        Response result;
        try {
            result = Response.fromJson(res);
        } catch (Exception e) {
            throw new IOException(e);
        }

        locker.unlock();

        return result;
    }
}
