package com.lingfeishengtian.gui;

import com.lingfeishengtian.cli.CommandExecutor;
import com.lingfeishengtian.gui.utils.Console;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
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
    @FXML
    public Button saveChangesBtn;
    @FXML
    public Button addProblemBtn;
    @FXML
    public Button chooseProblemListBtn;
    @FXML
    public Text problemListChosenTxt;
    @FXML
    public Button clearProblemListBtn;
    PrintStream old = System.out;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/GUIStarter.fxml"));
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

    private File problemList = null;

    private void disableContestOperations() {
        setDefaultContestSettingsBtn.setDisable(true);
        saveChangesBtn.setDisable(true);
        addProblemBtn.setDisable(true);
        chooseProblemListBtn.setDisable(true);
        clearProblemListBtn.setDisable(true);
    }

    private void enableContestOperations() {
        setDefaultContestSettingsBtn.setDisable(false);
        saveChangesBtn.setDisable(false);
        addProblemBtn.setDisable(false);
        chooseProblemListBtn.setDisable(false);
        clearProblemListBtn.setDisable(false);
    }

    private Optional<String> getUserEnteredPassword() {
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle("Contest Passcode");
        textInputDialog.setHeaderText("Enter the existing contest passcode or new contest passcode.");
        return textInputDialog.showAndWait();
    }

    @FXML
    public void loadContestBin(ActionEvent actionEvent) {
        File fileChosen = getUserChosenDirectory(actionEvent, null);

        if (fileChosen != null) {
            File bin = new File(fileChosen.getAbsolutePath() + File.separator + "bin");
            if (bin.exists()) {
                Optional<String> str = getUserEnteredPassword();
                try {
                    executor.execute(new String[]{"load", bin.getAbsolutePath(), str.isPresent() ? str.orElse("No reason error") : ""}, false);
                    contestStatusTxt.setText("Loaded " + bin.getAbsolutePath());
                    contestStatusTxt.setFill(Color.GREEN);
                    enableContestOperations();
                    contestLog.setScrollTop(Double.MAX_VALUE);
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

    private File getUserChosenDirectory(ActionEvent actionEvent, String title) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        if (title == null) title = "Choose PC^2 Root Directory";
        directoryChooser.setTitle(title);
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        return directoryChooser.showDialog(((Node) actionEvent.getTarget()).getScene().getWindow());
    }

    @FXML
    public void setDefaultContest(ActionEvent actionEvent) throws FileSecurityException {
        executor.execute(new String[]{"setDefaultContest"}, true);
    }

    @FXML
    public void createContestFolder(ActionEvent actionEvent) {
        File chosen = getUserChosenDirectory(actionEvent, null);

        if (chosen.isDirectory()) {
            System.out.println("Attempting to create a new contest folder at " + chosen.getAbsolutePath());

            boolean sameNameExists = false;
            for (File x : chosen.listFiles()) {
                if (x.getName().equals("pc2-9.6.0")) {
                    sameNameExists = true;
                    break;
                }
            }

            if (sameNameExists) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Override Alert");
                alert.setHeaderText("PC^2 Contest Found!");
                alert.setContentText("Are you sure you want to override an existing PC^2 contest?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() != ButtonType.OK) {
                    System.out.println("Operation cancelled due to pre-existing PC^2 contest and user cancelled.");
                    return;
                }
            }

            Optional<String> password = getUserEnteredPassword();

            if (!password.isPresent()) {
                System.out.println("No password entered!");
                return;
            } else if (password.orElse("").length() <= 1) {
                System.out.println("Password too short!");
                return;
            }
            try {
                System.out.println(password.toString());
                executor.execute(new String[]{"new", chosen.getAbsolutePath(), password.orElse("")}, true);
                System.out.println("Success!");
                contestStatusTxt.setText("Loaded " + chosen.getAbsolutePath());
                contestStatusTxt.setFill(Color.GREEN);
                enableContestOperations();
                contestLog.setScrollTop(Double.MAX_VALUE);
            } catch (FileSecurityException e) {
                System.out.println("Contest had an issue and couldn't create your contest. Likely a file saving issue.");
            }
        } else {
            System.out.println("Chosen file is not a directory.");
        }
    }

    @FXML
    public void openScriptEditor(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ScriptEditor.fxml"));
            Parent root1 = fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void saveChanges(ActionEvent actionEvent) {
        try {
            executor.execute(new String[]{"save"}, true);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in saving values! Could not save competition.");
        }
    }

    @FXML
    public void addProblemsFromDirectory(ActionEvent actionEvent) {
        File chosen = getUserChosenDirectory(actionEvent, "Choose Directory With inouts");
        if (chosen.exists()) {
            if (problemList == null) {
                executor.executeCommand(new String[]{"add", "problem", chosen.getAbsolutePath()});
            } else {
                executor.executeCommand(new String[]{"add", "problem", chosen.getAbsolutePath(), problemList.getAbsolutePath()});
            }
            System.out.println("Problems added");
        } else {
            System.out.println("An unexpected error occurred!");
        }
    }

    @FXML
    public void chooseProblemList(ActionEvent actionEvent) {
        FileChooser file = new FileChooser();
        file.setTitle("Choose Problem list");
        file.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt"));
        File newFile = file.showOpenDialog(((Node) actionEvent.getTarget()).getScene().getWindow());
        if (newFile != null) {
            problemList = newFile;
            problemListChosenTxt.setText(newFile.getName() + " is the current selected problem list.");
        }
    }

    @FXML
    public void clearProblemList(ActionEvent actionEvent) {
        System.out.println("Problem list cleared");
        problemListChosenTxt.setText("No Problem List Chosen");
    }
}
