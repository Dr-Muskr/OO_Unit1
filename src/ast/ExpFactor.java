package ast;

import java.math.BigInteger;

import poly.Mono;
import poly.Poly;

public class ExpFactor implements Factor {
    private Factor exp;
    private int power;
    
    public ExpFactor(Factor exp, int power) {
        this.exp = exp;
        this.power = power;
    }

    @Override
    public Poly toPoly() {
        Poly poly = new Poly();
        Mono mono = new Mono(BigInteger.ZERO, BigInteger.ZERO, 
                exp.toPoly(), BigInteger.valueOf(power));
        poly.addTerm(mono, BigInteger.ONE);
        return poly;
    }
}
