package ast;

import poly.Poly;

public class RecCallFactor implements Factor {
    private int offset;
    private Factor arg;

    public RecCallFactor(int offset, Factor arg) {
        this.offset = offset;
        this.arg = arg;
    }

    @Override
    public Poly toPoly() {
        int targetN = Function.getCurrentN() - this.offset;
        Poly argPoly = arg.toPoly();
        Poly basePoly = Function.getRecPoly(targetN);
        return basePoly.substitute(argPoly);
    }
}
