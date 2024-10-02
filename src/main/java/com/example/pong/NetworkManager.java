package com.example.pong;

import javafx.scene.control.ListView;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NetworkManager {
    static List<Thread> threads=new ArrayList<>();
    static boolean key=true;
    static boolean requestToJoin=false;
    static final int LOOKING_FOR_HOST=1;
    public static void listeningAsAHost(String name,ListView<String> list, List<InetAddress> IpList){
        key=true;
        Runnable runnable= ()->{
            while (key){
                DatagramPacket datagramPacket=UDP.receive();
                if(datagramPacket==null){
                    continue;
                }

                String massage=new String(datagramPacket.getData(),0,datagramPacket.getLength());

                if(massage.equals(String.valueOf(NetworkManager.LOOKING_FOR_HOST))){
                    String payload="SERVER_NAME="+name;
                    UDP.send(String.valueOf(datagramPacket.getAddress()),payload.getBytes());
                }else{
                    Pattern p=Pattern.compile("CLIENT_NAME=");
                    Matcher matcher=p.matcher(massage);
                    if (matcher.lookingAt() && !IpList.contains(datagramPacket.getAddress())){
                        IpList.add(datagramPacket.getAddress());
                        list.getItems().add(massage.substring(matcher.end()));
                    }
                }


            }
        };
        Thread thread=new Thread(runnable);
        thread.start();
        threads.add(thread);
    }
    public static void listeningAsAClient(ListView<String> list, List<InetAddress> IpList){
        key=true;
        Runnable runnable= ()->{
            while (key){
                DatagramPacket datagramPacket=UDP.receive();
                if(datagramPacket==null){
                    continue;
                }
                String massage=new String(datagramPacket.getData(),0,datagramPacket.getLength());
                Pattern p=Pattern.compile("SERVER_NAME=");
                Matcher matcher=p.matcher(massage);
                if(matcher.lookingAt()){
                    if(!IpList.contains(datagramPacket.getAddress())){
                        IpList.add(datagramPacket.getAddress());
                        list.getItems().add(massage.substring(matcher.end()));
                    }
                }
            }
        };
        Thread thread=new Thread(runnable);
        thread.start();
        threads.add(thread);
    }
    public static void lookingForHost(){
        key=true;
        Runnable runnable=()->{
            while (key && !requestToJoin){
                String payload=String.valueOf(NetworkManager.LOOKING_FOR_HOST);
                UDP.send("255.255.255.255", payload.getBytes());
            }
        };
        Thread thread=new Thread(runnable);
        thread.start();
        threads.add(thread);
    }
    public static String requestToJoin(String name,InetAddress ip){
        requestToJoin=true;
        key=true;
        final String[] playSide = {""};
        Runnable runnable=()->{
            while (key && requestToJoin){
                String payload="CLIENT_NAME="+name;
                UDP.send(String.valueOf(ip), payload.getBytes());
            }
        };
        Runnable runnable1=()->{
            while (key && requestToJoin){
                DatagramPacket datagramPacket=UDP.receive();
                playSide[0] = new String(datagramPacket.getData(),0,datagramPacket.getLength());
            }
        };
        Thread thread=new Thread(runnable);
        thread.start();
        threads.add(thread);
        Thread thread1=new Thread(runnable1);
        thread1.start();
        threads.add(thread1);
        return playSide[0];
    }
    public static void cancelJoin(){
        for(int i=0;i<2;i++){
            Thread thread=threads.remove(threads.size()-1);
            thread.interrupt();
        }
        requestToJoin=false;
    }
    public static void stop(){
        key=false;
        threads.forEach(Thread::interrupt);
        threads.clear();
        UDP.datagramSocket.close();
    }
}
