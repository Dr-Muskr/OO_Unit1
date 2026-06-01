package ast;

import poly.Poly;

public class ChoiceFactor implements Factor {
    private Factor left;
    private Factor right;
    private Factor trueBr;
    private Factor falseBr;

    public ChoiceFactor(Factor left, Factor right, Factor trueBr, Factor falseBr) {
        this.left = left;
        this.right = right;
        this.trueBr = trueBr;
        this.falseBr = falseBr;
    }

    @Override
    public Poly toPoly() {
        Poly polyLeft = left.toPoly();
        Poly polyRight = right.toPoly();
        boolean isMatch = polyLeft.equals(polyRight);
        if (isMatch) {
            return trueBr.toPoly();
        } else {
            return falseBr.toPoly();
        }
    }
}
