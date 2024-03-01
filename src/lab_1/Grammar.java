package lab_1;

import java.sql.SQLOutput;
import java.util.*;

public class Grammar {

    private List<Character> Vn;
    private List<Character> Vt;
    private Map<String,List<String>> P;
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
                Map.entry("S", Arrays.asList("dA")),
                Map.entry("A", Arrays.asList("aB", "bA")),
                Map.entry("B", Arrays.asList("bC", "aB", "d")),
                Map.entry("C", Arrays.asList("cB"))
        );

        // Set start symbol
        S = 'S';
    }

    public Grammar(List<Character> Vn, List<Character> Vt, Map<String, List<String>> P, Character S) {
        this.Vn = Vn;
        this.Vt = Vt;
        this.P = P;
        this.S = S;
        this.random = new Random();
    }

    public String generateString() {
        return generateString(S.toString());
    }

    private String generateString(String symbol) {
        //consider that the given grammar is of type 0, and the string-value has a single character
        //if the symbol is a terminal symbol, return it
        if (Vt.contains(symbol.charAt(0))){
            return symbol;
        }
        //get production rules for the non-terminal symbol
        List<String> productions = P.get(symbol);
        //randomly select a production rule
        String production = productions.get(random.nextInt(productions.size()));
        // generate a string by recursively processing each symbol in the production
        StringBuilder result = new StringBuilder();
        for (char c : production.toCharArray()) {
            if(Vn.contains(c)){ //if the symbol is a non-terminal symbol, recursively generate a string for it
                result.append(generateString(c + ""));
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
            List<String> productions = P.get(state.toString());

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

    public Integer classifyGrammar() {
        int type_2_passes = 0;
        int type_1_passes = 0;
        int type_0_passes = 0;

        //check for linearity first
        // I splited it because of the conflicts and the errors that sometimes occur when checking both right and left linear
        boolean allProductionsAreRightLinear = true;
        boolean allProductionsAreLeftLinear = true;

        for (Map.Entry<String, List<String>> entry : P.entrySet()) {
            for (String value : entry.getValue()) {
                if (entry.getKey().length() == 1) {
                    if (!isRightLinear(value)) {
                        allProductionsAreRightLinear = false;
                    }
                    if (!isLeftLinear(value)) {
                        allProductionsAreLeftLinear = false;
                    }
                }else {
                    allProductionsAreRightLinear = false;
                    allProductionsAreLeftLinear = false;
                }
            }
        }

        // If all productions consistently follow one linearity pattern, it's a regular grammar
        if (allProductionsAreRightLinear || allProductionsAreLeftLinear) {
            return 3; // Regular grammar
        }
        for (Map.Entry<String, List<String>> entry : P.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();

            for (String value : values) {

                if (isContextFreeGrammar(key)) {
                    type_2_passes++;
                }
                else if (isContextSensitiveGrammar(key, value)) {
                    type_1_passes++;
                } else {
                    type_0_passes++;
                }
            }
        }

        if (type_0_passes > 0) {
            return 0;
        } else if (type_1_passes > 0 ) {
            return 1;
        } else if (type_2_passes > 0) {
            return 2;
        }
        return -1;
    }


    private boolean isContextSensitiveGrammar(String key, String value) {
        return key.length() <=  value.length();
    }

    private boolean isContextFreeGrammar(String key) {
        // Context-free grammars have a single non-terminal on the LHS, without restriction on the RHS.
        return key.length() == 1 && Vn.contains(key.charAt(0));
    }

    private boolean isRightLinear( String value) {
        if (value.isEmpty() || Vt.contains(value.charAt(0))) {
            for (int i = 0; i < value.length() - 1; i++) {
                if (!Vt.contains(value.charAt(i))) return false; // Non-terminal found before the last character
            }
            // Last character can be a non-terminal
            return value.length() <= 1 || Vt.contains(value.charAt(value.length() - 1)) || Vn.contains(value.charAt(value.length() - 1));
        }
        if(value.length() == 1 && (Vt.contains(value.charAt(0)) || Vn.contains(value.charAt(0)))){
            return true;
        }
        return false;
    }


    private boolean isLeftLinear(String value) {
        if (value.isEmpty() || Vn.contains(value.charAt(0)) && value.length() == 1) return true;
        if (value.length() > 1 && Vn.contains(value.charAt(0))) {
            for (int i = 1; i < value.length(); i++) {
                if (!Vt.contains(value.charAt(i))) return false; // Terminal found after the first character
            }
            return true; // First character is a non-terminal followed by terminals
        }
        if(value.length() == 1 && (Vt.contains(value.charAt(0)) || Vn.contains(value.charAt(0)))){
            return true;
        }
        return false;
    }

    public void printGrammar() {
        System.out.println("Non-terminals (Vn): " + Vn);
        System.out.println("Terminals (Vt): " + Vt);
        System.out.println("Start Symbol (S): " + S);
        System.out.println("Production Rules (P):");
        for (Map.Entry<String, List<String>> entry : P.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            for (String value : values) {
                System.out.println("  " + key + " -> " + value);
            }
        }
    }


}
