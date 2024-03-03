package laboratory;

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
        Map<Character, Map<Character, List<Character>>> faTransitions = new HashMap<>();

        for (Character state : Vn) {
            // For each state, initialize its transition rules as an empty map.
            Map<Character, List<Character>> stateTransitions = new HashMap<>();
            faTransitions.put(state, stateTransitions);

            List<String> productions = P.get(state.toString());

            for (String production : productions) {
                if (production.isEmpty()) continue;

                char input = production.charAt(0);
                Character nextState = production.length() > 1 ? production.charAt(1) : 'F';

                if (Vt.contains(input)) {
                    // Ensure the list for this input exists
                    stateTransitions.computeIfAbsent(input, k -> new ArrayList<>()).add(nextState);
                }
            }
        }

        // Include the special final state 'F' in the list of states if it's not already included.
        List<Character> statesWithFinal = new ArrayList<>(Vn);
        if (!statesWithFinal.contains('F')) {
            statesWithFinal.add('F');
        }

        List<Character> finalStates = new ArrayList<>(Collections.singletonList('F'));

        // Now the `faTransitions` map matches the expected type.
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
        // Check for ε-production from a non-start symbol
        if (value.equals("ε") && !key.equals(S.toString())) {
            // This makes the grammar Type 0 if there's an empty production from a non-start symbol
            return false;
        }

        if (key.equals(S.toString()) && value.equals("ε")) {
            // Special case for start symbol producing ε is allowed for Type 1,
            // but only if the start symbol doesn't appear on the RHS of any production.
            for (List<String> values : P.values()) {
                for (String val : values) {
                    if (val.contains(S.toString())) return false;
                }
            }
            return true;
        }

        // General case for Context-Sensitive Grammar: RHS length >= LHS length
        return key.length() <= value.length();
    }


    private boolean isContextFreeGrammar(String key) {
        // Context-free grammars have a single non-terminal on the LHS, without restriction on the RHS.
        return key.length() == 1 && Vn.contains(key.charAt(0));
    }

    private boolean isRightLinear(String value) {
        if((value.length()==2 && (value.charAt(1) == 'ε' && Vn.contains(value.charAt(0)))) )
            return true;
        if (value.equals("ε") || Vt.contains(value.charAt(0)) || value.charAt(0)=='ε') {
            for (int i = 0; i < value.length() - 1; i++) {
                if (!Vt.contains(value.charAt(i)) && value.charAt(i)!='ε') return false; // Non-terminal found before the last character
            }
            // Last character can be a non-terminal
            return value.length() <= 1 || Vt.contains(value.charAt(value.length() - 1)) || Vn.contains(value.charAt(value.length() - 1)) ;
        }
        if(value.length() == 1 && (Vt.contains(value.charAt(0)) || Vn.contains(value.charAt(0)) || value.charAt(0)=='ε')){
            return true;
        }

        return false;
    }


    private boolean isLeftLinear(String value) {

        if (value.equals("ε") || Vn.contains(value.charAt(0)) && (value.length() == 1 || value.length()==2 && value.charAt(1)=='ε')) return true;
        if (value.length() > 1 && Vn.contains(value.charAt(0)) || value.charAt(0)=='ε') {
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
