package poly;

import java.math.BigInteger;
import java.util.Objects;

public class Mono {
    private BigInteger powerX;
    private BigInteger powerY;
    private Poly expPoly;
    private int hash = 0;
    private boolean hashValid = false;

    public static final Mono CONST = new Mono(BigInteger.ZERO, BigInteger.ZERO, new Poly());

    public Mono(BigInteger powerX, BigInteger powerY, Poly expPoly) {
        this.powerX = powerX;
        this.powerY = powerY;
        this.expPoly = expPoly;
    }

    public Mono(BigInteger powerX, BigInteger powerY, Poly expPoly, BigInteger expPower) {
        this.powerX = powerX;
        this.powerY = powerY;
        Poly expPowerPoly = new Poly();
        expPowerPoly.addTerm(CONST, expPower);
        this.expPoly = expPoly.mul(expPowerPoly);
    }

    public BigInteger getPowerX() {
        return powerX;
    }

    public BigInteger getPowerY() {
        return powerY;
    }

    public Poly getExpPoly() {
        return expPoly;
    }

    public Poly derive(String varName, BigInteger coef) {
        Poly result = new Poly();
        if ("x".equals(varName)) {
            if (!this.powerX.equals(BigInteger.ZERO)) {
                Mono m1 = new Mono(this.powerX.subtract(BigInteger.ONE), this.powerY, this.expPoly);
                result.addTerm(m1, coef.multiply(this.powerX));
            }
        } else if ("y".equals(varName)) {
            if (!this.powerY.equals(BigInteger.ZERO)) {
                Mono m1 = new Mono(this.powerX, this.powerY.subtract(BigInteger.ONE), this.expPoly);
                result.addTerm(m1, coef.multiply(this.powerY));
            }
        }

        if (!this.expPoly.getTerms().isEmpty()) {
            Poly polyDerive = this.expPoly.derive(varName); 
            Mono m2 = new Mono(this.powerX, this.powerY, this.expPoly);
            Poly expDerive = new Poly();
            expDerive.addTerm(m2, coef);
            result = result.add(expDerive.mul(polyDerive)); 
        }

        return result;
    }

    public Mono mul(Mono other) {
        BigInteger newPowerX = this.powerX.add(other.powerX);
        BigInteger newPowerY = this.powerY.add(other.powerY);
        Poly newExpPoly = this.expPoly.add(other.expPoly);
        return new Mono(newPowerX, newPowerY, newExpPoly);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        } else {
            Mono other = (Mono) obj;
            return this.powerX.equals(other.powerX) 
                    && this.powerY.equals(other.powerY)
                    && this.expPoly.equals(other.expPoly);
        }
    }

    @Override
    public int hashCode() {
        if (!hashValid) {
            hash = Objects.hash(this.powerX, this.powerY, this.expPoly);
            hashValid = true;
        }
        return hash;
    }
}
