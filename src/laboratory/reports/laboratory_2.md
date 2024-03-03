# Laboratory Work 2

### Course: Formal Languages & Finite Automata
### Author: Felicia Novac

----

## Theory
- The *Chomsky classification* of Grammar organizes languages into four levels based on their complexity, from simplest to most complex. The 4 types of Grammar are:
  - *Regular Grammar `Type 3`* - the most restricted form of the language, and the only one which is accepted by the FA. The lhs (left-hand side) of the production must have a single non-terminal and the rhs (right-hand side) consisting of a single terminal or a comb between a non-terminal and terminal.
  - *Context-Free Grammar `Type 2`* - the lhs of production can have only one variable and there is no restriction on the rhs.
  - *Context-Sensitive Grammar `Type 1`* - the count of symbols on the lhs must be less or equal to the count of symbols on the rhs.
  - *Unrestricted Grammar `Type 0`* - there are no restrictions, and typically when a Grammar is not of type 3, 2 or 1, it falls into the type 0 category.


- *Non-Deterministic FA* - there is an indeterminancy in the transitions, as with one single terminal, a state can go in more than one states. Also, there might be ε transitions, and in this case automaton is called ε-NFA.
- *Deterministic FA* - inverse of NFA.

- *NFA to DFA Conversion*:

  -  **Step 1:** Initially Q' = ϕ
  -  **Step 2:** Add q0 of NFA to Q'. Then find the transitions from this start state.
  -  **Step 3:** In Q', find the possible set of states for each input symbol. If this set of states is not in Q', then add it to Q'.
  -  **Step 4:** In DFA, the final state will be all the states which contain F(final states of NFA)
- *ε-NFA to DFA conversion* - requires one more step before converting NFA to DFA, which is determining ε-CLOSUREs.



## Objectives:

* Understand what an automaton is and what it can be used for.
* Provide a function in your grammar type/class that could classify the grammar based on Chomsky hierarchy.
* Implement conversion of a finite automaton to a regular grammar.
* Determine whether your FA is deterministic or non-deterministic.
* Implement some functionality that would convert an NDFA to a DFA.
* Represent the finite automaton graphically.
* Maintain mental health while creating FA graphs in Java (*optional*)


## Implementation description

* About 2-3 sentences to explain each piece of the implementation.


* Code snippets from your files.

```
public static void main() 
{

}
```

* If needed, screenshots.


## Conclusions / Screenshots / Results


## References
