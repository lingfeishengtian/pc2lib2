package com.lingfeishengtian.core;

public class ProblemException extends Exception{
    String exceptionMessage;

    public ProblemException(String message){
        exceptionMessage = message;
    }

    @Override
    public void printStackTrace() {
        System.out.println("Default problem exception occurred.");
        System.out.println(exceptionMessage);
        super.printStackTrace();
    }
}
