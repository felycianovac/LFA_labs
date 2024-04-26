import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandStructure {
    private static final Map<String, List<TokenType>> commandParamMap = new HashMap<>();

    static {
       commandParamMap.put("crop", List.of(TokenType.PARAMETER, TokenType.EQUALS, TokenType.NUMBER // --x
               , TokenType.PARAMETER, TokenType.EQUALS, TokenType.NUMBER, // --y
               TokenType.PARAMETER, TokenType.EQUALS, TokenType.NUMBER, // --w
               TokenType.PARAMETER, TokenType.EQUALS, TokenType.NUMBER)); // --h

        commandParamMap.put("convert", List.of(TokenType.PARAMETER, TokenType.EQUALS, TokenType.IMAGE_TYPE));
        commandParamMap.put("rotate", List.of(TokenType.PARAMETER, TokenType.EQUALS, TokenType.NUMBER));
        commandParamMap.put("resize", List.of(TokenType.PARAMETER, TokenType.EQUALS, TokenType.NUMBER // --w
                , TokenType.PARAMETER, TokenType.EQUALS, TokenType.NUMBER)); // --h
        commandParamMap.put("threshold", List.of(TokenType.PARAMETER, TokenType.EQUALS, TokenType.NUMBER));
        commandParamMap.put("contrast", List.of(TokenType.PARAMETER, TokenType.EQUALS, TokenType.NUMBER));
        commandParamMap.put("brightness", List.of(TokenType.PARAMETER, TokenType.EQUALS, TokenType.NUMBER));
        commandParamMap.put("blur", List.of(TokenType.PARAMETER, TokenType.EQUALS, TokenType.NUMBER));
        commandParamMap.put("sharpen", List.of(TokenType.PARAMETER, TokenType.EQUALS, TokenType.NUMBER));
    }

    public static List<TokenType> getCommandParams(String command) {
        return commandParamMap.get(command);
    }
}
