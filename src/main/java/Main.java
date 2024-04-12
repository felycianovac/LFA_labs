import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        List<Character> states = List.of('A', 'B', 'C');
        List<Character> alphabet = List.of('0', '1');
        Map<Character, Map<Character, List<Character>>> transitions = Map.of(
                'A', Map.of(
                        '0', List.of('A','B')
                ),
                'B', Map.of(
                        '0', List.of('C'),
                        '1', List.of('A','B')
                ),
                'C', Map.of(
                        '0', List.of('B')
                )

        );

        FiniteAutomaton finiteAutomaton = new FiniteAutomaton(states, alphabet, transitions, 'A', List.of('C'));
        finiteAutomaton.nfa_to_dfa().generatePngRepresentation();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the command (type 'exit to quit'): ");
        while (true) {
            String input = scanner.nextLine();
            if (input.equals("exit")) {
                break;
            }
            List<Token> tokens = Tokenizer.tokenize(input);
            for (Token token : tokens) {
                System.out.println(token);
            }
        }

    }



}


