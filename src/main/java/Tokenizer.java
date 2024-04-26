import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    private static final String NUMBER_REGEX = "-?\\d+";
    private static final String IMAGE_IDENTIFIER_REGEX = "--img";
    private static final String EQUALS_REGEX = "=";
    private static final String FILE_PATH_REGEX ="\"[^\"]+\\.(png|jpg|bmp|gif|jpeg|tiff|webp)\""; // Specific for file paths with extension

    private static final String FOLDER_PATH_REGEX = "\"(?!.*\\.(png|jpg|bmp|gif|tiff|webp|jpeg)$)[^\"]+\"";
    private static final String IMAGE_TYPE_REGEX = "(png|jpg|bmp|gif|jpeg|tiff|webp)";
    private static final String PARAMETER_REGEX = "--(x|y|w|h|deg|lvl|img|format)\\b";
    private static final String COMMAND_REGEX = "\\b(crop|convert|rotate|flipX|flipY|bw|resize|contrast|brightness|negative|colorize|blur|sharpen|compress|ft|threshold|reduceNoise|remBg)";
    private static final String START_COMMAND_REGEX = "imp";
    private static final String PIPE_LINE_REGEX = "->";


    private static final String TOKEN_REGEX = String.join("|",
            COMMAND_REGEX,PARAMETER_REGEX, NUMBER_REGEX, IMAGE_TYPE_REGEX, PARAMETER_REGEX, FILE_PATH_REGEX, FOLDER_PATH_REGEX, EQUALS_REGEX, IMAGE_IDENTIFIER_REGEX, START_COMMAND_REGEX, PIPE_LINE_REGEX
    );

    private static final Pattern TOKEN_PATTERN = Pattern.compile(TOKEN_REGEX);

    public static List<Token> tokenize(String input) {
        List<Token> tokens = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERN.matcher(input);
        int start = 0; // Start index of the current token in the input string
        while (matcher.find()) {
            if (matcher.start() > start) {
                // There's unmatched input before the current match
                String unmatchedInput = input.substring(start, matcher.start()).trim();
                if(!unmatchedInput.trim().isEmpty()){
                    reportError(unmatchedInput);
                }
            }

            String value = matcher.group();
            TokenType type = determineTokenType(value);
            //special case for absorbing quotes within the value
            //and manually adding the quotes as tokens
            if ( type == TokenType.FILE_PATH || type == TokenType.FOLDER_PATH) {
                tokens.add(new Token(TokenType.QUOTE, "\""));
                tokens.add(new Token(type, value.substring(1, value.length() - 1)));
                tokens.add(new Token(TokenType.QUOTE, "\""));
            }
            else {
                tokens.add(new Token(type, value));
            }
            start = matcher.end();
        }

        if (start < input.length()) {
            // Handle any trailing unmatched input
            String unmatchedInput = input.substring(start).trim();
            if (!unmatchedInput.trim().isEmpty()) {
                reportError(unmatchedInput); // Handle or report the unmatched input
            }
        }

        return tokens;
    }

    private static void reportError(String value) {
        System.err.println("Unrecognized input: '" + value + "'");
    }


    private static TokenType  determineTokenType(String value) {
        if (value.matches(COMMAND_REGEX)) {
            return TokenType.COMMAND;
        } else if (value.matches(NUMBER_REGEX)) {
            return TokenType.NUMBER;
        } else if (value.matches(IMAGE_TYPE_REGEX)) {
            return TokenType.IMAGE_TYPE;
        } else if (value.matches(IMAGE_IDENTIFIER_REGEX)) {
            return TokenType.IMAGE_IDENTIFIER;
        } else if (value.matches(FILE_PATH_REGEX)) {
            return TokenType.FILE_PATH;
        } else if (value.matches(PARAMETER_REGEX)) {
            return TokenType.PARAMETER;
        }
        else if (value.matches(FOLDER_PATH_REGEX)) {
            return TokenType.FOLDER_PATH;
        } else if (value.matches(EQUALS_REGEX)) {
            return TokenType.EQUALS;
        } else if (value.matches(START_COMMAND_REGEX)) {
            return TokenType.START_COMMAND;
        } else if (value.matches(PIPE_LINE_REGEX)) {
            return TokenType.PIPE_LINE;
        }  else {
            return TokenType.UNKNOWN;
        }
    }
}
