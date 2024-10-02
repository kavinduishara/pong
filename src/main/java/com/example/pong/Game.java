package com.example.pong;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;


public class Game implements Initializable {

    @FXML
    protected AnchorPane playground;
    @FXML
    protected AnchorPane gamescreen;

    @FXML
    protected Circle ball;

    @FXML
    protected Rectangle rightsidebar;

    @FXML
    protected Rectangle leftsidebar;
    private final int velY=10;
    private int ballVelX=3;
    private int ballVelY= 3;
    private Timeline timeline;
    protected InetAddress ip;
    Thread recieveThread;
    boolean run;
    String playSide="";
    boolean iamready=false;
    boolean teamready=false;
    @FXML
    Label countdown;
    @FXML
    Pane wait;
    @FXML
    Label score;
    @FXML
    Label scoreLeft;
    @FXML
    Label scoreRight;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        timeline = new Timeline(new KeyFrame(Duration.millis(1000 / 60.0), e -> {
            ball.setLayoutX(ball.getLayoutX()+ballVelX);
            ball.setLayoutY(ball.getLayoutY()+ballVelY);

            if(ball.getLayoutY()<0 || ball.getLayoutY()> playground.getHeight()){
                ballVelY*=-1;
            }else if(ball.getLayoutX()> playground.getWidth()-10){
                if(ball.getLayoutY()>rightsidebar.getLayoutY() && ball.getLayoutY()<rightsidebar.getLayoutY()+rightsidebar.getHeight()){
                    ballVelX*=-1;
                }
                else {
                    ball.setLayoutX(playground.getWidth()/2);
                    ball.setLayoutY(playground.getHeight()/2);
                }
            }else if(ball.getLayoutX()<10){
                if(ball.getLayoutY()>leftsidebar.getLayoutY() && ball.getLayoutY()<leftsidebar.getLayoutY()+leftsidebar.getHeight()){
                    ballVelX*=-1;
                }
                else {
                    ball.setLayoutX(playground.getWidth()/2);
                    ball.setLayoutY(playground.getHeight()/2);
                }
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        if(ip==null){
            teamready=true;
        }else {

        }
        wait.setVisible(true);

    }
    protected void moveUp(){
        if(ip!=null){
            UDP.send(String.valueOf(ip),"w".getBytes());
        }
        if(rightsidebar.getLayoutY()>0 && !Objects.equals(playSide, "left")){
            rightsidebar.setLayoutY(rightsidebar.getLayoutY()-velY);
        }
        if(leftsidebar.getLayoutY()>0 && !Objects.equals(playSide, "right")){
            leftsidebar.setLayoutY(leftsidebar.getLayoutY()-velY);
        }
    }
    protected void moveDown(){
        if(ip!=null){
            UDP.send(String.valueOf(ip),"s".getBytes());
        }
        if(rightsidebar.getLayoutY()+ rightsidebar.getHeight()<playground.getHeight() && !Objects.equals(playSide, "left")){
            rightsidebar.setLayoutY(rightsidebar.getLayoutY()+velY);
        }
        if(leftsidebar.getLayoutY()+ leftsidebar.getHeight()<playground.getHeight() && !Objects.equals(playSide, "right")){
            leftsidebar.setLayoutY(leftsidebar.getLayoutY()+velY);
        }
    }
    protected void start(){
        if(teamready && iamready){
            AtomicInteger i=new AtomicInteger(3);
            Timeline timeline1=new Timeline(new KeyFrame(Duration.millis(1000),e -> {
                countdown.setText("game stats in "+i.getAndDecrement());
                if(i.get()<0){
                    wait.setVisible(false);
                    timeline.play();
                    if(ip!=null){
                        receive();
                    }
                }
            }));
            timeline1.setCycleCount(i.get()+1);
            timeline1.play();
        }
    }
    @FXML
    protected void ready(){
        iamready=true;
        UDP.send(String.valueOf(ip),"ready".getBytes());
        start();
    }
    protected void receive(){
        Runnable runnable=()->{
            while (run){
                DatagramPacket datagramPacket=UDP.receive();
                if(datagramPacket==null){
                    continue;
                }
                String massage=new String(datagramPacket.getData(),0,datagramPacket.getLength());
                if(datagramPacket.getAddress()==ip){
                    if(massage.equals("w")){
                        if(rightsidebar.getLayoutY()>0 && Objects.equals(playSide, "left")){
                            rightsidebar.setLayoutY(rightsidebar.getLayoutY()-velY);
                        }
                        else if(leftsidebar.getLayoutY()>0 && Objects.equals(playSide, "right")){
                            leftsidebar.setLayoutY(leftsidebar.getLayoutY()-velY);
                        }
                    }else if(massage.equals("s")){
                        if(rightsidebar.getLayoutY()+ rightsidebar.getHeight()<playground.getHeight() && !Objects.equals(playSide, "left")){
                            rightsidebar.setLayoutY(rightsidebar.getLayoutY()+velY);
                        }
                        else if(leftsidebar.getLayoutY()+ leftsidebar.getHeight()<playground.getHeight() && !Objects.equals(playSide, "right")){
                            leftsidebar.setLayoutY(leftsidebar.getLayoutY()+velY);
                        }
                    }else if(massage.equals("ready")){
                        teamready=true;
                    }
                }
            }

        };
        recieveThread=new Thread(runnable);
        recieveThread.start();
    }
    @FXML
    protected void goBack(ActionEvent event){
        run=false;
        recieveThread.interrupt();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        Scene scene;
        try {
            scene = new Scene(loader.load(),600,400);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setResizable(false);
    }

}
