package lab_1;

import java.util.*;

public class FiniteAutomaton {
    List<Character> states;
    List<Character> alphabet;
    Map<Character, Map<Character, List<Character>>> transitions;
    Character startState;
    List<Character> finalStates;
    public FiniteAutomaton(List<Character> states, List<Character> alphabet, Map<Character, Map<Character, List<Character>>> transitions, Character startState, List<Character> finalStates) {
        this.states = new ArrayList<>(states);
        this.alphabet = new ArrayList<>(alphabet);
        this.transitions = transitions;
        this.startState = startState;
        this.finalStates = finalStates;
        // Add an explicit final state 'F' to represent acceptance for productions ending in terminal symbols
        this.states = new ArrayList<>(states);
    }
    public void displayAutomaton() {
        System.out.println("Finite Automaton Structure:");
        System.out.println("States: " + states);
        System.out.println("Alphabet: " + alphabet);
        System.out.println("Start State: " + startState);
        System.out.println("Final States: " + finalStates);
        System.out.println("Transitions: ");
        for (Map.Entry<Character, Map<Character, List<Character>>> entry : transitions.entrySet()) {
            Character state = entry.getKey();
            for (Map.Entry<Character, List<Character>> trans : entry.getValue().entrySet()) {
                Character input = trans.getKey();
                List<Character> nextStates = trans.getValue();
                for (Character nextState : nextStates) {
                    System.out.println("    " + state + " --(" + input + ")--> " + nextState);
                }
            }
        }
    }


    public boolean stringBelongsToLanguage(String inputString) {
        Set<Character> currentStates = new HashSet<>(); // Start from the initial state
        currentStates.add(startState);
        finalStates.add('F');
        for (int i = 0; i < inputString.length(); i++) {
            Character currentLetter = inputString.charAt(i);

            // Check if the current letter is part of the alphabet
            if (!alphabet.contains(currentLetter)) {
                System.out.println("ERROR: Letter '" + currentLetter + "' not in the alphabet.");
                return false; // Letter not in alphabet
            }

            // Retrieve the transition rules for the current state.
            Set<Character> newStates = new HashSet<>();
            // Check if there is a transition rule for the current letter in the current state.
            // Process transitions for each of the current states
            for (Character state : currentStates) {
                // Retrieve the transition rules for the current state.
                Map<Character, List<Character>> currentTransitions = transitions.get(state);

                // Check if there is a transition rule for the current letter in the current state.
                if (currentTransitions != null && currentTransitions.containsKey(currentLetter)) {
                    // Add all possible next states to the new states set
                    newStates.addAll(currentTransitions.get(currentLetter));
                }
            }

            // Update the current states to the new states for the next iteration
            currentStates = newStates;

            // If no transitions were found for the current letter, return false.
            if (currentStates.isEmpty()) {
                return false; // Transition not found
            }
        }

        // Check if any of the current states after processing the string is a final state
        for (Character state : currentStates) {
            if (finalStates.contains(state)) {
                return true;
            }
        }

        return false; // None of the current states is a final state
    }

    public Grammar toRegularGrammar() {
        Map<String, List<String>> productions = new HashMap<>();

        for (Map.Entry<Character, Map<Character, List<Character>>> stateTransitionsEntry : transitions.entrySet()) {
            Character state = stateTransitionsEntry.getKey();
            List<String> stateProductions = new ArrayList<>();

            for (Map.Entry<Character, List<Character>> transition : stateTransitionsEntry.getValue().entrySet()) {
                Character inputSymbol = transition.getKey();
                for (Character nextState : transition.getValue()) {
                    stateProductions.add(inputSymbol + nextState.toString());
                }
            }

            if (finalStates.contains(state)) {
                stateProductions.add("ε"); // ε-production for final states
            }

            productions.put(state.toString(), stateProductions);
        }

        // Construct and return the Grammar object
        // Assuming you have a constructor for Grammar that takes these parameters
        return new Grammar(new ArrayList<>(states), alphabet, productions, startState);
    }

}
