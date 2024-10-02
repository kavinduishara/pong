package com.example.pong;

import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        String massage="SERVER_NAME=kavindu";
        Pattern p=Pattern.compile("SERVER_NAME=");
        Matcher matcher=p.matcher(massage);

        if(matcher.lookingAt()){
            System.out.println(matcher.group());
            System.out.println();
            System.out.println(Arrays.toString(massage.split("SERVER_NAME=")));
        }
        java.util.Date now = new java.util.Date();
        int i=5;
        while (i>0){
            int y= (int) (-now.getTime()+(new Date()).getTime());
            if(y%1000==0 && y/1000!=i) {
                System.out.println((-now.getTime()+(new java.util.Date()).getTime())/1000);
                i++;
            }
        }
    }
}
