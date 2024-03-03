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
        Map<Set<Character>, Character> stateSetToDFAState = new HashMap<>();
        Map<Character, Set<Character>> dfaStateToStateSet = new HashMap<>();
        List<Character> dfaStates = new ArrayList<>();
        Map<Character, Map<Character, List<Character>>> dfaTransitions = new HashMap<>();
        List<Character> dfaFinalStates = new ArrayList<>();
        char nextStateName = 'A';

        Set<Character> initialClosure = epsilonClosure(startState, transitions);
        stateSetToDFAState.put(initialClosure, nextStateName);
        dfaStateToStateSet.put(nextStateName, initialClosure);
        dfaStates.add(nextStateName);
        Character dfaStartState = nextStateName++;

        Queue<Set<Character>> queue = new LinkedList<>();
        queue.add(initialClosure);

        while (!queue.isEmpty()) {
            Set<Character> currentSet = queue.poll();
            Character currentState = stateSetToDFAState.get(currentSet);

            Map<Character, List<Character>> transitionMap = new HashMap<>();
            for (Character symbol : alphabet) {
                if (symbol.toString().equals("ε")) continue; // Skip ε-transitions for DFA

                Set<Character> nextSet = new HashSet<>();
                for (Character state : currentSet) {
                    Set<Character> closure = epsilonClosure(state, transitions);
                    for (Character closureState : closure) {
                        List<Character> nextStates = transitions.getOrDefault(closureState, new HashMap<>()).getOrDefault(symbol, new ArrayList<>());
                        for (Character nextState : nextStates) {
                            nextSet.addAll(epsilonClosure(nextState, transitions));
                        }
                    }
                }

                if (!nextSet.isEmpty()) {
                    Character nextState;
                    if (!stateSetToDFAState.containsKey(nextSet)) {
                        nextState = nextStateName++;
                        stateSetToDFAState.put(nextSet, nextState);
                        dfaStateToStateSet.put(nextState, nextSet);
                        dfaStates.add(nextState);
                        queue.add(nextSet);
                    } else {
                        nextState = stateSetToDFAState.get(nextSet);
                    }
                    transitionMap.putIfAbsent(symbol, new ArrayList<>());
                    transitionMap.get(symbol).add(nextState);
                }
            }
            dfaTransitions.put(currentState, transitionMap);
        }

        // Identify final states in DFA
        for (Map.Entry<Character, Set<Character>> entry : dfaStateToStateSet.entrySet()) {
            for (Character finalState : finalStates) {
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
        Set<Character> closure = new HashSet<>();
        Stack<Character> stack = new Stack<>();
        stack.push(state);

        while (!stack.isEmpty()) {
            Character currentState = stack.pop();
            closure.add(currentState);

            List<Character> epsilonTransitions = transitions.getOrDefault(currentState, new HashMap<>()).getOrDefault('ε', new ArrayList<>());
            for (Character nextState : epsilonTransitions) {
                if (!closure.contains(nextState)) {
                    stack.push(nextState);
                }
            }
        }

        return closure;
    }

    public void generatePngRepresentation() {
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
