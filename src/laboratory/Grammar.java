package laboratory;

import java.util.*;

public class Grammar {

    protected List<Character> Vn;
    protected List<Character> Vt;
    protected Map<String,List<String>> P;
    protected Character S;
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
        //flags to keep track of each production type
        int type_2_passes = 0;
        int type_1_passes = 0;
        int type_0_passes = 0;

        //check for linearity first
        // If all productions are consistently right or left linear, it's a regular grammar
        //flags to keep track of each production type
        boolean allProductionsAreRightLinear = true;
        boolean allProductionsAreLeftLinear = true;

        //iterate through the production rules and check the production linearity for each
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
                    //if the lhs of the rule is not a single non-terminal, then the grammar is not regular
                    allProductionsAreRightLinear = false;
                    allProductionsAreLeftLinear = false;
                }
            }
        }

        // If all productions consistently follow one linearity pattern, it's a regular grammar
        if (allProductionsAreRightLinear || allProductionsAreLeftLinear) {
            return 3; // Regular grammar
        }
        //iterate through the production rules and check the production of the remaining types
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

        //if at least one production is of type 0, then the grammar is of type 0
        // bring it in the reversed order of the type checking because we first check for type 2, then type 1, and finally type 0
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
        //if the production has the rhs of length 1 then it can be either terminal or non-terminal, or even ε
        if(value.length() == 1 && (Vt.contains(value.charAt(0)) || Vn.contains(value.charAt(0)) || value.charAt(0)=='ε')){
            return true;
        }
        //the valid cases for right linear grammar will be if the production is of the form A->aB or A->a and A->ε, A->εA is allowed as well
        if (value.equals("ε") || Vt.contains(value.charAt(0)) || value.charAt(0)=='ε') {
            //iterate over the entire string, and check if the non-terminal is at the end of the string
            for (int i = 0; i < value.length() - 1; i++) {
                if (!Vt.contains(value.charAt(i)) && value.charAt(i)!='ε') return false; // Non-terminal found before the last character
            }
            // Last character can be a non-terminal as well as terminal, there are no restrictions
            return value.length() <= 1 || Vt.contains(value.charAt(value.length() - 1)) || Vn.contains(value.charAt(value.length() - 1)) ;
        }

        return false;
    }


    private boolean isLeftLinear(String value) {
        //if the production has the rhs of length 1 then it can be either terminal or non-terminal, or even ε
        if(value.length() == 1 && (Vt.contains(value.charAt(0)) || Vn.contains(value.charAt(0)) || value.charAt(0)=='ε')){
            return true;
        }
        //the valid cases for left linear grammar will be if the production is of the form A->Ba or A->a and A->ε, A->Aε is allowed as well
        if (value.length() > 1 && Vn.contains(value.charAt(0)) || value.charAt(0)=='ε' ) {
            //iterate over the entire string, and check if the non-terminal is at the start of the string
            for (int i = 1; i < value.length(); i++) {
                if (!Vt.contains(value.charAt(i))) return false; // Terminal found after the first character
            }
            return true; // First character is a non-terminal followed by terminals
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
