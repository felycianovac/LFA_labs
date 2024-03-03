package laboratory;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
        //iterate over the transitions
        for (Map.Entry<Character, Map<Character, List<Character>>> stateTransitionsEntry : transitions.entrySet()) {
            //retrieve each state for the transitions map
            Character state = stateTransitionsEntry.getKey();
            //list to hold the productions for each state
            List<String> stateProductions = new ArrayList<>();
            //iterate over the inner map
            for (Map.Entry<Character, List<Character>> transition : stateTransitionsEntry.getValue().entrySet()) {
                //retrieve the input symbol and the next state
                Character inputSymbol = transition.getKey();
                //if there are multiple next states for the input symbol, add each of them to the productions
                for (Character nextState : transition.getValue()) {
                    stateProductions.add(inputSymbol + nextState.toString());
                }
            }
            // Add ε (empty) production for final states
            if (finalStates.contains(state)) {
                stateProductions.add("ε"); // ε-production for final states
            }
            // Add the state to itself for transitions not defined for the state
            productions.put(state.toString(), stateProductions);
        }

        return new Grammar(new ArrayList<>(states), alphabet, productions, startState);
    }

    public boolean is_dfa() {
        // Check for ε (empty)-transitions first
        for (Map.Entry<Character, Map<Character, List<Character>>> stateTransitionsEntry : transitions.entrySet()) {
            for (Map.Entry<Character, List<Character>> transition : stateTransitionsEntry.getValue().entrySet()) {
                // If any input symbol maps to more than one state, it's nondeterministic
                if (transition.getValue().size() > 1 || transition.getValue().toString().equals("ε") ) {
                    return false;
                }
            }
        }
        return true;
    }

    public FiniteAutomaton nfa_to_dfa() {
        //keep track of the states created during the conversion
        //each key is a set of states in the NFA and the value is the corresponding state in the DFA
        Map<Set<Character>, Character> stateSetToDFAState = new HashMap<>();
        //inverse of the above map
        //each key is a state in the DFA and the value is the set of states in the NFA
        Map<Character, Set<Character>> dfaStateToStateSet = new HashMap<>();
        //list to hold the states in the DFA
        List<Character> dfaStates = new ArrayList<>();
        //map to hold the transitions in the DFA
        Map<Character, Map<Character, List<Character>>> dfaTransitions = new HashMap<>();
        List<Character> dfaFinalStates = new ArrayList<>();
        char nextStateName = 'A';

        // Compute ε-closure for the start state
        Set<Character> initialClosure = epsilonClosure(startState, transitions);
        // Add the initial state to the state set to DFA state map
        stateSetToDFAState.put(initialClosure, nextStateName);
        // Add the initial state to the DFA state to state set map
        dfaStateToStateSet.put(nextStateName, initialClosure);
        // Add the initial state to the list of states in the DFA
        dfaStates.add(nextStateName);
        // The start state of the DFA is the ε-closure of the start state of the NFA
        Character dfaStartState = nextStateName++;

        // Add the initial state to the queue
        Queue<Set<Character>> queue = new LinkedList<>();
        queue.add(initialClosure);

        // Process the states in the queue
        while (!queue.isEmpty()) {
            // Return and remove the current state set from the queue
            Set<Character> currentSet = queue.poll();
            // Retrieve the corresponding state in the DFA
            Character currentState = stateSetToDFAState.get(currentSet);
            // Map transitions for the current state in the DFA
            Map<Character, List<Character>> transitionMap = new HashMap<>();
            // Iterate over the alphabet to compute transitions for the current state
            for (Character symbol : alphabet) {
                // Compute the next state set for the current symbol
                Set<Character> nextSet = new HashSet<>();
                // Iterate over the states in the current set to compute the next set
                for (Character state : currentSet) {
                    // Compute the ε-closure for each state in the current set
                    Set<Character> closure = epsilonClosure(state, transitions);
                    // Compute the next states for the current symbol
                    for (Character closureState : closure) {
                        // Retrieve the next states for the current symbol
                        List<Character> nextStates = transitions.getOrDefault(closureState, new HashMap<>()).getOrDefault(symbol, new ArrayList<>());
                        // Add the next states to the next set
                        for (Character nextState : nextStates) {
                            nextSet.addAll(epsilonClosure(nextState, transitions));
                        }
                    }
                }
                // If the next set is not empty, add it to the DFA
                if (!nextSet.isEmpty()) {
                    // Check if the next set is already in the DFA
                    Character nextState;
                    // If the next set is not in the DFA, add it to the DFA
                    if (!stateSetToDFAState.containsKey(nextSet)) {
                        // Add the next set to the state set to DFA state map
                        nextState = nextStateName++;
                        // Add the next set to the DFA state to state set map
                        stateSetToDFAState.put(nextSet, nextState);
                        // Add the next set to the list of states in the DFA
                        dfaStateToStateSet.put(nextState, nextSet);
                        // Add the next state to the queue
                        dfaStates.add(nextState);
                        queue.add(nextSet);
                    } else {
                        // Retrieve the next state from the state set to DFA state map
                        nextState = stateSetToDFAState.get(nextSet);
                    }
                    // Add the next state to the transition map for the current symbol
                    transitionMap.putIfAbsent(symbol, new ArrayList<>());
                    transitionMap.get(symbol).add(nextState);
                }
            }
            // Add the transition map for the current state to the DFA transitions
            dfaTransitions.put(currentState, transitionMap);
        }

        // Identify final states in DFA
        for (Map.Entry<Character, Set<Character>> entry : dfaStateToStateSet.entrySet()) {
            for (Character finalState : finalStates) {
                // If the final state of the NFA is in the set of states in the DFA, the DFA state is a final state
                if (entry.getValue().contains(finalState)) {
                    dfaFinalStates.add(entry.getKey());
                    break;
                }
            }
        }

        return new FiniteAutomaton(dfaStates, alphabet, dfaTransitions, dfaStartState, dfaFinalStates);
    }


    // Method to compute ε-closure for a set of states
    private Set<Character> epsilonClosure(Character state, Map<Character, Map<Character, List<Character>>> transitions) {
        //a set to hold the closures
        Set<Character> closure = new HashSet<>();
        //a stack to explore the states
        Stack<Character> stack = new Stack<>();
        //push the initial state to the stack
        stack.push(state);

        //while the stack is not empty
        while (!stack.isEmpty()) {
            //pop the current state from the stack
            Character currentState = stack.pop();
            //add the current state to the closure set
            closure.add(currentState);
            //get the ε-transitions for the current state
            List<Character> epsilonTransitions = transitions.getOrDefault(currentState, new HashMap<>()).getOrDefault('ε', new ArrayList<>());
            //for each of the ε-transitions, if it is not already in the closure set, add it to the stack
            for (Character nextState : epsilonTransitions) {
                //if the closure set does not contain the next state, add it to the stack
                if (!closure.contains(nextState)) {
                    stack.push(nextState);
                }
            }
        }

        return closure;
    }

    public void generatePngRepresentation() {
        // Generate the PlantUML source for the finite automaton
        String source = "@startuml\n";
        source += "hide empty description\n";

        // Define states and mark final states
        for (Character state : states) {
            if (finalStates.contains(state)) {
                source += "state \"" + state + "\" as " + state + " <<final>>\n";
            } else {
                source += "state \"" + state + "\" as " + state + "\n";
            }
        }

        // Initial state arrow
        source += "[*] --> " + startState + "\n";

        // Define transitions
        for (Map.Entry<Character, Map<Character, List<Character>>> entry : transitions.entrySet()) {
            Character fromState = entry.getKey();
            for (Map.Entry<Character, List<Character>> trans : entry.getValue().entrySet()) {
                Character symbol = trans.getKey();
                for (Character toState : trans.getValue()) {
                    source += fromState + " --> " + toState + " : " + symbol + "\n";
                }
            }
        }

        for (Character state : finalStates) {
            source += state + " --> [*]\n";
        }
        source += "@enduml\n";


        try (OutputStream png = new FileOutputStream("images"+ File.separator +"generated_finite_automaton.png")) {

            SourceStringReader reader = new SourceStringReader(source);
            String desc = reader.generateImage(png);
            if (desc != null) {
                System.out.println("Image saved as finite_automaton.png");
            } else {

                System.out.println("Failed to generate image.");
            }
        } catch (IOException e) {
        }
    }
}
