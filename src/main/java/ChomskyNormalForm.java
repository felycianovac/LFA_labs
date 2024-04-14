import java.util.*;
import java.util.stream.Collectors;

public class ChomskyNormalForm extends Grammar {
    public ChomskyNormalForm(Grammar grammar) {
        super(grammar.Vn, grammar.Vt, grammar.P, grammar.S);
    }

    public Map<String, List<String>> removeEpsilonProductions() {
        // Initialize a set to store non-terminals that can produce epsilon directly or indirectly
        Set<String> epsilonProducingNonTerminals = new HashSet<>();

        // Iterate over map to identify non-terminals that directly produce epsilon
        P.forEach((nonTerminal, productions) -> {
            if (productions.contains("ε")) {
                epsilonProducingNonTerminals.add(nonTerminal);
            }
        });

        boolean changesMade;
        do {
            changesMade = false;
            // Check for indirect epsilon productions
            for (Map.Entry<String, List<String>> entry : P.entrySet()) {
                for (String production : entry.getValue()) {
                    // If all characters in a production are either epsilon or can themselves produce epsilon
                    if (production.chars().mapToObj(c -> String.valueOf((char) c))
                            .allMatch(c -> epsilonProducingNonTerminals.contains(c) || c.equals("ε"))) {
                        // Add the non-terminal to the epsilon producing set if it's not already there
                        if (epsilonProducingNonTerminals.add(entry.getKey())) {
                            changesMade = true;
                        }
                    }
                }
            }
        } while (changesMade);

        // Map to store the adjusted productions after epsilon removal
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
                // Check if the production is a unit production (length == 1 and the character is a non-terminal).
                if (production.length() == 1 && Vn.contains(production.charAt(0))) {
                    // Store unit productions in a separate map for further processing.
                    directUnitProductions.computeIfAbsent(nonTerminal, k -> new ArrayList<>()).add(production);
                } else {
                    // Store all other productions directly in the result map.
                    resultProductions.computeIfAbsent(nonTerminal, k -> new ArrayList<>()).add(production);
                }
            });
        });
        // Process each non-terminal's unit productions to resolve indirect unit productions.
        directUnitProductions.forEach((nonTerminal, unitProductions) -> {
            Set<String> visited = new HashSet<>(); // Track visited non-terminals to avoid cycles.
            List<String> toVisit = new ArrayList<>(unitProductions); // List of unit productions to process.

            while (!toVisit.isEmpty()) {
                // Remove the last element to simulate stack behavior, processing each unit production.
                String current = toVisit.remove(toVisit.size() - 1);
                if (!visited.contains(current)) {
                    visited.add(current); // Mark this non-terminal as visited.

                    // Retrieve the productions of the current non-terminal from the main production map.
                    List<String> currentProductions = P.get(current);
                    if (currentProductions != null) {
                        currentProductions.forEach(prod -> {
                            // Check if the production is still a unit production.
                            if (prod.length() == 1 && Vn.contains(prod.charAt(0))) {
                                // Add to the processing list if it hasn't been visited to prevent cycles.
                                if (!visited.contains(prod)) { // Prevent cycles
                                    toVisit.add(prod);
                                }
                            } else {
                                // Add non-unit productions to the result under the original non-terminal.
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

        // Flag to track if any new symbols were added during the current iteration.
        boolean changed = true;
            while (changed) {
                changed = false; // Reset change indicator for this iteration.
                // Iterate through all production rules in the grammar.
                for (Map.Entry<String, List<String>> entry : P.entrySet()) {
                    String nonTerminal = entry.getKey();
                    // Check if the current non-terminal is accessible.
                    if (accessibleSymbols.contains(nonTerminal)) {
                        // Iterate over each production of the current non-terminal.
                        for (String production : entry.getValue()) {
                            // Iterate through each character in the production.
                            for (int i = 0; i < production.length(); i++) {
                                char symbol = production.charAt(i);
                                // Check if the symbol is a non-terminal and not already marked as accessible.
                                if (Vn.contains(symbol) && !accessibleSymbols.contains(String.valueOf(symbol))) {
                                    // If new, non-accessible non-terminal found, add it to the set of accessible symbols.
                                    accessibleSymbols.add(String.valueOf(symbol));
                                    changed = true;
                                }
                            }
                        }
                    }
                }
            }
        // List to hold all non-terminals that are still accessible after removal.
        List<Character> newVn = new ArrayList<>();
        Map<String, List<String>> newProductions = new HashMap<>();
        // Iterate over all entries in the original productions.
        for (Map.Entry<String, List<String>> entry : P.entrySet()) {
                if (accessibleSymbols.contains(entry.getKey())) {
                    // Add each accessible non-terminal to the new list of non-terminals.
                    newVn.add(entry.getKey().charAt(0));
                    // Add the productions of each accessible non-terminal to the new productions map.
                    newProductions.put(entry.getKey(), new ArrayList<>(entry.getValue()));
                }
            }
        // Update the set of non-terminals in the grammar to only include those that are accessible.

        Vn = newVn;

            return newProductions;
    }

    public Map<String, List<String>> removeNonProductiveSymbols() {
        Set<String> productiveSymbols = new HashSet<>();
        // Flag to monitor changes in the set of productive symbols during each iteration.
        boolean changed = true;

        // Continue the loop as long as new productive symbols are being added.
        while (changed) {
            changed = false; // Reset the flag for the current iteration.

            // Iterate over all production rules in the grammar.
            for (Map.Entry<String, List<String>> entry : P.entrySet()) {
                String nonTerminal = entry.getKey();
                // Only process the non-terminal if it has not been identified as productive yet.
                if (!productiveSymbols.contains(nonTerminal)) {
                    // Check each production of this non-terminal.
                    for (String production : entry.getValue()) {
                        boolean productionIsProductive = true; // Assume the production is productive until proven otherwise.

                        // Check each symbol in the production.
                        for (int i = 0; i < production.length(); i++) {
                            char sym = production.charAt(i);
                            // Determine if the symbol is either a terminal or a known productive non-terminal.
                            if (!(Vt.contains(sym) || productiveSymbols.contains(String.valueOf(sym)))) {
                                productionIsProductive = false;
                                break;
                            }
                        }
                        // If the production is deemed productive, add the non-terminal to the productive set.

                        if (productionIsProductive) {
                            productiveSymbols.add(nonTerminal);
                            changed = true;
                        }
                    }
                }
            }
        }

        // Prepare a new map for storing productions that only include productive symbols.
        Map<String, List<String>> newProductions = new HashMap<>();
        // Iterate over all entries in the original productions map.
        for (Map.Entry<String, List<String>> entry : P.entrySet()) {
            if (productiveSymbols.contains(entry.getKey())) {
                List<String> newProductionList = new ArrayList<>();
                // Check each production for the productive non-terminal.
                for (String production : entry.getValue()) {
                    boolean allSymbolsAreProductive = true; // Assume all symbols are productive.
                    for (int i = 0; i < production.length(); i++) {
                        char sym = production.charAt(i);
                        // Check if every symbol in the production is productive.
                        if (!Vt.contains(sym) && !productiveSymbols.contains(String.valueOf(sym))) {
                            allSymbolsAreProductive = false; // Mark the production as non-productive.
                            break;
                        }
                    }
                    // If all symbols in the production are productive, add to the new production list.
                    if (allSymbolsAreProductive) {
                        newProductionList.add(production);
                    }
                }
                // If the new production list for the non-terminal is not empty, add it to the new productions map.
                if (!newProductionList.isEmpty()) {
                    newProductions.put(entry.getKey(), newProductionList);
                }
            }
        }
        Vn = productiveSymbols.stream().map(s -> s.charAt(0)).collect(Collectors.toList());


        return newProductions;
    }

    public Map<String, List<String>> toCNF() {
        Map<String, List<String>> newProductions = new HashMap<>();
        // Map to keep track of how terminals are replaced by new non-terminal symbols.
        Map<Character, String> terminalReplacements = new HashMap<>();
        // Map to link already existing combinations of symbols to new non-terminal symbols.
        Map<String, String> existingProductions = new HashMap<>(); // Map RHS to existing Xn symbols
        // Set to track all new and existing non-terminals to ensure uniqueness.
        Set<Character> newNonTerminals = new HashSet<>(Vn);
        // Start character for new variables; it will increment if the character is already used.
        char newVar = 'A';
        while (newNonTerminals.contains(newVar)) {
            newVar++;
        }

        // Iterate over each production rule in the original grammar.
        for (Map.Entry<String, List<String>> entry : P.entrySet()) {
            String lhs = entry.getKey();

            // Iterate over each right-hand side (RHS) production of the current left-hand side (LHS).
            for (String rhs : entry.getValue()) {
                // Check if the RHS is a terminal symbol or a combination of two non-terminal symbols.
                if ((rhs.length() == 1 && Vt.contains(rhs.charAt(0))) || (rhs.length()==2 && Vn.contains(rhs.charAt(0)) && Vn.contains(rhs.charAt(1)))) {
                    newProductions.computeIfAbsent(lhs, k -> new ArrayList<>()).add(rhs);
                } else {
                    // List to modify the production to make it CNF compliant.
                    List<String> modifiedProduction = new ArrayList<>();
                    for (char sym : rhs.toCharArray()) {
                        if (Vt.contains(sym)) { // If the symbol is a terminal.
                            String terminalReplacement = terminalReplacements.get(sym);
                            if (terminalReplacement == null) {
                                terminalReplacement = existingProductions.get(String.valueOf(sym));
                                if (terminalReplacement == null) {
                                    // Create a new non-terminal for the terminal symbol if none exists.
                                    terminalReplacement = String.valueOf(newVar);
                                    newNonTerminals.add(newVar);
                                    terminalReplacements.put(sym, terminalReplacement);
                                    existingProductions.put(String.valueOf(sym), terminalReplacement);
                                    newProductions.computeIfAbsent(terminalReplacement, x -> new ArrayList<>()).add(String.valueOf(sym));

                                    do {
                                    newVar++;
                                } while (newNonTerminals.contains(newVar));
                            }
                                 else {
                                    terminalReplacements.put(sym, terminalReplacement);
                                }
                            }
                            modifiedProduction.add(terminalReplacement);
                        } else {
                            modifiedProduction.add(String.valueOf(sym));
                        }
                    }

                    // Reduce any long sequences of non-terminals to pairs by introducing new non-terminals.
                    while (modifiedProduction.size() > 2) {
                        String combinedSymbols = String.join("", modifiedProduction.subList(0, 2));
                        String newVarStr = existingProductions.get(combinedSymbols);
                        if (newVarStr == null) {
                            newVarStr = String.valueOf(newVar);
                            newNonTerminals.add(newVar);
                            existingProductions.put(combinedSymbols, newVarStr);
                            newProductions.put(newVarStr, List.of(combinedSymbols));

                            do {
                                newVar++;
                            } while (newNonTerminals.contains(newVar));
                        }
                        //Remove the first two symbols and add the new non-terminal to the beginning of the list.
                        modifiedProduction = new ArrayList<>(modifiedProduction.subList(2, modifiedProduction.size()));
                        modifiedProduction.add(0, newVarStr);
                    }

                    newProductions.computeIfAbsent(lhs, k -> new ArrayList<>()).add(String.join("", modifiedProduction));
                }
            }
        }
        Vn = new ArrayList<>(newNonTerminals);

        return newProductions;
    }

}
