import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private String expression;
    private int pos = 0;
    private String currentToken;
    private static final Pattern NUM_PATTERN = Pattern.compile("\\d+");
    private static final Pattern SIGN_PATTERN = Pattern.compile("[+-]+");
    
    private final Matcher numMatcher;
    private final Matcher signMatcher;

    public Lexer(String input) {
        this.expression = input.replaceAll("\\s+", "");
        this.numMatcher = NUM_PATTERN.matcher(this.expression);
        this.signMatcher = SIGN_PATTERN.matcher(this.expression);
        next();
    }

    public String getNum() {
        numMatcher.region(pos, expression.length());
        if (numMatcher.lookingAt()) {
            String num = numMatcher.group();
            pos += num.length();
            return num;
        } else {
            throw new RuntimeException("Expected a number at position " + pos);
        }
    }

    public String getSign() {
        signMatcher.region(pos, expression.length());
        if (signMatcher.lookingAt()) {
            String signs = signMatcher.group();
            pos += signs.length();
            int minusCount = signs.length() - signs.replace("-", "").length();
            return (minusCount % 2 == 0) ? "+" : "-";
        } else {
            throw new RuntimeException("Expected a sign at position " + pos);
        }
    }

    public void next() {
        if (pos >= expression.length()) {
            currentToken = null;
            return;
        }
        char c = expression.charAt(pos);
        if (Character.isDigit(c)) {
            currentToken = getNum();
        } else if (c == '+' || c == '-') {
            currentToken = getSign();
        } else if (expression.startsWith("==", pos)) {
            currentToken = "==";
            pos += 2;
        } else if (expression.startsWith("exp", pos)) {
            currentToken = "exp";
            pos += 3;
        } else if (expression.startsWith("dx", pos)) {
            currentToken = "dx";
            pos += 2;
        } else if (expression.startsWith("dy", pos)) {
            currentToken = "dy";
            pos += 2;
        } else if (expression.startsWith("grad", pos)) {
            currentToken = "grad";
            pos += 4;
        } else {
            currentToken = String.valueOf(c);
            pos++;
        }
    }

    // 常用工具方法：这对于 Parser 非常方便
    public String peek() {
        return currentToken;
    }
}
