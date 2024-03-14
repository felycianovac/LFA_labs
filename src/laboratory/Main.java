package laboratory;

import java.util.List;
import java.util.Scanner;

import static laboratory.Tokenizer.tokenize;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the command (type 'exit to quit'): ");
        while (true) {
            String input = scanner.nextLine();
            if (input.equals("exit")) {
                break;
            }
            List<Token> tokens = tokenize(input);
            for (Token token : tokens) {
                System.out.println(token);
            }
        }

    }


}


