
import java.util.*;

public class ASTBuilder {


    public ASTNode buildParseTree(List<Token> tokens)  throws IllegalArgumentException{
        if(tokens.isEmpty()){
            throw new IllegalArgumentException("No tokens to build parse tree");
        }
        if(tokens.get(0).getType() != TokenType.START_COMMAND){
            throw new IllegalArgumentException("First token must be a start command");
        }

        if(tokens.get(1).getType() != TokenType.IMAGE_IDENTIFIER){
            throw new IllegalArgumentException("Second token must be an image identifier");
        }
        if(tokens.get(2).getType() != TokenType.EQUALS){
            throw new IllegalArgumentException("Third token must be an equals sign");
        }
        if(tokens.get(3).getType() != TokenType.QUOTE){
            throw new IllegalArgumentException("Invalid image path format");
        }
        if(tokens.get(4).getType() != TokenType.FILE_PATH && tokens.get(4).getType() != TokenType.FOLDER_PATH) {
            throw new IllegalArgumentException("Fourth token must be either a file or folder path");
        }
        if(tokens.get(5).getType() != TokenType.QUOTE){
            throw new IllegalArgumentException("Invalid image path format");
        }


        ASTNode root = new ASTNode(tokens.get(0));  // Root node for the parse tree
        Stack<ASTNode> stack = new Stack<>();
        stack.push(root);

        ASTNode commandNode = null;


        for(int i = 1; i< tokens.size(); i++){
            Token token = tokens.get(i);
            TokenType tokenType = token.getType();

            if(tokenType == TokenType.START_COMMAND){
                throw new IllegalArgumentException("Nested start command");
            }

            switch (tokenType){
                case IMAGE_IDENTIFIER:
                    //do not add repeated image identifiers
                    if(stack.peek().getToken().getType() == TokenType.IMAGE_IDENTIFIER){
                        throw new IllegalArgumentException("Repeated image identifier");
                    }
                    ASTNode imageIdentifierNode = new ASTNode(token);
                    stack.peek().addChild(imageIdentifierNode);
                    break;
                case PIPE_LINE:
                    if(commandNode == null){
                        throw new IllegalArgumentException("Pipe line without a command");
                    }
                    stack.pop();
//                    stack.peek().addChild(new ASTNode(token)); //??

                    break;
                case COMMAND:
                    commandNode = new ASTNode(token);
                    stack.peek().addChild(commandNode);
                    stack.push(commandNode);
                    break;

                case PARAMETER:
                    if(commandNode == null){
                        throw new IllegalArgumentException("Parameter without a command");
                    }
                    commandNode.addChild(new ASTNode(token));
                    break;
                case EQUALS:

                    ASTNode equalsNode = new ASTNode(token);
                    stack.peek().addChild(equalsNode);
                    break;
                case NUMBER:
                    ASTNode numberNode = new ASTNode(token);
                    stack.peek().addChild(numberNode);
                    break;

                case FILE_PATH:
                    ASTNode filePathNode = new ASTNode(token);
                    stack.peek().addChild(filePathNode);
                    stack.push(filePathNode);
                    break;
                case FOLDER_PATH:
                    ASTNode folderPathNode = new ASTNode(token);
                    stack.peek().addChild(folderPathNode);
                    stack.push(folderPathNode);

                    break;
                case IMAGE_TYPE:
                    ASTNode imageTypeNode = new ASTNode(token);
                    stack.peek().addChild(imageTypeNode);
                    break;
                case QUOTE:
                    break;


                default:
                    throw new IllegalArgumentException("Invalid token type: " + tokenType);
            }
        }
        return root;
    }

    // Recursive function to print the parse tree in a hierarchical format
    public void printParseTree(ASTNode node, int level) {
        printNode(node, level);

        for (ASTNode child : node.getChildren()) {
            printParseTree(child, level + 1);
        }
    }

    private void printNode(ASTNode node, int level) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < level; i++) {
            indent.append("\t");
        }

        System.out.println(indent.toString() + (node.getToken() != null ? node.getToken().getValue() : "Node"));
    }

}







