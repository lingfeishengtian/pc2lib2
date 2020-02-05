package com.lingfeishengtian.gui;

import com.lingfeishengtian.cli.CommandExecutor;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Optional;

public class GUIStarter extends Application {
    @FXML
    public Text contestStatusTxt;
    @FXML
    public Button setDefaultContestSettingsBtn;
    @FXML
    public TextArea contestLog;
    PrintStream old = System.out;

    public static void main(String[] args) {
        launch(args);
    }

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    PrintStream ps = new PrintStream(baos);
    private CommandExecutor executor;

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("GUIStarter.fxml"));
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setTitle("PC^2PQA");
        primaryStage.show();
    }

    @FXML
    public void initialize() {
        executor = new CommandExecutor();

        disableContestOperations();
        log("Please make sure you're running this from a provided script or with the vm option -Djdk.crypto.KeyAgreement.legacyKDF=true.");
    }

    private void disableContestOperations() {
        setDefaultContestSettingsBtn.setDisable(true);
    }

    private void setOutputToLog() {
        System.setOut(ps);
    }

    private void switchOutputToJavaConsole() {
        System.out.flush();
        System.setOut(old);
    }

    private void updateLog() {
        contestLog.setText(contestLog.getText() + "\n" + baos.toString());
        baos = new ByteArrayOutputStream();
        ps = new PrintStream(baos);
    }

    private void log(String message) {
        ps.println("\n" + message);
        updateLog();
    }

    @FXML
    public void loadContestBin(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose PC^2 Root Directory");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File fileChosen = directoryChooser.showDialog(((Node) actionEvent.getTarget()).getScene().getWindow());

        File bin = new File(fileChosen.getAbsolutePath() + File.separator + "bin");
        setOutputToLog();
        if (bin.exists()) {
            TextInputDialog textInputDialog = new TextInputDialog();
            textInputDialog.setTitle("Contest Passcode");
            textInputDialog.setHeaderText("Enter the existing contest passcode or new contest passcode.");
            Optional<String> str = textInputDialog.showAndWait();
            try {
                executor.executeCommand(new String[]{"load", bin.getAbsolutePath(), str.isPresent() ? str.orElse("No reason error") : ""});
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Contest couldn't be loaded, try again!");
            } finally {
                contestStatusTxt.setText("Loaded " + bin.getAbsolutePath());
                contestStatusTxt.setFill(Color.GREEN);
            }
        } else {
            System.out.println("Invalid bin directory location + " + bin.getAbsolutePath() + ".");
        }
        updateLog();
        switchOutputToJavaConsole();
    }
}
