package ast;

import java.math.BigInteger;

import poly.Poly;
import poly.Mono;

public class PowerFactor implements Factor {
    private String name; // 当前只会是 "x" "y"
    private int power = 1; // 指数（非负整数，最高到 8）

    public PowerFactor(String name, int power) {
        this.name = name;
        this.power = power;
    }

    @Override
    public Poly toPoly() {
        Poly poly = new Poly();
        if (name.equals("x")) {
            Mono mono = new Mono(BigInteger.valueOf(power), BigInteger.ZERO, new Poly());
            poly.addTerm(mono, BigInteger.ONE);
        } else if (name.equals("y")) {
            Mono mono = new Mono(BigInteger.ZERO, BigInteger.valueOf(power), new Poly());
            poly.addTerm(mono, BigInteger.ONE);
        } else {
            throw new RuntimeException("An unexpected varname: " + name);
        }
        return poly;
    }
}