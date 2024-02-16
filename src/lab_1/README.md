# Laboratory Work 1

### Course: Formal Languages & Finite Automata
### Author: Felicia Novac

----

## Theory
You are about to go on a turbocharged tour of what's happening in this laboratory work. I promise to keep it snappy because, let's face it, nobody has enough time today, and I'm betting you're scrolling through this in between sips of coffee. So, fasten your seatbelt and let's get started.

#### Essentials: 
- *Grammar* - set of rules defining how to form a language. It is consisted of the (Vn, Vt, P, S) tuple.
  - *Non-terminals (Vn)* - uppercase letters, used to derive into other piece of text
  - *Terminals* - lowercase letters which form the strings of the language.
  - *Productions* - rules on how the non-terminals should be converted into terminals and other non-terminals
  - *Starting Symbol* - equivalent of S, the name speaks for itself


- *Finite Automata* - is like a robot that follows a path based on signals, evolving through states until it reaches an end or stops. It is also consisted of a similar, but at the same time different tuple (Q, Sigma, Delta, q0, F).
  - *Q* - set of states, equivalent to (Vn + additional state)
  - *Sigma* - equivalent to Vt
  - *Delta* - transition function
  - *q0* - S
  - *F* = additional state from Q


## Objectives:

* Populate the class Grammar with the variant
* Implement the generate_string() method in Grammar class
* Construct the blueprint of automaton
* Implement the toFiniteAutomaton() method in Grammar class
* Implement the stringBelongToLanguage(String string) method in the FiniteAutomata class
* Understand the basic concepts of Grammar & Finite Automata

## Implementation description

I have stored the Vn & Vt in lists of Characters, and the productions in the map, as I saw it as the most convenient way to link a Character (non-terminal) to a list of Strings (combo between Vn & Vt).

The next adressed task was to generate strings from the staff that we already have, but not implementing a specific length or any additional stop conditions. So, basically, I started from the prompt S, and looked for the rules in the map for corresponding non-terminal symbol (extract the corresponding list), and then pick one of them at random. Then, via a for loop I go through the list of productions for corresponding non-terminal, and then check eacch element of the String for Vn appartenence. If Vn does not contain the given symbol, I append it via a StringBuilder, else I call recursively the same function for the new non-terminal.

```
private String generateString(Character symbol) {
  if (Vt.contains(symbol)) {
    return symbol.toString();
  }
  List<String> productions = P.get(symbol);
  String production = productions.get(random.nextInt(productions.size()));
  StringBuilder result = new StringBuilder();
  for (char c : production.toCharArray()) {
    if(Vn.contains(c)){ 
      result.append(generateString(c));
    } else {
      result.append(c);
    }
  }
  return result.toString();
    }
```

Next, I defined the FiniteAutomaton class, I've decided to use lists for representing states, alphabet and finalStates (as it might be more than one). The transitions are represented in a Map with key Character and Value another Map of Characters, for convenience, in order to treat non-terminals and terminals separately (each non-terminal is assigned to a map of a key-terminal with value non-terminal/or not), which means treating the states and transitions separately. Comparing to grammar, where the behaviour of the string creation is obvious, I've seen FA as a more complex structure, and avoided using List in this case.

In order to convert the Grammar to FA, I simply converted the defined grammar elements into the FA elements. So, for the Sigma and q0, nothing really changes, while for the Q, we convert Vn into another list and additionally add F (final state). And here goes the most interesting part, in order to define delta, we can first iterate through all Vn's and put them in the map as keys, with an empty map-value for each. Then, in the same loop, we extract the productions (a List) for each Vn, and now iterate through these productions in order to split the terminal from non-terminal and put them in the inner map. So the inner map will contain the key-terminal symbol and value-corresponding non-terminal or F. An important moment here is to check the grammar for regularity, which is done by checking the terminals to be on the left side,  as this is a Right Linear Grammar.
```
Map<Character, Map<Character, Character>> faTransitions = new HashMap<>();

for (Character state : Vn) {
  faTransitions.put(state, new HashMap<>());
  List<String> productions = P.get(state);

  for (String production : productions) {
    char input = production.charAt(0); // Terminal symbol
    Character nextState = production.length() > 1 ? production.charAt(1) : 'F'; 
     if (!Vt.contains(input)) {
        continue; 
       }
faTransitions.get(state).put(input, nextState);
```
The last part of the lab requires the contruction of the method for checking string apartenence to the language. So, basically, I've tried to transpose the algorithm from the courses into code. First, we check whether each letter in the string belongs to the alphabet via a for-loop, if not then there is no sense of checking further. Next, in the same loop, starting with the first state, retrieve the transition rules for the state and check if there is a transition rule for the current letter in the current state, if not - the string does not belong to language, but if yes - continue by updating the current state based on the transition rule (going into the next state). This will take place until we go through the entire length of the String. When we reach the end, we check whether the state that we belong to is one of the final ones, and it is all done.
```

for (int i = 0; i < inputString.length(); i++) {
  Character currentLetter = inputString.charAt(i);

  Map<Character, Character> currentTransitions = transitions.get(currentState);
  if (currentTransitions != null && currentTransitions.containsKey(currentLetter)) {
    currentState = currentTransitions.get(currentLetter);
  } else {
     System.out.println("No transition found for state " + currentState + " with input " + currentLetter);
    return false; // Transition not found
    }
}
```

## Conclusions / Screenshots / Results


