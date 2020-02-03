import com.lingfeishengtian.core.ContestInstance;
import com.lingfeishengtian.core.DefaultProblem;
import com.lingfeishengtian.core.ProblemException;

import java.io.File;

public class Tests {
    public static void main(String[] args) throws ProblemException {
        ContestInstance contest = new ContestInstance("/Users/hunterhan/Desktop/PC2PQA_WithCustomScripts/pc2-9.6.0/bin", true, "chscompsci");
        contest.startDataViewing();
        DefaultProblem problem = new DefaultProblem("ABC", new File("TestData/problem.in"), new File("TestData/problem.out"));
        contest.test();
        contest.addProblem(problem);
        contest.saveContest();
        System.exit(0);
    }
}
