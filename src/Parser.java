import ast.ChoiceFactor;
import ast.DeriveFactor;
import ast.ExpFactor;
import ast.Expr;
import ast.ExprFactor;
import ast.Factor;
import ast.FunctFactor;
import ast.NumFactor;
import ast.Term;
import ast.PowerFactor;
import ast.RecCallFactor;

import java.math.BigInteger;

public class Parser {
    private final Lexer lexer;

    public Parser(String input) {
        this.lexer = new Lexer(input);
    }
    
    public Expr parseExpr() {
        Expr expr = new Expr();
        while (lexer.peek() != null && !")".equals(lexer.peek())) {
            expr.addTerm(parseTerm());
        }
        return expr;
    }

    public Term parseTerm() {
        Term term = new Term();
        do {
            if ("*".equals(lexer.peek())) {
                lexer.next();
            }
            if ("+".equals(lexer.peek())) {
                lexer.next();
            } else if ("-".equals(lexer.peek())) {
                term.addFactor(new NumFactor(BigInteger.valueOf(-1)));
                lexer.next();
            }
            term.addFactor(parseFactor());
        } while ("*".equals(lexer.peek()));

        return term;
    }

    public Factor parseFactor() {
        if (lexer.peek() == null) {
            throw new RuntimeException("Unexpected end of input while parsing Factor");
        } else if ("(".equals(lexer.peek())) {
            return parseExprFactor();
        } else if ("x".equals(lexer.peek()) || "y".equals(lexer.peek())) {
            return parsePowerFactor();
        } else if (Character.isDigit(lexer.peek().charAt(0)) 
                || "+".equals(lexer.peek()) || "-".equals(lexer.peek())) {
            return parseNumFactor();
        } else if ("exp".equals(lexer.peek())) {
            return parseExpFactor();
        } else if ("f".equals(lexer.peek())) {
            return parseFunctLikeFactor();
        } else if ("[".equals(lexer.peek())) {
            return parseChoiceFactor();
        } else if ("dx".equals(lexer.peek()) || "dy".equals(lexer.peek()) 
                    || "grad".equals(lexer.peek())) {
            return parseDeriveFactor();
        } else {
            throw new RuntimeException("Unexpected Factor Token: " + lexer.peek());
        }
    }

    public NumFactor parseNumFactor() {
        int sign = 1;
        if ("+".equals(lexer.peek())) {
            lexer.next();
        } else if ("-".equals(lexer.peek())) {
            sign = -1;
            lexer.next();
        }
        BigInteger value = new BigInteger(lexer.peek());
        if (sign < 0) {
            value = value.negate();
        }
        NumFactor numFactor = new NumFactor(value);
        lexer.next();
        return numFactor;
    }

    public PowerFactor parsePowerFactor() {
        String name = lexer.peek();
        lexer.next(); // 跳过变量名
        int exponent = parseExponent();
        PowerFactor varFactor = new PowerFactor(name, exponent);
        return varFactor;
    }

    public ExprFactor parseExprFactor() {
        lexer.next(); // 跳过左括号
        Expr expr = parseExpr();
        lexer.next(); // 跳过右括号
        int exponent = parseExponent();
        ExprFactor exprFactor = new ExprFactor(expr, exponent);
        return exprFactor;
    }

    public ExpFactor parseExpFactor() {
        lexer.next(); // 跳过 exp
        lexer.next(); // 跳过左括号
        Factor exp = parseFactor();
        lexer.next(); // 跳过右括号
        int exponent = parseExponent();
        ExpFactor expFactor = new ExpFactor(exp, exponent);
        return expFactor;
    }

    public Factor parseFunctLikeFactor() {
        lexer.next(); // 跳过 f
        boolean isRecCall = false;
        int offset = 0;
        int fixIndex = -1;
        boolean isNormal = true;

        if ("{".equals(lexer.peek())) {
            isNormal = false;
            lexer.next(); // 跳过左花括号
            String inner = lexer.peek();
            if ("n".equals(inner)) {
                isRecCall = true;
                lexer.next(); // 跳过 n
                lexer.next(); // 跳过 -
                offset = Integer.parseInt(lexer.peek());
                lexer.next(); // 跳过数字 1 2
            } else {
                fixIndex = Integer.parseInt(inner);
                lexer.next(); // 跳过数字
            }
            lexer.next(); // 跳过右花括号
        }
        lexer.next(); // 跳过左括号
        Factor arg = parseFactor();
        lexer.next(); // 跳过右括号
        if (isNormal) {
            return new FunctFactor(arg);
        } else {
            if (isRecCall) {
                return new RecCallFactor(offset, arg);
            } else {
                return new FunctFactor(fixIndex, arg);
            }
        }
    }

    public ChoiceFactor parseChoiceFactor() {
        lexer.next(); // 跳过左中括号
        lexer.next(); // 跳过左括号
        Factor left = parseFactor();
        lexer.next(); // 跳过 ==
        Factor right = parseFactor();
        lexer.next(); // 跳过右括号
        lexer.next(); // 跳过 ?
        Factor trueFactor = parseFactor();
        lexer.next(); // 跳过 :
        Factor falseFactor = parseFactor();
        lexer.next(); // 跳过右中括号
        ChoiceFactor choiceFactor = new ChoiceFactor(left, right, trueFactor, falseFactor);
        return choiceFactor;
    }

    public DeriveFactor parseDeriveFactor() {
        String type = lexer.peek();
        lexer.next(); // 跳过 dx dy grad
        lexer.next(); // 跳过左括号
        Expr expr = parseExpr();
        DeriveFactor deriveFactor = new DeriveFactor(type, expr);
        lexer.next(); // 跳过右括号
        return deriveFactor;
    }

    public int parseExponent() {
        int exponent = 1;
        if ("^".equals(lexer.peek())) {
            lexer.next();
            if ("+".equals(lexer.peek())) {
                lexer.next();
            }
            exponent = Integer.parseInt(lexer.peek());
            lexer.next();
        }
        return exponent;
    }
}
