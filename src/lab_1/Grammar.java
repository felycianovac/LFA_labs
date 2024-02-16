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
//

    public String generateString() {
        return generateString(S);
    }

    private String generateString(Character symbol) {
        //if the symbol is a terminal symbol, return it
        if (Vt.contains(symbol)) {
            return symbol.toString();
        }
        //get production rules for the non-terminal symbol
        List<String> productions = P.get(symbol);
        //randomly select a production rule
        String production = productions.get(random.nextInt(productions.size()));
        // generate a string by recursively processing each symbol in the production
        StringBuilder result = new StringBuilder();
        for (char c : production.toCharArray()) {
            if(Vn.contains(c)){ //if the symbol is a non-terminal symbol, recursively generate a string for it
                result.append(generateString(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    public FiniteAutomaton toFiniteAutomaton() {
        // Initialize a map to hold the transition rules for the finite automaton.
        // Each state maps to another map, where each input symbol maps to a next state.
        Map<Character, Map<Character, Character>> faTransitions = new HashMap<>();

        //Iterate over all non-terminal symbols, treating each as a state in the finite automaton.
        for (Character state : Vn) {
            // For each state, initialize its transition rules as an empty map.
            faTransitions.put(state, new HashMap<>());
            // Retrieve the production rules for the current non-terminal symbol.
            List<String> productions = P.get(state);

            // Iterate over each production rule, adding a transition rule for each terminal symbol.
            for (String production : productions) {
                // The first character of the production is considered as the input symbol for the transition.
                char input = production.charAt(0); // Terminal symbol
                // Determine the next state based on the production rule:
                // If the production has more than one character, the second character is the next state.
                // Otherwise, use 'F' to denote a final state.
                Character nextState = production.length() > 1 ? production.charAt(1) : 'F'; // Next state or final state 'F'
                // Only consider the production rule if the first character is a terminal symbol.
                if (!Vt.contains(input)) {
                    continue; // Skip if the first character is not a terminal symbol
                }
                // Add the transition rule to the map: from the current state, given the input symbol, to the next state.
                faTransitions.get(state).put(input, nextState);
            }
        }

        // Define the final or accepting states for the automaton, initially containing 'F'.
        List<Character> finalStates = new ArrayList<>(Collections.singletonList('F'));
        // Prepare a list of all states, including non-terminals and the special final state 'F'.
        List<Character> statesWithFinal = new ArrayList<>(Vn);

        return new FiniteAutomaton(statesWithFinal, Vt, faTransitions, S, finalStates);
    }

}
