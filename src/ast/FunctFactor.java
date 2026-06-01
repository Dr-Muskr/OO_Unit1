package ast;

import poly.Poly;

public class FunctFactor implements Factor {
    private boolean isNormal;
    private int recIndex;
    private Factor arg;
    
    public FunctFactor(Factor arg) {
        this.isNormal = true;
        this.arg = arg;
    }

    public FunctFactor(int recIndex, Factor arg) {
        this.isNormal = false;
        this.recIndex = recIndex;
        this.arg = arg;
    }

    @Override
    public Poly toPoly() {
        Poly argPoly = this.arg.toPoly();
        Poly basePoly;
        if (isNormal) {
            basePoly = Function.getNormalPoly();
        } else {
            basePoly = Function.getRecPoly(recIndex);
        }
        return basePoly.substitute(argPoly);
    }
}
