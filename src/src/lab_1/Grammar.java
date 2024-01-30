package lab_1;

import java.util.*;

public class Grammar {

    private List<Character> Vn;
    private List<Character> Vt;
    private Map<Character,List<String>> P;
    private Character S;
    private Random random;


    public Grammar(){
        this.random = new Random();

        // Initialize non-terminal symbols
        Vn = Arrays.asList('S', 'A', 'B', 'C');

        // Initialize terminal symbols
        Vt = Arrays.asList('a', 'b', 'c', 'd');

        //initialize Production rules
        P = Map.ofEntries(
                Map.entry('S', Arrays.asList("dA")),
                Map.entry('A', Arrays.asList("aB", "bA")),
                Map.entry('B', Arrays.asList("bC", "aB", "d")),
                Map.entry('C', Arrays.asList("cB"))
        );

        // Set start symbol
        S = 'S';
    }

    public String generateString() {
        /*in order to keep the structure of class presented as example I will use queue
         to keep track which symbols need to be processed next
         */
        Queue<Character> queue = new LinkedList<>();

        queue.add(S); // Start with the start symbol
        StringBuilder result = new StringBuilder();

        while (!queue.isEmpty()) {
            Character currentSymbol = queue.poll(); //remove and return the first symcol from queue
            if (Vn.contains(currentSymbol)) { //check for non-terminal symbol
                List<String> productions = P.get(currentSymbol); //lookup for production rules
                String chosenProduction = productions.get(random.nextInt(productions.size()));
                for (int i = chosenProduction.length() - 1; i >= 0; i--) {
                    queue.add(chosenProduction.charAt(i));
                }
            } else {
                result.append(currentSymbol);
            }
        }

        return result.toString();
    }

}
