import com.lingfeishengtian.core.ContestInstance;
import com.lingfeishengtian.core.DefaultProblem;
import com.lingfeishengtian.core.ProblemException;
import com.lingfeishengtian.ziputils.ZipCreator;

import java.io.File;
import java.io.IOException;

public class Tests {
    public static void main(String[] args) throws ProblemException, IOException {
        runTests();
        ZipCreator.unzipTo("/Users/hunterhan/Desktop/PC2PQA_WithCustomScripts/PP");
    }

    public static void runTests() throws ProblemException {
        ContestInstance contest = new ContestInstance("/Users/hunterhan/Desktop/PC2PQA_WithCustomScripts/pc2-9.6.0/bin", true, "password");
        contest.startDataViewing();
        DefaultProblem problem = new DefaultProblem("ABC", new File("TestData/problem.in"), new File("TestData/problem.out"));
        contest.test();
        contest.setDefaultNumberOfAccounts();
        contest.setDefaultContestTime();
        contest.addProblem(problem);
        contest.setDefaultScoring();
        contest.saveContest();
        System.exit(0);
    }
}
