package com.lingfeishengtian.cli;

import com.lingfeishengtian.core.ContestInstance;
import com.lingfeishengtian.core.DefaultProblem;
import com.lingfeishengtian.utils.Cleaner;
import com.lingfeishengtian.ziputils.ZipCreator;
import edu.csus.ecs.pc2.core.model.ClientType;
import edu.csus.ecs.pc2.core.security.FileSecurityException;

import java.io.*;

public class CommandExecutor {
    ContestInstance contest;

    public static void addProperty(ContestInstance contest, String[] args) {
        String property = args[1];
        if (property.equals("account")) {
            try {
                ClientType.Type type = ClientType.Type.valueOf(args[2]);
                contest.generateSingleAccount(type);
            } catch (Exception e) {
                System.out.println("Argument entered <type> is not valid.");
            }
        } else if (property.equals("problem")) {
            // if (args.length == 5) {
            //     String dispName = args[2];
            //     File inFolder = new File(args[3]);
            //     if (args[3].equals("null")) inFolder = null;
            //     File outFolder = new File(args[4]);
            //     try {
            //         contest.addProblem(new DefaultProblem(dispName, ));
            //     } catch (Exception e) {
            //         System.out.println("This is weird, for some reason, out file is null?");
            //     }
            // } 
            // else 
            if (args.length == 4) {
                contest.bulkAddProblems(new File(args[2]), new File(args[3]));
            } else if (args.length == 3) {
                contest.bulkAddProblems(new File(args[2]));
            } 
            else {
                System.out.println("Too many or not enough arguments.");
            }
        } else {
            System.out.println("Invalid add command executed.");
        }
    }

    public void execute(String[] args, boolean fromFile) throws FileSecurityException {
        String command = args[0];
        if (command.startsWith("#")) return;
        if (command.equals("help")) {
            System.out.println("+===========HELP MENU==========+");
            System.out.println("You must load or create a contest before you make any actions.");
            System.out.println("Please save your contest before you exit!");
            System.out.println("run <file path> : This allows you to run a \"PC2PQA Script\"");
            System.out.println("load <contest bin root path> <contest password> : Loads an existing contest or empty contest folder.");
            System.out.println("new <contest folder destination> <contest password> : Creates a new contest folder.");
            System.out.println("add <property> {arguments} : Adds different stuff to the competition.");
            System.out.println("properties: account, problem");
            System.out.println("\taccount arguments -> <type>");
            System.out.println("\t\taccount types: JUDGE, ADMINISTRATOR, TEAM, SCOREBOARD");
            System.out.println("\tproblem arguments option 1 -> <display name> <data file in path> <data file out path>");
            System.out.println("\tproblem arguments option 2 -> <in out files folder directory> <problem list>");
            System.out.println("\tproblem arguments option 3 -> <in out files folder directory>");
            System.out.println("\tMore info: In out file directory should only contain files with either .dat, .in, or .out extensions. The problem list is optional, but allows you to put problems in order.");
            System.out.println("autoJudge: gives judges autojudge ability for all problems");
            System.out.println("setDefaultTime: sets contest to 2 hours.");
            System.out.println("setDefaultScore : sets scores");
            System.out.println("setDefaultContest : Does all the default contest setup for you!");
            System.out.println("setNewPasscodes <file with passwords>");
            System.out.println("clean : Removes unnecessary backups.");
            System.out.println("save : Saves changes made");
            System.out.println("exit : Exists the script");
            System.out.println("display <properties> : displays properties (WIP) ");
            System.out.println("+==============================+");
            return;
        } else if (command.equals("exit")) {
            System.exit(0);
        } else if (command.equals("run")) {
            if (!fromFile) {
                try {
                    File file = new File(args[1]);
                    if (file.exists()) {
                        BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()));
                        String a;
                        while ((a = br.readLine()) != null) {
                            execute(a.split(" "), true);
                        }
                    } else {
                        System.out.println("File entered does not exist.");
                    }
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("Not enough arguments.");
                } catch (FileNotFoundException e) {
                    System.out.println("File not found.");
                } catch (IOException e) {
                    System.out.println("Couldn't read lines of the file.");
                }
            } else {
                System.out.println("Cannot run script from a script. Operation too dangerous.");
            }
        } else if (command.equals("load")) {
            File fileLoc = new File(args[1]);
            if (fileLoc.exists()) {
                String filePath = fileLoc.getAbsolutePath();
                if (filePath.endsWith(File.separator))
                    filePath = filePath.substring(0, filePath.length() - 1);
                contest = new ContestInstance(filePath, true, args[2]);
                try {
                    contest.startDataViewing();
                } catch (FileSecurityException e) {
                    System.out.println("File security error.");
                    throw e;
                } catch (Exception e) {
                    System.out.println("Weird unexpected error while loading the competition.");
                }
            } else {
                System.out.println("That directory/file does not exist.");
            }
        } else if (command.equals("new")) {
            File dir = new File(args[1]);
            File existing = (new File(dir.getAbsolutePath() + File.separator + "pc2-9.6.0"));
            if (existing.exists()) {
                deleteDir(existing);
            }
            if (dir.exists())
                try {
                    ZipCreator.unzipTo(dir.getAbsolutePath());
                } catch (IOException e) {
                    System.out.println("There was an error while trying to unzip.");
                }
            executeCommand(new String[]{"load", dir.getAbsolutePath() + File.separator + "pc2-9.6.0" + File.separator + "bin", args[2]});
        } else if (command.equals("setLang")) {
            System.out.println("Not supported.");
        } else {
            try {
                if (contest != null) {
                    if (command.equals("add")) {
                        addProperty(contest, args);
                    } else if (command.equals("save")) {
                        contest.saveContest();
                        System.out.println("Contest saved!");
                    } else if (command.equals("autoJudge")) {
                        contest.setContestAutoJudges();
                    } else if (command.equals("clean")) {
                        Cleaner.clean(contest.getPathToBin());
                    } else if (command.equals("setDefaultTime")) {
                        contest.setDefaultContestTime();
                    } else if (command.equals("setDefaultScore")) {
                        contest.setDefaultScoring();
                    } else if (command.equals("setDefaultContest")) {
                        contest.setDefaultContestTime();
                        contest.setDefaultNumberOfAccounts();
                        contest.setDefaultScoring();
                        contest.setContestAutoJudges();
                        System.out.println("Default values set!");
                    } else if (command.equals("setNewPasscodes")) {
                        contest.setGeneratedPasswords(new File(args[1]));
                    }
                } else {
                    System.out.println(command + " is either an invalid command or your contest has not been loaded yet!");
                }
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Not enough arguments.");
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("Syntax error!");
                e.printStackTrace();
            }
        }
    }

    private void error(String message) {

    }

    private enum language {
        en, cn
    }

    private enum errors {
        notEnoughArgs, syntax, notALang, scriptFromScript, unexpected, fileDoesntExist
    }

    public void executeCommand(String[] args) {
        try {
            execute(args, false);
        } catch (Exception e) {
            System.out.println("Invalid contest passcode.");
        }
    }

    boolean deleteDir(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDir(file);
            }
        }
        return directoryToBeDeleted.delete();
    }
}