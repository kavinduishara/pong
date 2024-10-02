package com.example.pong;

import java.net.DatagramPacket;
import java.util.Arrays;

public class OtherMain {
    public static void main(String[] args) {
        while (true){
            DatagramPacket datagramPacket=UDP.receive();
            System.out.println(Arrays.toString(datagramPacket.getData()));
            System.out.println(datagramPacket.getAddress());

        }
    }
}
