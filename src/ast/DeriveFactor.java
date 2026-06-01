package ast;

import poly.Poly;

public class DeriveFactor implements Factor {
    private String type;
    private Expr expr;

    public DeriveFactor(String type, Expr expr) {
        this.type = type;
        this.expr = expr;
    }

    @Override
    public Poly toPoly() {
        Poly poly = expr.toPoly();
        if (type.equals("dx")) {
            return poly.derive("x");
        } else if (type.equals("dy")) {
            return poly.derive("y");
        } else if (type.equals("grad")) {
            return poly.derive("x").add(poly.derive("y"));
        } else {
            throw new RuntimeException("Unknown derive type: " + type);
        }
    }
}
