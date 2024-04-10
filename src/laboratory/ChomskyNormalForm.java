package laboratory;

import java.util.*;

public class ChomskyNormalForm extends Grammar{
    public ChomskyNormalForm(Grammar grammar) {
        super(grammar.Vn, grammar.Vt, grammar.P, grammar.S);
    }

    private Map<List<String>, List<String>> removeEpsilonProductions() {
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

        Map<List<String>, List<String>> newProductions = new HashMap<>();
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

            newProductions.put(List.of(nonTerminal), new ArrayList<>(adjustedProductions));
        });

        return newProductions;
    }
    public Map<List<String>, List<String>> removeUnitProductions() {
        Map<List<String>, List<String>> resultProductions = new HashMap<>();
        Map<String, List<String>> directUnitProductions = new HashMap<>(); // Direct unit productions for easy traversal

        // Identify direct unit and non-unit productions
        P.forEach((nonTerminal, productions) -> {
            List<String> key = List.of(nonTerminal);
            productions.forEach(production -> {
                if (production.length() == 1 && Vn.contains(production.charAt(0))) {
                    directUnitProductions.computeIfAbsent(nonTerminal, k -> new ArrayList<>()).add(production);
                } else {
                    resultProductions.computeIfAbsent(key, k -> new ArrayList<>()).add(production);
                }
            });
        });

        // Process each non-terminal for unit productions
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
                                resultProductions.computeIfAbsent(List.of(nonTerminal), k -> new ArrayList<>()).add(prod);
                            }
                        });
                    }
                }
            }
        });

        return resultProductions;
    }







    //test main
    public static void main(String[] args) {
        List<Character> Vn = List.of('S', 'T','F');
        List<Character> Vt = List.of('+', '*', '(', ')', 'a');
        Map<String, List<String>> P = new HashMap<>();
        P.put("S", List.of("T", "S+T"));
        P.put("T", List.of("F", "T*F"));
        P.put("F", List.of("(S)", "a"));


        Character S = 'S';
        Grammar grammar = new Grammar(Vn, Vt, P, S);
        ChomskyNormalForm chomskyNormalForm = new ChomskyNormalForm(grammar);
        System.out.println(chomskyNormalForm.removeUnitProductions());
    }


}
