package laboratory;

import java.util.*;

public class ChomskyNormalForm extends Grammar{
    public ChomskyNormalForm(Grammar grammar) {
        super(grammar.Vn, grammar.Vt, grammar.P, grammar.S);
        Map<String, List<String>> updatedProductions;
        updatedProductions = removeEpsilonProductions();
        this.P = updatedProductions;

        updatedProductions = removeUnitProductions();
        this.P = updatedProductions;

        updatedProductions = removeInaccessibleSymbols();
        this.P = updatedProductions;
        updatedProductions = removeNonProductiveSymbols();
        this.P = updatedProductions;

        updatedProductions = toCNF();
        this.P = updatedProductions;



    }



    private Map<String, List<String>> removeEpsilonProductions() {
        Set<String> epsilonProducingNonTerminals = new HashSet<>();

        P.forEach((nonTerminal, productions) -> {
            if (productions.contains("ε")) {
                epsilonProducingNonTerminals.add(nonTerminal);
            }
        });

        boolean changesMade;
        do {
            changesMade = false;
            for (Map.Entry<String, List<String>> entry : P.entrySet()) {
                for (String production : entry.getValue()) {
                    if (production.chars().mapToObj(c -> String.valueOf((char) c))
                            .allMatch(c -> epsilonProducingNonTerminals.contains(c) || c.equals("ε"))) {
                        if (epsilonProducingNonTerminals.add(entry.getKey())) {
                            changesMade = true;
                        }
                    }
                }
            }
        } while (changesMade);

        Map<String, List<String>> newProductions = new HashMap<>();
        P.forEach((nonTerminal, productions) -> {
            Set<String> adjustedProductions = new HashSet<>();
            for (String production : productions) {
                if (!production.equals("ε")) {
                    // Generate all combinations excluding epsilon-producing non-terminals
                    for (int i = 0; i < (1 << production.length()); i++) {
                        StringBuilder combination = new StringBuilder();
                        for (int j = 0; j < production.length(); j++) {
                            if ((i & (1 << j)) == 0 || !epsilonProducingNonTerminals.contains(String.valueOf(production.charAt(j)))) {
                                combination.append(production.charAt(j));
                            }
                        }
                        if (combination.length() > 0) {
                            adjustedProductions.add(combination.toString());
                        }
                    }
                }
            }

            newProductions.put(nonTerminal, new ArrayList<>(adjustedProductions));
        });

        return newProductions;
    }
    public Map<String, List<String>> removeUnitProductions() {
        Map<String, List<String>> resultProductions = new HashMap<>();
        Map<String, List<String>> directUnitProductions = new HashMap<>(); // Direct unit productions for easy traversal

        // Identify direct unit and non-unit productions
        P.forEach((nonTerminal, productions) -> {
            productions.forEach(production -> {
                if (production.length() == 1 && Vn.contains(production.charAt(0))) {
                    directUnitProductions.computeIfAbsent(nonTerminal, k -> new ArrayList<>()).add(production);
                } else {
                    resultProductions.computeIfAbsent(nonTerminal, k -> new ArrayList<>()).add(production);
                }
            });
        });

        directUnitProductions.forEach((nonTerminal, unitProductions) -> {
            Set<String> visited = new HashSet<>();
            List<String> toVisit = new ArrayList<>(unitProductions);

            while (!toVisit.isEmpty()) {
                String current = toVisit.remove(toVisit.size() - 1); // Simulate stack behavior
                if (!visited.contains(current)) {
                    visited.add(current);

                    // Add non-unit productions of the current unit production to the original non-terminal
                    List<String> currentProductions = P.get(current);
                    if (currentProductions != null) {
                        currentProductions.forEach(prod -> {
                            if (prod.length() == 1 && Vn.contains(prod.charAt(0))) {
                                if (!visited.contains(prod)) { // Prevent cycles
                                    toVisit.add(prod);
                                }
                            } else {
                                resultProductions.computeIfAbsent(nonTerminal, k -> new ArrayList<>()).add(prod);
                            }
                        });
                    }
                }
            }
        });

        return resultProductions;
    }
        public Map<String, List<String>> removeInaccessibleSymbols() {
            Set<String> accessibleSymbols = new HashSet<>();
            accessibleSymbols.add("S");

            boolean changed = true;
            while (changed) {
                changed = false;
                for (Map.Entry<String, List<String>> entry : P.entrySet()) {
                    String nonTerminal = entry.getKey();
                    if (accessibleSymbols.contains(nonTerminal)) {
                        for (String production : entry.getValue()) {
                            for (int i = 0; i < production.length(); i++) {
                                char symbol = production.charAt(i);
                                if (Vn.contains(symbol) && !accessibleSymbols.contains(String.valueOf(symbol))) {
                                    accessibleSymbols.add(String.valueOf(symbol));
                                    changed = true;
                                }
                            }
                        }
                    }
                }
            }

            Map<String, List<String>> newProductions = new HashMap<>();
            for (Map.Entry<String, List<String>> entry : P.entrySet()) {
                if (accessibleSymbols.contains(entry.getKey())) {
                    newProductions.put(entry.getKey(), new ArrayList<>(entry.getValue()));
                }
            }

            return newProductions;
        }

