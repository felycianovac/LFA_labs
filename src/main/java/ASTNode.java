import java.util.*;

class ASTNode {
    private Token token;
    private List<ASTNode> children;
    private ASTNode parent;

    public ASTNode() {
        this.children = new ArrayList<>();
    }

    public ASTNode(Token token) {
        this.token = token;
        this.children = new ArrayList<>();
    }

    public void addChild(ASTNode child) {
        child.setParent(this);
        children.add(child);
    }

    public Token getToken() {
        return token;
    }

    public List<ASTNode> getChildren() {
        return children;
    }

    public ASTNode getParent() {
        return parent;
    }

    public void setParent(ASTNode parent) {
        this.parent = parent;
    }
}
