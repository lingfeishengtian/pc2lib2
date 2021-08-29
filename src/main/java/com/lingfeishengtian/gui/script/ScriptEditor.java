package com.lingfeishengtian.gui.script;

import com.lingfeishengtian.cli.CommandExecutor;
import edu.csus.ecs.pc2.core.security.FileSecurityException;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.Scanner;

public class ScriptEditor {
    public TextArea editor;

    public void runScriptFromFile(ActionEvent actionEvent) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose Script File");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PC2 Scripts", "*.pc2pqascript"));
        File file = chooser.showOpenDialog(((Node) actionEvent.getTarget()).getScene().getWindow());

        if (file != null) {
            CommandExecutor executor = new CommandExecutor();
            executor.executeCommand(new String[]{"run", file.getAbsolutePath()});
        }
    }

    public void runScriptFromEditor(ActionEvent actionEvent) {
        Scanner scan = new Scanner(editor.getText());
        CommandExecutor executor = new CommandExecutor();
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            try {
                executor.execute(line.split(" "), true);
            } catch (FileSecurityException e) {
                System.out.println("Invalid contest passcode!");
            }
        }
    }
}
