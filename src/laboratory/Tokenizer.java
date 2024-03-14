package laboratory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    private static final String NUMBER_REGEX = "-?\\d+";
    private static final String IMAGE_IDENTIFIER_REGEX = "--img";
    private static final String ID_REGEX = "[a-zA-Z][a-zA-Z0-9]*"; //for other identifiers such as variables
    private static final String QUOTED_REGEX="\"";
    private static final String EQUALS_REGEX = "=";
    private static final String FILE_PATH_REGEX ="\"[^\"]+\\.(png|jpg|bmp|gif)\""; // Specific for file paths with extension
    private static final String FOLDER_PATH_REGEX = "\"(?!.*\\.(png|jpg|bmp|gif)$)[^\"]+\"";
    private static final String IMAGE_TYPE_REGEX = "\\b(png|jpg|bmp|gif)\\b";
    private static final String PARAMETER_REGEX = "--(x|y|w|h|degrees|lvl|img|format)\\b";
    private static final String COMMAND_REGEX = "\\b(crop|convert|rotate|flipX|flipY|bw|resize|contrast|brightness|negative|colorize|blur|sharpen|compress|ft|threshold)";

    private static final String TOKEN_REGEX = String.join("|",
            COMMAND_REGEX,PARAMETER_REGEX, NUMBER_REGEX, ID_REGEX, IMAGE_TYPE_REGEX, PARAMETER_REGEX, FILE_PATH_REGEX, FOLDER_PATH_REGEX, QUOTED_REGEX, EQUALS_REGEX, IMAGE_IDENTIFIER_REGEX
    );

    private static final Pattern TOKEN_PATTERN = Pattern.compile(TOKEN_REGEX);

    public static List<Token> tokenize(String input) {
            List<Token> tokens = new ArrayList<>();
            Matcher matcher = TOKEN_PATTERN.matcher(input);
            while (matcher.find()) {
                String value = matcher.group();
                String type = determineTokenType(value);
                tokens.add(new Token(type, value));
            }
            return tokens;
    }


    private static String determineTokenType(String value) {
        if (value.matches(COMMAND_REGEX)) {
            return "COMMAND";
        } else if (value.matches(NUMBER_REGEX)) {
            return "NUMBER";
        } else if (value.matches(ID_REGEX)) {
            return "IDENTIFIER";
        } else if (value.matches(IMAGE_TYPE_REGEX)) {
            return "IMAGE_TYPE";
        } else if (value.matches(IMAGE_IDENTIFIER_REGEX)) {
            return "IMAGE_IDENTIFIER";
        }
        else if (value.matches(FILE_PATH_REGEX)) {
            return "FILE_PATH";
        } else if(value.matches(PARAMETER_REGEX)){
            return "PARAMETER";
        } else if (value.matches(FOLDER_PATH_REGEX)) {
            return "FOLDER_PATH";
        } else if (value.matches(QUOTED_REGEX)) {
            return "QUOTED";
        } else if (value.matches(EQUALS_REGEX)) {
            return "EQUALS";
        } else {
            return "UNKNOWN";
        }
    }
}


