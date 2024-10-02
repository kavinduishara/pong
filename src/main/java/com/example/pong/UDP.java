package com.example.pong;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDP {
    final static int PORT=1234;
    static DatagramSocket datagramSocket = null;
    public static void send(String ip,byte[] bytes){
        try{
            InetAddress inetAddress=InetAddress.getByName(ip);
            DatagramSocket datagramSocket=new DatagramSocket();
            DatagramPacket datagramPacket=new DatagramPacket(bytes, bytes.length,inetAddress,PORT);
            datagramSocket.send(datagramPacket);
            datagramSocket.close();
        }
        catch (IOException ioException){
            throw new RuntimeException(ioException);
        }

    }
    public static DatagramPacket receive(){
        try {
            byte[] bytes=new byte[1024];
            DatagramPacket datagramPacket=new DatagramPacket(bytes, bytes.length);
            datagramSocket=new DatagramSocket(PORT);
            datagramSocket.receive(datagramPacket);
            datagramSocket.close();
            if(datagramPacket.getAddress()==InetAddress.getLocalHost()){
                return datagramPacket;
            }else {
                return null;
            }

        } catch (IOException e) {
            if(e.getMessage().equals("Socket closed")){
                System.out.println("Socket closed");
                return null;
            }
            throw new RuntimeException(e);
        }
    }
}
