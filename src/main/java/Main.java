
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the input string:");
        String input = scanner.nextLine();

        List<Token> tokens = Tokenizer.tokenize(input);
        ASTBuilder astBuilder = new ASTBuilder();
        ASTNode root = astBuilder.buildParseTree(tokens);

        System.out.println("AST:");
        astBuilder.printParseTree(root, 0);



    }

}
