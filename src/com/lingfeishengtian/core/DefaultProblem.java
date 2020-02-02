package com.lingfeishengtian.core;

import edu.csus.ecs.pc2.core.model.Problem;

public class DefaultProblem {
    private Problem problem;

    public DefaultProblem(String name){
        problem = new Problem(name);
    }
}
