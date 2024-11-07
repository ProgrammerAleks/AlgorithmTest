import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final String FORMULA = "((F +(C/N) + H * (P /R *S)) *X)";

    private static final String[] QUESTIONS = {
            "FHow fun will the class be? (1-10)I",
            "CHow cool is the Professor? (1-10)I",
            "NDo I need this class to Graduate? (Yes/No)B",
            "HHow many hours does this class start after I wake up? (0-23)I",
            "PWhat is the percent chance I will pass this class? (0-100)P",
            "RIs this a repeat, have I taken the class before? (Yes/No)B",
            "SAre any of my smart friends taking this class? (Yes/No)B"
    };
    private static final float X = 0.085f;

    private static void questions() {
        Scanner input = new Scanner(System.in);
        Formula formula = new Formula(FORMULA);

        formula.defineVariable("X", X);
        for (String question : QUESTIONS) {
            System.out.println(question.substring(1, question.length()-1)); //display the question

            char variable = question.charAt(0);
            char inputType = question.charAt(question.length()-1);
            var value = (inputType == 'B') ? input.next() : input.nextInt();

            if (inputType == 'B') { //filter boolean input
                if (variable == 'N') {
                    value = ((String)value).toLowerCase().contains("y") ? 1 : 2;
                } else {
                    value = ((String)value).toLowerCase().contains("y") ? 2 : 1;
                }
            } else if (inputType == 'P') {
                value = (Integer)value/100f;
            }

            formula.defineVariable(""+variable, value.toString()); //replace the variable in the formula with the user input
        }

        System.out.println(formula.expression());
        try {System.out.println(formula.expression().evaluate());} catch (Exception e) {System.out.println(e.getMessage());}
        input.close();
    }

    public static void main(String[] args) {
        questions();
    }
}