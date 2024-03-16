# Laboratory Work 3

### Course: Formal Languages & Finite Automata
### Author: Felicia Novac

----

## Theory
Lexical Analysis which is also known as tokenization is the process of breaking down a complex string of characters from source code into manageable pieces called tokens (from here comes the term tokenization).
These so called tokens are small units that carry meaning and are essential for the programming language's understanding and further processing of the input. The tokens are categorized in types `regex`, each representing a different element of the syntax of the programming language. Such examples of tokens might include:
* *keywords* - crop, resize
* *identifiers* - foo, var
* *numbers* - 7, 23
* ...

## Objectives:
* Understand what lexical analysis is.
* Get familiar with the inner workings of a lexer/scanner/tokenizer.
* Implement a sample lexer and show how it works.


## Implementation description
I've decided to create a sample Tokenizer for the ELSD project, which is a DSL for image processing. Despite the fact that it was implemented within the project using ANTLR, I tried to do it on my own in the way I see it, with the use of example from [1]. 

The code within the lab is an implementation of a tokenizer class in Java. This class has a method called tokenize(String input). The method uses regular expressions to identify tokens in the input string, and returns a list of Token objects.

The Tokenizer class contains a constant String `TOKEN_REGEX`, which represents all possible token names that can be identified by the tokenizer, splitter with the use of OR operator. It also contains several constant regular expressions, one for each type of token that can be identified. These regular expressions are used to match tokens in the input string.

The tokenize method creates a new ArrayList of Token objects to hold the tokens found in the input string. It then creates a Matcher object using the Pattern constant created from the regular expressions. The method then iterates over the input string, using the Matcher object to find matches for the regular expressions. For each match found, the method identifies Token Type by using a helper method `determineTokenType(String value)`, and creates a new Token object with its corresponding type and value, and adds it to the list of tokens.

Finally, the tokenize method returns the list of Token objects.



## References
- **A Sample of a Lexer Implementation** - Accessed March 13, 2024. [https://www.tutorialspoint.com/automata_theory/chomsky_classification_of_grammars.htm]([https://llvm.org/docs/tutorial/MyFirstLanguageFrontend/LangImpl01.html](https://llvm.org/docs/tutorial/MyFirstLanguageFrontend/LangImpl01.html).

