package ast;

import poly.Poly;

public class ExprFactor implements Factor {
    private Expr expr;
    private int power; 

    public ExprFactor(Expr expr, int power) {
        this.expr = expr;
        this.power = power;
    }

    @Override
    public Poly toPoly() {
        Poly poly = expr.toPoly().pow(this.power);
        return poly;
    }
}