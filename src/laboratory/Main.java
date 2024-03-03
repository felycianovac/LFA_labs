package laboratory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        //Testing the Grammar Type Identification
//        List<Character> Vn = Arrays.asList('S', 'A', 'B', 'C');
//        List<Character> Vt = Arrays.asList('a', 'b', 'c', 'd');
//        Map<String, List<String>> P = Map.ofEntries(
//                Map.entry("S", Arrays.asList("dA")),
//                Map.entry("A", Arrays.asList("aB", "bA")),
//                Map.entry("B", Arrays.asList("bC", "aB", "d")),
//                Map.entry("C", Arrays.asList("cB"))
//        );
//        Character S = 'S';
//        Grammar grammar = new Grammar(Vn, Vt, P, S);
//        grammar.printGrammar();
//        System.out.println("The given grammar is of the type " + grammar.classifyGrammar());
//
//        List<Character> Vn = Arrays.asList('S', 'A');
//        List<Character> Vt = Arrays.asList('a', 'b', 'c');
//        Map<String, List<String>> P = Map.ofEntries(
//                Map.entry("S", Arrays.asList("Aa")),
//                Map.entry("A", Arrays.asList("a", "aA","abc", "ε"))
//
//        );
//        Character S = 'S';
//        Grammar grammar = new Grammar(Vn, Vt, P, S);
//        grammar.printGrammar();
//        System.out.println("The given grammar is of the type " + grammar.classifyGrammar());


        List<Character> Vn = Arrays.asList('A', 'B');
        List<Character> Vt = Arrays.asList('b', 'c');
        Map<String, List<String>> P = Map.ofEntries(
                Map.entry("A", Arrays.asList("AbBc")),
                Map.entry("AB", Arrays.asList("aB", "bA")),
                Map.entry("B", Arrays.asList("b"))
        );
        Character S = 'S';
        Grammar grammar = new Grammar(Vn, Vt, P, S);
        grammar.printGrammar();
        System.out.println("The given grammar is of the type " + grammar.classifyGrammar());

//        List<Character> Vn = Arrays.asList('S', 'B', 'C');
//        List<Character> Vt = Arrays.asList('a', 'b');
//        Map<String, List<String>> P = Map.ofEntries(
//                Map.entry("S", Arrays.asList("aSBC")),
//                Map.entry("BCS", Arrays.asList("Bb")),
//                Map.entry("C", Arrays.asList("a")),
//                Map.entry("B", Arrays.asList("b","ε"))
//        );
//        Character S = 'S';
//        Grammar grammar = new Grammar(Vn, Vt, P, S);
//        grammar.printGrammar();
//        System.out.println("The given grammar is of the type " + grammar.classifyGrammar());


        //Testing the FA conversion to grammar
        //let S=q0, A=q1, B=q2, C=q3

        List<Character> states = Arrays.asList('S','A','B','C');
        List<Character> alphabet = Arrays.asList('a', 'b');
        List<Character> finalStates = Arrays.asList('C');
        Character startState = 'S';
        Map<Character, Map<Character, List<Character>>> transitions = Map.ofEntries(
                Map.entry('S', Map.ofEntries(
                        Map.entry('a', Arrays.asList('S','A'))
                )),
                Map.entry('A', Map.ofEntries(
                        Map.entry('b', Arrays.asList('A')),
                        Map.entry('a', Arrays.asList('B'))
                )),
                Map.entry('B', Map.ofEntries(
                        Map.entry('b', Arrays.asList('C')),
                        Map.entry('a', Arrays.asList('S'))
                )),
                Map.entry('C', Map.ofEntries(
                ))
        );
//
        FiniteAutomaton finiteAutomaton = new FiniteAutomaton(states, alphabet, transitions, startState, finalStates);
//        finiteAutomaton.nfa_to_dfa().generatePngRepresentation();
//        System.out.println("The given finite automaton has the following grammar:");
//        finiteAutomaton.toRegularGrammar().printGrammar();
//        finiteAutomaton.displayAutomaton();
//        finiteAutomaton.is_dfa();
//        if (finiteAutomaton.is_dfa()){
//            System.out.println("The given finite automaton is a DFA");
//        } else {
//            System.out.println("The given finite automaton is a NFA");
//        }
//        System.out.println("The given finite automaton has the following DFA structure:");
        finiteAutomaton.nfa_to_dfa().generatePngRepresentation();

//        finiteAutomaton.generatePngRepresentation();



    }


}


