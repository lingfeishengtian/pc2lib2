package com.lingfeishengtian.cli;

import java.util.Scanner;

public class CLIStarter {
    final static CommandExecutor executor = new CommandExecutor();

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        while(true){
            System.out.print("PC^2 PQA CLI>");
            String command = scan.nextLine();
            String[] arguments = command.split(" ");

            executor.executeCommand(arguments);
        }
    }
}
