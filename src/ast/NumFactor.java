package ast;

import java.math.BigInteger;

import poly.Poly;
import poly.Mono;

public class NumFactor implements Factor {
    private BigInteger value;

    public NumFactor(BigInteger value) {
        this.value = value;
    }

    @Override
    public Poly toPoly() {
        Poly poly = new Poly();
        poly.addTerm(Mono.CONST, value);
        return poly;
    }
}
