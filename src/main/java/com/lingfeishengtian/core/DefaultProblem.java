package com.lingfeishengtian.core;

import edu.csus.ecs.pc2.core.model.Problem;
import edu.csus.ecs.pc2.core.model.ProblemDataFiles;
import edu.csus.ecs.pc2.core.model.SerializedFile;
import edu.csus.ecs.pc2.validator.pc2Validator.PC2ValidatorSettings;

import java.io.File;

public class DefaultProblem {
    private Problem problem;
    private File in, out;

    public DefaultProblem(String name, File in, File out) throws ProblemException {
        problem = new Problem(name);
        problem.setComputerJudged(true);
        problem.setValidatorType(Problem.VALIDATOR_TYPE.PC2VALIDATOR);
        PC2ValidatorSettings validatorSettings = new PC2ValidatorSettings();
        validatorSettings.setWhichPC2Validator(1);
        problem.setPC2ValidatorSettings(validatorSettings);
        problem.setShowCompareWindow(true);
        problem.setShowValidationToJudges(true);
        problem.setManualReview(true);

        setDataFiles(in, out);
    }

    public void setDataFiles(File in, File out) throws ProblemException {
        if(out == null){
            throw new ProblemException("Your out file cannot be null.");
        }else{
            this.in = in;
            this.out = out;
        }
        if(in != null){
            problem.setDataFileName(in.getName());
        }
        problem.setAnswerFileName(out.getName());
    }

    public ProblemDataFiles generateDataFiles() {
        ProblemDataFiles dataFiles = new ProblemDataFiles(problem);
        dataFiles.setJudgesAnswerFile(new SerializedFile(out.getAbsolutePath()));
        if(in != null)
            dataFiles.setJudgesDataFile(new SerializedFile(in.getAbsolutePath()));
        return dataFiles;
    }

    public Problem getProblem() {
        return problem;
    }
}