    public Map<String, List<String>> removeNonProductiveSymbols() {
        Set<String> productiveSymbols = new HashSet<>();
        boolean changed = true;

        while (changed) {
            changed = false;

            for (Map.Entry<String, List<String>> entry : P.entrySet()) {
                String nonTerminal = entry.getKey();
                if (!productiveSymbols.contains(nonTerminal)) {
                    for (String production : entry.getValue()) {
                        boolean productionIsProductive = true;

                        for (int i = 0; i < production.length(); i++) {
                            char sym = production.charAt(i);
                            if (!(Vt.contains(sym) || productiveSymbols.contains(String.valueOf(sym)))) {
                                productionIsProductive = false;
                                break;
                            }
                        }

                        if (productionIsProductive) {
                            productiveSymbols.add(nonTerminal);
                            changed = true;
                        }
                    }
                }
            }
        }

        Map<String, List<String>> newProductions = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : P.entrySet()) {
            if (productiveSymbols.contains(entry.getKey())) {
                List<String> newProductionList = new ArrayList<>();
                for (String production : entry.getValue()) {
                    boolean allSymbolsAreProductive = true;
                    for (int i = 0; i < production.length(); i++) {
                        char sym = production.charAt(i);
                        if (!Vt.contains(sym) && !productiveSymbols.contains(String.valueOf(sym))) {
                            allSymbolsAreProductive = false;
                            break;
                        }
                    }
                    if (allSymbolsAreProductive) {
                        newProductionList.add(production);
                    }
                }
                if (!newProductionList.isEmpty()) {
                    newProductions.put(entry.getKey(), newProductionList);
                }
            }
        }

        return newProductions;
    }

    public Map<String, List<String>> toCNF() {
        Map<String, List<String>> newProductions = new HashMap<>();
        Map<Character, String> terminalReplacements = new HashMap<>();
        Map<String, String> existingProductions = new HashMap<>(); // Map RHS to existing Xn symbols
        int[] newVarCounter = {1}; // Use an array to allow modification

        for (Map.Entry<String, List<String>> entry : P.entrySet()) {
            String lhs = entry.getKey();

            for (String rhs : entry.getValue()) {
                if (rhs.length() == 1) {
                    newProductions.computeIfAbsent(lhs, k -> new ArrayList<>()).add(rhs);
                } else {
                    List<String> modifiedProduction = new ArrayList<>();
                    for (char sym : rhs.toCharArray()) {
                        if (Vt.contains(sym)) {
                            String terminalReplacement = terminalReplacements.get(sym);
                            if (terminalReplacement == null) {
                                terminalReplacement = existingProductions.get(String.valueOf(sym));
                                if (terminalReplacement == null) {
                                    terminalReplacement = "X" + newVarCounter[0]++;
                                    terminalReplacements.put(sym, terminalReplacement);
                                    existingProductions.put(String.valueOf(sym), terminalReplacement);
                                    newProductions.computeIfAbsent(terminalReplacement, x -> new ArrayList<>()).add(String.valueOf(sym));
                                } else {
                                    terminalReplacements.put(sym, terminalReplacement);
                                }
                            }
                            modifiedProduction.add(terminalReplacement);
                        } else {
                            modifiedProduction.add(String.valueOf(sym));
                        }
                    }

                    while (modifiedProduction.size() > 2) {
                        String combinedSymbols = String.join("", modifiedProduction.subList(0, 2));
                        String newVar = existingProductions.get(combinedSymbols);
                        if (newVar == null) {
                            newVar = "X" + newVarCounter[0]++;
                            existingProductions.put(combinedSymbols, newVar);
                            newProductions.put(newVar, List.of(combinedSymbols));
                        }
                        modifiedProduction = new ArrayList<>(modifiedProduction.subList(2, modifiedProduction.size()));
                        modifiedProduction.add(0, newVar);
                    }

                    newProductions.computeIfAbsent(lhs, k -> new ArrayList<>()).add(String.join("", modifiedProduction));
                }
            }
        }

        return newProductions;
    }









    //test main
    public static void main(String[] args) {
        List<Character> Vn = List.of('S', 'A', 'B', 'C', 'D');
        List<Character> Vt = List.of('a', 'b');
        Map<String, List<String>> P = new HashMap<>();
        P.put("S", List.of("aB","AC"));
        P.put("A", List.of("a", "ASC","BC"));
        P.put("B", List.of("b", "bS"));
        P.put("C", List.of("BA", "ε"));


        Character S = 'S';
        Grammar grammar = new Grammar(Vn, Vt, P, S);
        ChomskyNormalForm chomskyNormalForm = new ChomskyNormalForm(grammar);
        chomskyNormalForm.printGrammar();

    }

}
