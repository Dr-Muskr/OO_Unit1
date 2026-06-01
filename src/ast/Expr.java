package ast;

import java.util.ArrayList;
import java.util.List;

import poly.Poly;

public class Expr implements Node {
    private List<Term> terms = new ArrayList<>();

    public void addTerm(Term term) {
        this.terms.add(term);
    }

    @Override
    public Poly toPoly() {
        Poly zero = new Poly();
        Poly poly = terms.stream()
                .map(term -> term.toPoly())
                .reduce(zero, Poly::add);
        return poly;
    }
}
