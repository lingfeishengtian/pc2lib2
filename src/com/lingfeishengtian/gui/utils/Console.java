package com.lingfeishengtian.gui.utils;

import javafx.scene.control.TextArea;

import java.io.OutputStream;

public class Console extends OutputStream {
    private TextArea output;

    public Console(TextArea ta) {
        this.output = ta;
    }

    @Override
    public void write(int i) {
        output.appendText(String.valueOf((char) i));
    }
}