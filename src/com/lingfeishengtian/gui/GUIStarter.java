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

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("GUIStarter.fxml"));
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setTitle("PC^2PQA");
        primaryStage.show();
    }
    private CommandExecutor executor;

    @FXML
    public void initialize() {
        executor = new CommandExecutor();

        Console console = new Console(contestLog);
        PrintStream ps = new PrintStream(console, true);
        System.setOut(ps);
        System.setErr(ps);

        disableContestOperations();
        System.out.println("\nPlease make sure you're running this from a provided script or with the vm option -Djdk.crypto.KeyAgreement.legacyKDF=true.");
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.exit(0);
    }

    private void disableContestOperations() {
        setDefaultContestSettingsBtn.setDisable(true);
    }

    private void enableContestOperations() {
        setDefaultContestSettingsBtn.setDisable(false);
    }

    @FXML
    public void loadContestBin(ActionEvent actionEvent) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose PC^2 Root Directory");
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File fileChosen = directoryChooser.showDialog(((Node) actionEvent.getTarget()).getScene().getWindow());

        if (fileChosen != null) {
            File bin = new File(fileChosen.getAbsolutePath() + File.separator + "bin");
            if (bin.exists()) {
                TextInputDialog textInputDialog = new TextInputDialog();
                textInputDialog.setTitle("Contest Passcode");
                textInputDialog.setHeaderText("Enter the existing contest passcode or new contest passcode.");
                Optional<String> str = textInputDialog.showAndWait();
                try {
                    executor.execute(new String[]{"load", bin.getAbsolutePath(), str.isPresent() ? str.orElse("No reason error") : ""}, false);
                    contestStatusTxt.setText("Loaded " + bin.getAbsolutePath());
                    contestStatusTxt.setFill(Color.GREEN);
                    enableContestOperations();
                } catch (Exception e) {
                    System.out.println("Contest couldn't be loaded, try again!");
                    e.printStackTrace();
                    System.out.println("It looks like the contest password is incorrect or your bin path is incorrect. Unlikely, but your contest configuration could also be corrupted.");
                }
            } else {
                System.out.println("Invalid bin directory location " + bin.getAbsolutePath());
            }
        } else {
            System.out.println("Operation choose pc2 root directory cancelled.");
        }
    }
}
