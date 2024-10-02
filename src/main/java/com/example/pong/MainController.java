package com.example.pong;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

public class MainController {
    String name="kavindu";
    @FXML
    Pane home;
    @FXML
    Pane create;
    @FXML
    Pane find;
    @FXML
    ListView<String> findersList;
    @FXML
    ListView<String> creatorsList;
    List<InetAddress> findersIpList=new LinkedList<>();
    List<InetAddress> creatorsIpList=new LinkedList<>();
    InetAddress teamMateIp;
    String teamMateName;
    @FXML
    Pane joining;
    String playSide;

    @FXML
    protected void playSolo(ActionEvent event){
        game(event);
    }
    private void game(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
            Scene scene = new Scene(loader.load(),600,400);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            Game game=loader.getController();
            game.ip=teamMateIp;

            scene.setOnKeyPressed(keyEvent -> {
                switch (keyEvent.getCode()) {
                    case W -> game.moveUp();
                    case S -> game.moveDown();
                }
            });
            stage.show();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
    @FXML
    protected void creating(){

        home.setVisible(false);
        create.setVisible(true);
        NetworkManager.listeningAsAHost(name,findersList,findersIpList);
        findersList.getSelectionModel().selectedItemProperty().addListener((observableValue, string, t1) -> {
            teamMateIp=findersIpList.get(findersList.getSelectionModel().getSelectedIndex());
            teamMateName=findersList.getSelectionModel().getSelectedItem();
        });

    }
    @FXML
    protected void finding(){
        home.setVisible(false);
        find.setVisible(true);
        NetworkManager.lookingForHost();
        NetworkManager.listeningAsAClient(creatorsList,creatorsIpList);
        creatorsList.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> observableValue, String string, String t1)-> {
                teamMateIp=creatorsIpList.get(creatorsList.getSelectionModel().getSelectedIndex());
                teamMateName=creatorsList.getSelectionModel().getSelectedItem();
        });
    }
    @FXML
    protected void joinMe(){
        if(teamMateIp==null){
            return;
        }
        joining.setVisible(true);
        playSide=NetworkManager.requestToJoin(name,teamMateIp);
    }
    @FXML
    protected void cancelJoin(){
        NetworkManager.cancelJoin();
    }
    @FXML
    protected void acceptIt(ActionEvent event){
        playSide=0.5>Math.random()?"right":"left";
        UDP.send(String.valueOf(teamMateIp),playSide.getBytes());
        game(event);
    }
    @FXML
    protected void home(){
        NetworkManager.stop();
        findersList.getSelectionModel().clearSelection();
        creatorsList.getSelectionModel().clearSelection();
        creatorsIpList.clear();
        findersIpList.clear();
        home.setVisible(true);
        find.setVisible(false);
        create.setVisible(false);
    }
}