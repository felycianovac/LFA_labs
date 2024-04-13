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
The `removeEpsilonProductions` method starts with identifying non-terminals that directly produce epsilon by iterating over the productions with the use of the forEach() method for Maps and store the ones that `.contains("ε")` in a `Set<String> epsilonProducingNonTerminals`. 
Once the states producing epsilon are identified, the method checks for non-terminals that can produce an epsilon indirectly. This is done by iterating over the `Map<String, List<String>> P` entries and for each entry, over the corresponding list of values (productions). Each `char` of every production is being verified for belongness to `epsilonProducingNonTerminals` or even being equal to "ε", and if so it is added to the same Set. 
After identifying all epsilon producers, we have to adjust the existing production rules that contain those producers. We do so by iterating once again over the productions in the Map and for each production excluding ε in the productions List we generate all possible combinations of symbols in a production string. This is achieved through a for loop that runs from 0 to 2^(production.length()) - 1, representing every possible subset of the production's characters. Each subset is represented by a binary number, where each bit indicates whether the corresponding character is included (1) or excluded (0).
For each subset, we iterate over each character in the production combination using another for loop.  Then, we perform the bitwise check `(i & (1 << j)) == 0` to find out  whether a character in the production string should be excluded from the current subset being generated. Along with bitwise check goes `!epsilonProducingNonTerminals.contains(String.valueOf(production.charAt(j)))`, which ensures that the character, if included, is not an epsilon-producing non-terminal. Each character corresponding to at least one of those conditions is appended to a StringBuilder object, building a new production string.

### Unit-productions removal
The `removeUnitProductions()` method starts by identifying and separating productions. This means iterating over the P Map and for each pair of non-terminal with the corresponding List of productions, iterate over those productions and check their length and belongness to Vn, such that a unit production consists only of a single non-terminal. In this way, we separate the productions, and those that are ok we add directly to the final Map. 
Now we are left with modifying the unit-productions, and to do so we iterate over the previously found unit-productions and for each such production, we search in the P Map and retrieve the leading productions from the correponding single-non terminal and replace it with its productions. If one of the found productions is still an unit production, add it to `toVisit` List if it is not yet there and process until there are no more elements left in that List. 

### Inaccessible-symbols removal
The `removeInaccessibleSymbols` method starts by initializing a `Set<String> accessibleSymbols` and adding `S` as the first element, as it is automatically accessible. After that, we have to find the over accessible symbols, and we do so just like in real life. 
Iterate over the entries in P Map and retrieve the current key (non-terminal) being process, to check the belongness to `accessibleSymbols`. Once a belonging non-terminal was found, we iterate over its productions and add the newly found resulting non-terminals. Also, mark `boolean changed` flag as true, to let the program know that there were added new states in the Set and it is needed to be process once again.
Now that we have identified the inaccessible symbols, we have to remove all the productions containing them. To do so, we iterate over the original productions Map and verify each key (non-terminal) if it is contained in `accessibleSymbols` Set as well, and if yes add it with the corresponding value to the Map that holds the new productions and  only the key to new Vn as well.

### Non-productive states removal
The `removeNonProductiveSymbols` method operates similar to the previously described one, but does not initially add S to the Set. It also uses a `boolean changed` flag to keep track of any new States added in `productiveSymbols` Set. 
We're set to identify all productive symbols, do so by iterating over the P productions, but process only those non-terminal that are not yet added to `productiveSymbols`, and for each non-terminal's production, check each symbol belongness to either `Vt` or `productiveSymbols`. If none of the conditions are met the `productionIsProductive` flag is marked as false and production isn't added to `productiveSymbols`.
After identifying all productive symbols, we have to exclude the non-productive ones. Iterate once again over the P Map and process only the productions of the non-terminals that are marked as productive. Same as previously done, check each production's symbol belongness to either `Vt` or `productiveSymbols`. If at least one of the conditions is met, add the corresponding production to the `newProductions` with its corresponding non-terminal.

### Conversion to CNF
The `toCNF` method



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
