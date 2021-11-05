package com.lingfeishengtian.core;

import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.validator.pc2Validator.PC2ValidatorSettings;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class DefaultProblem {
    private Problem problem;
    private String problemName;
    private File testCases;

    public DefaultProblem(String name, File testCases) throws ProblemException {
        this.testCases = testCases;
        this.problemName = name;

        problem = new Problem(name);
        problem.setComputerJudged(true);
        problem.setValidatorType(Problem.VALIDATOR_TYPE.PC2VALIDATOR);
        PC2ValidatorSettings validatorSettings = new PC2ValidatorSettings();
        validatorSettings.setWhichPC2Validator(1);
        problem.setPC2ValidatorSettings(validatorSettings);
        problem.setShowCompareWindow(true);
        problem.setShowValidationToJudges(true);
        problem.setManualReview(true);
        problem.setShortName(parseProblemName());
        setDataFiles();
    }

    private boolean hasInputCases() {
        for (File file : testCases.listFiles()) {
            if (file.getName().endsWith(".in") || file.getName().endsWith(".dat")) {
                return true;
            }
        }
        return false;
    }

    public void setDataFiles() throws ProblemException {
        if(testCases == null){
            throw new ProblemException("Your out folder cannot be null.");
        }
        if(hasInputCases()){
            problem.setDataFileName(1 + ".in");
        }
        problem.setAnswerFileName(1 + ".out");
    }

    public String parseProblemName() {
        return problemName.replaceAll("/[^A-Z0-9]+/ig", "_");
    }

    private int getLargestTestCaseNumber() {
        int largest = 0;
        for (File file : testCases.listFiles()) {
            if (file.getName().endsWith(".out")) {
                String name = file.getName();
                String number = name.substring(0, name.indexOf("."));
                int num = Integer.parseInt(number);
                if (num > largest) {
                    largest = num;
                }
            }
        }
        return largest;
    }

    public ProblemDataFiles generateDataFiles() {
        SerializedFile[] serializedInFiles = new SerializedFile[getLargestTestCaseNumber()];
        SerializedFile[] serializedOutFiles = new SerializedFile[getLargestTestCaseNumber()];
        ProblemDataFiles dataFiles = new ProblemDataFiles(problem);

        for (File file : testCases.listFiles()) {
            String name = file.getName();
            System.out.println(name);
            if(name.endsWith("in") || name.endsWith("out")){
                String number = name.substring(0, name.indexOf("."));
                int num = Integer.parseInt(number);
                if (file.getName().endsWith(".in")) {
                    serializedInFiles[num - 1] = new SerializedFile(file.getAbsolutePath());
                } else if (file.getName().endsWith(".out")) {
                    serializedOutFiles[num - 1] = new SerializedFile(file.getAbsolutePath());
                }
            }
        }

        dataFiles.setJudgesAnswerFiles(serializedOutFiles);
        if(testCases != null){
            dataFiles.setJudgesDataFiles(serializedInFiles);
        }

        for (int i = 0; i < serializedOutFiles.length; i++) {
            problem.addTestCaseFilenames(serializedInFiles[i].getName(), serializedOutFiles[i].getName());
        }
        return dataFiles;
    }

    public Problem getProblem() {
        return problem;
    }
}