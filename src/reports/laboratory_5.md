# Laboratory Work 5

### Course: Formal Languages & Finite Automata
### Author: Felicia Novac

----

## Theory
Chomsky Normal Form (CNF) is a specific way of structuring the production rules of a context-free grammar. A grammar in CNF has each of its production rules in one of the two forms: `A->BC` or `A->a`. The main reason of converting a type 2 grammar to CNF is the significant simplification of parsing.

The conversion process of a Context-Free Grammar into CNF includes 5 main steps:
1. *ε-productions removal* 
2. *Unit-productions removal*
3. *Inaccessible-symbols removal*
4. *Non-productive states removal*
5. *Conversion to CNF*

## Objectives:
* Learn about Chomsky Normal Form (CNF).
* Get familiar with the approaches of normalizing a grammar.
* Implement a method for normalizing an input grammar by the rules of CNF.
* Implement unit tests that validate the functionality of the project
* Make the CNF conversion extendable to any form of grammar (type 2).

## Implementation description
In order to implement the functionality *conversion to CNF*, I've initialized a new class named `ChomskyNormalForm` that extends the `Grammar` class, consequently inheriting all its public & protected attributes and methods. When instantiating CNF class, we would do so by passing a `Grammar` object created in advance with corresponding Vn, Vt, P and S attributes. 

This conversion, as mentioned before, involves several steps to ensure the grammar meets the specific conditions required by CNF.

### ε-productions removal
The `removeEpsilonProductions` method starts by identifying non-terminals that directly produce epsilon. It does so by checking each production rule in the grammar, and if a production rule contains an epsilon, the non-terminal generating it is added to a set of epsilon-producing non-terminals. 
Once direct producers are identified, the method checks for non-terminals that can produce an epsilon indirectly. This is done by examining each production to see if all the symbols in the production are either known epsilon producers or are themselves epsilons. If so, the non-terminal associated with such a production is also considered an epsilon producer. This check is repeated in a loop until no new epsilon-producing non-terminals are found, ensuring that all possible epsilon producers are accounted for.
After identifying all epsilon producers, the method then adjusts the existing production rules. It excludes epsilon from these rules except where necessary. For each production of each non-terminal, the method generates all possible combinations of the symbols in the production, excluding those combinations that would result in an empty string solely through the removal of epsilon-producing symbols. This is done using a bitmask to consider all subsets of the symbols in each production.
The final step involves reconstructing the grammar. For each non-terminal, new production rules are created from the non-empty combinations generated in the previous step. These new productions replace the old ones, effectively eliminating unnecessary epsilons from the grammar, while preserving its language generation capabilities.





## Conclusions / Screenshots / Results
In order to "simulate" the DSL in function, I take the input commands from the console, and I'll provide some screenshots with valid & invalid input prompts and the corresponding results.

As it can be seen from Table 1 and compared to the Grammar from [2], the only *parts* of the input that are being added to the List of Token objects, are those defined within the *grammar* and *regex*.

|                      Valid Input Prompts                      |                      Invalid Input Prompts                      |
|:-------------------------------------------------------------:|:-----------------------------------------------------------------:|
| <img src="https://github.com/felycianovac/LFA_labs/blob/main/images/valid1.png" width="300"> <img src="https://github.com/felycianovac/LFA_labs/blob/main/images/valid2.png" width="300"> | <img src="https://github.com/felycianovac/LFA_labs/blob/main/images/invalid1.png" width="300"> <img src="https://github.com/felycianovac/LFA_labs/blob/main/images/invalid2.png" width="300"> |
<p align="center">
  <strong>Table 1.</strong> Tokenizer in Action
</p>


In conclusion, I've managed to understand the essence of Lexical Analysis, how to implement it and perform an accurate tokenization with corresponding error validation. It took relatively much time when reviewing all possible input prompts and trying to overcome some possible issues that might occur (hopefully I covered all of them). However, it was interesting to code and see how the Lexer does its job.

P.S. I've implemented lexer the way I saw it the most convenient to work with when parsing, so please do not judge :)



## References
[1] **A Sample of a Lexer Implementation** - Accessed March 13, 2024. [https://llvm.org/docs/tutorial/MyFirstLanguageFrontend/LangImpl01.html](https://llvm.org/docs/tutorial/MyFirstLanguageFrontend/LangImpl01.html).

[2] **DSL Grammar for image processing** - Accessed March 13, 2024. [https://1drv.ms/b/c/06b88d8283f1f472/EcWOdkET6aVOtnGd6-9MG3QB221Cpzyqvsfwg0QWueeGLg?e=m6SUlf](https://1drv.ms/b/c/06b88d8283f1f472/EcWOdkET6aVOtnGd6-9MG3QB221Cpzyqvsfwg0QWueeGLg?e=m6SUlf).

[3] **Introduction to Lexical Analysis** - Accessed March 12, 2024. [https://www.geeksforgeeks.org/introduction-of-lexical-analysis/](https://www.geeksforgeeks.org/introduction-of-lexical-analysis/).
