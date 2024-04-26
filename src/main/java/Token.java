public class Token {
    private String value;
    private TokenType tokenType;

    public Token(TokenType tokenType, String value) {
        this.tokenType = tokenType;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type='" + tokenType + '\'' +
                ", value='" + value + '\'' +
                '}';
    }


    public TokenType getType() {
        return tokenType;
    }

    public String getValue() {
        return value;
    }

}
