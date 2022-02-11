package com.lingfeishengtian.core;

import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.validator.pc2Validator.PC2ValidatorSettings;

public class DefaultProblem {
    private Problem problem;
    private String testCaseName;
    private SerializedFile[] inSerializedFiles, outSerializedFiles;
    private String inExtensions, outExtensions;

    private boolean isArrayNull(SerializedFile[] files) throws ProblemException{
        if (files != null) {
            boolean isNull = files[0] == null;
            for (SerializedFile serializedFile : files) {
                if((serializedFile == null) != isNull) {
                    throw new ProblemException("Input Mismatch");
                }
            }
            return isNull;
        }
        return true;
    }

    public DefaultProblem(String name, String testCaseName, SerializedFile[] serializedIn,
            SerializedFile[] serializedOut) throws ProblemException {
        this.inSerializedFiles = serializedIn;
        this.outSerializedFiles = serializedOut;
        if (serializedOut == null || serializedOut.length == 0) {
            System.out.println(name + " " + testCaseName);
            throw new ProblemException("There must be out files.");
        }
        if (serializedIn != null && serializedIn.length != serializedOut.length)
            throw new ProblemException("In files length must be the same as out file length.");
        String tmpExceptions;
        if (serializedIn != null && serializedIn.length > 0 && !isArrayNull(serializedIn)) {
            tmpExceptions = serializedIn[0].getAbsolutePath()
                    .substring(serializedIn[0].getAbsolutePath().lastIndexOf("."));
            for (SerializedFile file : serializedIn) {
                if (!tmpExceptions.equals(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."))))
                    throw new ProblemException("Input file extensions are different.");
            }
            this.inExtensions = tmpExceptions;
        }
        tmpExceptions = serializedOut[0].getAbsolutePath()
                .substring(serializedOut[0].getAbsolutePath().lastIndexOf("."));
        for (SerializedFile file : serializedOut) {
            if (!tmpExceptions.equals(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."))))
                throw new ProblemException("Output file extensions are different.");
        }
        this.outExtensions = tmpExceptions;
        this.testCaseName = testCaseName;

        problem = new Problem(name);
        problem.setComputerJudged(true);
        problem.setValidatorType(Problem.VALIDATOR_TYPE.PC2VALIDATOR);
        PC2ValidatorSettings validatorSettings = new PC2ValidatorSettings();
        validatorSettings.setWhichPC2Validator(1);
        problem.setPC2ValidatorSettings(validatorSettings);
        problem.setShowCompareWindow(true);
        problem.setShowValidationToJudges(true);
        problem.setManualReview(true);
        problem.setShortName(testCaseName);
        setDataFiles();
    }

    public DefaultProblem(String name, SerializedFile[] serializedIn, SerializedFile[] serializedOut)
            throws ProblemException {
        this(name, name.replaceAll("[^a-zA-Z]+", "").replaceAll(" ", "").toLowerCase(), serializedIn, serializedOut);
    }

    private boolean hasInputCases() {
        if (inSerializedFiles == null || inSerializedFiles.length == 0 || inExtensions == null)
            return false;
        else
            return true;
    }

    public void setDataFiles() throws ProblemException {
        if (outSerializedFiles == null) {
            throw new ProblemException("Your out folder cannot be null.");
        }
        if (hasInputCases()) {
            problem.setDataFileName(testCaseName + inExtensions);
        }
        problem.setAnswerFileName(testCaseName + outExtensions);
    }

    public ProblemDataFiles generateDataFiles() {
        ProblemDataFiles dataFiles = new ProblemDataFiles(problem);

        // for (File file : testCases.listFiles()) {
        // String name = file.getName();
        // System.out.println(name);
        // if(name.endsWith("in") || name.endsWith("out")){
        // String number = name.substring(0, name.lastIndexOf("."));
        // int num = Integer.parseInt(number);
        // if (file.getName().endsWith(".in")) {
        // serializedInFiles[num - 1] = new SerializedFile(file.getAbsolutePath());
        // } else if (file.getName().endsWith(".out")) {
        // serializedOutFiles[num - 1] = new SerializedFile(file.getAbsolutePath());
        // }
        // }
        // }

        dataFiles.setJudgesAnswerFiles(outSerializedFiles);
        if (hasInputCases()) {
            dataFiles.setJudgesDataFiles(inSerializedFiles);
        }

        for (int i = 0; i < outSerializedFiles.length; i++) {
            problem.addTestCaseFilenames(inSerializedFiles == null || inSerializedFiles[i] == null ? null : inSerializedFiles[i].getName(), outSerializedFiles[i].getName());
        }
        return dataFiles;
    }

    public Problem getProblem() {
        return problem;
    }
}