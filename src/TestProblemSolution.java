import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TestProblemSolution {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner scan = new Scanner(new File("problem.in"));
        while (scan.hasNext()) {
            String x = scan.nextLine();
            for (char a : x.toCharArray()) {
                if (a == ' ') {
                    System.out.print(a + "");
                } else {
                    int b = (a - 96);
                    System.out.print(b + "");
                }
            }
        }
    }
}