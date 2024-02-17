package lab_1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FiniteAutomaton {
    List<Character> states;
    List<Character> alphabet;
    Map<Character, Map<Character, Character>> transitions;
    Character startState;
    List<Character> finalStates;
    public FiniteAutomaton(List<Character> states, List<Character> alphabet, Map<Character, Map<Character, Character>> transitions, Character startState, List<Character> finalStates) {
        this.states = new ArrayList<>(states);
        this.alphabet = new ArrayList<>(alphabet);
        this.transitions = transitions;
        this.startState = startState;
        this.finalStates = finalStates;
        // Add an explicit final state 'F' to represent acceptance for productions ending in terminal symbols
        this.states.add('F');
    }
    public void displayAutomaton() {
        System.out.println("Finite Automaton Structure:");
        System.out.println("States: " + states);
        System.out.println("Alphabet: " + alphabet);
        System.out.println("Start State: " + startState);
        System.out.println("Final States: " + finalStates);
        System.out.println("Transitions: ");
        for (Map.Entry<Character, Map<Character, Character>> entry : transitions.entrySet()) {
            Character state = entry.getKey();
            for (Map.Entry<Character, Character> trans : entry.getValue().entrySet()) {
                Character input = trans.getKey();
                Character nextState = trans.getValue();
                System.out.println("    " + state + " --(" + input + ")--> " + nextState);
            }
        }
    }

    public boolean stringBelongsToLanguage(String inputString) {
        Character currentState = startState; // Start from the initial state

        for (int i = 0; i < inputString.length(); i++) {
            Character currentLetter = inputString.charAt(i);

            // Check if the current letter is part of the alphabet
            if (!alphabet.contains(currentLetter)) {
                System.out.println("ERROR: Letter '" + currentLetter + "' not in the alphabet.");
                return false; // Letter not in alphabet
            }

            // Retrieve the transition rules for the current state.
            Map<Character, Character> currentTransitions = transitions.get(currentState);
            // Check if there is a transition rule for the current letter in the current state.
            if (currentTransitions != null && currentTransitions.containsKey(currentLetter)) {
                // Update the current state based on the transition rule.
                currentState = currentTransitions.get(currentLetter);
            } else {
                // If no transition rule is found for the current letter, print an error message and return false.
                //System.out.println("No transition found for state " + currentState + " with input " + currentLetter);
                return false; // Transition not found
            }
        }

        // Check if the current state after processing the string is one of the final states
        return finalStates.contains(currentState);
    }

}
