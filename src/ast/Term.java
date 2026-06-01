package ast;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import poly.Mono;
import poly.Poly;

public class Term implements Node {
    private List<Factor> factors = new ArrayList<>();

    public void addFactor(Factor factor) {
        this.factors.add(factor);
    }

    @Override
    public Poly toPoly() {
        Poly one = new Poly();
        one.addTerm(Mono.CONST, BigInteger.ONE);
        Poly poly = factors.stream()
                .map(factor -> factor.toPoly())
                .reduce(one, Poly::mul);
        return poly;
    }
}
