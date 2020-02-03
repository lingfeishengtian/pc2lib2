package com.lingfeishengtian.cli;

import com.lingfeishengtian.core.ContestInstance;
import com.lingfeishengtian.core.DefaultProblem;
import edu.csus.ecs.pc2.core.model.ClientType;

import java.io.*;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Scanner;

public class CLIStarter {
    static ContestInstance contest;

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        while(true){
            System.out.print("PC^2 PQA CLI>");
            String command = scan.nextLine();
            String[] arguments = command.split(" ");

            if(arguments[0].equals("run")){
                try{
                    File file = new File(arguments[1]);
                    if(file.exists()){
                        BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()));
                        String a;
                        while((a = br.readLine()) != null){
                            executeCommand(a.split(" "));
                        }
                    }else{
                        System.out.println("File entered does not exist.");
                    }
                }catch (IndexOutOfBoundsException e){
                    System.out.println("Not enough arguments.");
                }catch (FileNotFoundException e){
                    System.out.println("File not found.");
                }catch (IOException e){
                    System.out.println("Couldn't read lines of the file.");
                }
            }

            executeCommand(arguments);
        }
    }

    public static void executeCommand(String[] args){
        String command = args[0];

        if(command.equals("help")){
            System.out.println("+===========HELP MENU==========+");
            System.out.println("You must load a contest before you make any actions.");
            System.out.println("Please save your contest before you exit!");
            System.out.println("run <file path> : This allows you to run a \"PC2PQA Script\"");
            System.out.println("load <contest bin root path> <contest password>");
            System.out.println("add <property> {arguments}");
            System.out.println("properties: account, problem");
            System.out.println("\taccount arguments -> <type>");
            System.out.println("\t\taccount types: JUDGE, ADMINISTRATOR, TEAM, SCOREBOARD");
            System.out.println("\tproblem arguments -> <display name> <data file in path> <data file out path>");
            System.out.println("autoJudge: gives judges autojudge ability for all problems");
            System.out.println("save");
            System.out.println("exit");
            System.out.println("+==============================+");
            return;
        }else if(command.equals("exit")){
            System.exit(0);
        }

        try {
            if(contest == null){
                if(command.equals("load")){
                    File fileLoc = new File(args[1]);
                    if(fileLoc.exists()) {
                        String filePath = fileLoc.getAbsolutePath();
                        if(filePath.endsWith("/"))
                            filePath = filePath.substring(0, filePath.length() - 1);
                        contest = new ContestInstance(filePath, true, args[2]);
                        contest.startDataViewing();
                    }else{
                        System.out.println("That directory/file does not exist.");
                    }
                }else
                    System.out.println("You must load a contest before you make any commands.");
            }else{
                if(command.equals("add")){
                    addProperty(contest, args);
                }else if(command.equals("save")){
                    contest.saveContest();
                }else if(command.equals("autoJudge")){
                    contest.setContestAutoJudges();
                }
            }
        }catch (IndexOutOfBoundsException e){
            System.out.println("Not enough arguments.");
        }
    }

    public static void addProperty(ContestInstance contest, String[] args){
        String property = args[1];
        if(property.equals("account")){
            try {
                ClientType.Type type = ClientType.Type.valueOf(args[2]);
                contest.generateSingleAccount(type);
            }catch (Exception e){
                System.out.println("Argument entered <type> is not valid.");
            }
        }else if(property.equals("problem")){
            String dispName = args[2];
            File inFile = new File(args[3]);
            if(args[3].equals("null")) inFile = null;
            File outFile = new File(args[4]);
            try {
                contest.addProblem(new DefaultProblem(dispName, inFile, outFile));
            }catch (Exception e){
                System.out.println("This is weird, for some reason, out file is null?");
            }
        }else{
            System.out.println("Invalid add command executed.");
        }
    }
}
