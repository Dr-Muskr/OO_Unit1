package poly;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

public class Poly {
    private Map<Mono, BigInteger> terms = new HashMap<>();
    private int hash = 0;
    private boolean hashValid = false;

    public void addTerm(Mono mono, BigInteger coef) {
        hashValid = false;
        BigInteger newCoef = terms.getOrDefault(mono, BigInteger.ZERO).add(coef);
        if (newCoef.equals(BigInteger.ZERO)) {
            terms.remove(mono);
        } else {
            terms.put(mono, newCoef);
        }
    }

    public Poly add(Poly other) {
        Poly result = new Poly();
        for (Map.Entry<Mono, BigInteger> entry : this.terms.entrySet()) {
            result.addTerm(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<Mono, BigInteger> entry : other.getTerms().entrySet()) {
            result.addTerm(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public Poly mul(Poly other) {
        Poly result = new Poly();
        for (Map.Entry<Mono, BigInteger> entry1 : this.terms.entrySet()) {
            Mono power1 = entry1.getKey();
            BigInteger coef1 = entry1.getValue();
            for (Map.Entry<Mono, BigInteger> entry2 : other.getTerms().entrySet()) {
                Mono power2 = entry2.getKey();
                BigInteger coef2 = entry2.getValue();
                result.addTerm(power1.mul(power2), coef1.multiply(coef2));
            }
        }
        return result;
    }

    public Poly pow(int power) {
        Poly result = new Poly();
        Poly base = this;
        result.addTerm(Mono.CONST, BigInteger.ONE);
        int powerCount = power; 
        while (powerCount != 0) {
            if (powerCount % 2 == 1) {
                result = result.mul(base);
                powerCount = powerCount - 1;
            } else {
                base = base.mul(base);
                powerCount = powerCount / 2;
            }
        }
        return result;
    }

    public Map<Mono, BigInteger> getTerms() {
        return this.terms;
    }

    public Poly derive(String varName) {
        Poly result = new Poly();
        for (Map.Entry<Mono, BigInteger> entry : this.terms.entrySet()) {
            Mono mono = entry.getKey();
            BigInteger coef = entry.getValue();
            Poly derivePoly = mono.derive(varName, coef);
            result = result.add(derivePoly);
        }
        return result;
    }

    public Poly substitute(Poly arg) {
        Poly result = new Poly();
        for (Map.Entry<Mono, BigInteger> entry : this.terms.entrySet()) {
            Mono mono = entry.getKey();
            BigInteger coef = entry.getValue();

            Poly newExpPoly = new Poly();
            if (!mono.getExpPoly().getTerms().isEmpty()) {
                newExpPoly = mono.getExpPoly().substitute(arg); 
            }
            BigInteger k = mono.getPowerX();
            Mono leftMono = new Mono(BigInteger.ZERO, mono.getPowerY(), newExpPoly);
            Poly leftPoly = new Poly();
            leftPoly.addTerm(leftMono, coef);
            if (k.equals(BigInteger.ZERO)) {
                result = result.add(leftPoly);
            } else {
                Poly substituted = arg.pow(k.intValue());
                result = result.add(substituted.mul(leftPoly));
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        } else {
            return this.terms.equals(((Poly)obj).getTerms());
        }
    }

    @Override
    public int hashCode() {
        if (!hashValid) {
            hash = this.terms.hashCode();
            hashValid = true;
        }
        return hash;
    }

    public String exprToString(boolean isTest) {
        if (terms.isEmpty()) {
            return "0";
        } else {
            StringBuilder sb = new StringBuilder(); 
            Mono plusMono = null;
            boolean isFirst = true;

            for (Map.Entry<Mono, BigInteger> entry : this.terms.entrySet()) {
                if (entry.getValue().compareTo(BigInteger.ZERO) > 0) {
                    plusMono = entry.getKey();
                    BigInteger plusCoef = entry.getValue();
                    appendTerm(sb, plusMono, plusCoef, isFirst, isTest);
                    isFirst = false;
                    break;
                }
            }

            for (Map.Entry<Mono, BigInteger> entry : this.terms.entrySet()) {
                Mono mono = entry.getKey();
                BigInteger coef = entry.getValue();
                if (!mono.equals(plusMono)) {
                    appendTerm(sb, mono, coef, isFirst, isTest);
                    isFirst = false;
                }
            }
            return sb.toString();
        }
    }

    public void appendTerm(StringBuilder sb, Mono mono, 
                BigInteger coef, boolean isFirst, boolean isTest) {
        BigInteger powerX = mono.getPowerX();
        BigInteger powerY = mono.getPowerY();
        Poly expPoly = mono.getExpPoly();
        
        if (coef.compareTo(BigInteger.ZERO) < 0) {
            sb.append("-");
        } else if (!isFirst) {
            sb.append("+");
        }
        if (powerX.equals(BigInteger.ZERO) && powerY.equals(BigInteger.ZERO) 
                && expPoly.getTerms().isEmpty()) {
            sb.append(coef.abs());
        } else {
            if (coef.abs().compareTo(BigInteger.ONE) != 0) {
                sb.append(coef.abs());
                sb.append("*");
            }
            if (isTest) {
                sb.append("X");
            } else {
                if (!powerX.equals(BigInteger.ZERO)) {
                    sb.append("x");
                    if (!powerX.equals(BigInteger.ONE)) {
                        sb.append("^");
                        sb.append(powerX);
                    }
                    if (!expPoly.getTerms().isEmpty() || !powerY.equals(BigInteger.ZERO)) {
                        sb.append("*");
                    }
                }
                if (!powerY.equals(BigInteger.ZERO)) {
                    sb.append("y");
                    if (!powerY.equals(BigInteger.ONE)) {
                        sb.append("^");
                        sb.append(powerY);
                    }
                    if (!expPoly.getTerms().isEmpty()) {
                        sb.append("*");
                    }
                }
                if (!expPoly.getTerms().isEmpty()) {
                    sb.append(getExpStr(expPoly));
                }
            }
        }
    }

    private String getExpStr(Poly expPoly) {
        String bestStr = getSingleExpStr(expPoly);
        int n = expPoly.getTerms().size();
        if (n <= 2) {
            return bestStr;
        }

        String candA = bestStr;
        if (candA != null) {
            bestStr = candA;
        }

        String candB = bestStr;
        if (candB != null) {
            bestStr = candB;
        }

        return bestStr;
    }

    private String getExpStrExcludeOne(Poly expPoly, int currentMinLen) {
        int minLen = currentMinLen;
        int n = expPoly.getTerms().size();
        Mono[] monos = new Mono[n];
        BigInteger[] coeffs = new BigInteger[n];
        int index = 0;
        for (Map.Entry<Mono, BigInteger> entry : expPoly.getTerms().entrySet()) {
            monos[index] = entry.getKey();
            coeffs[index] = entry.getValue(); 
            index++;
        }

        BigInteger[] preGcd = new BigInteger[n];
        preGcd[0] = coeffs[0].abs(); 
        for (int i = 1; i < n; i++) {
            preGcd[i] = preGcd[i - 1].gcd(coeffs[i].abs());
        }

        BigInteger[] sufGcd = new BigInteger[n];
        sufGcd[n - 1] = coeffs[n - 1].abs();
        for (int i = n - 2; i >= 0; i--) {
            sufGcd[i] = sufGcd[i + 1].gcd(coeffs[i].abs());
        }

        BigInteger globalGcd = preGcd[n - 1];
        String bestStr = null;

        for (int i = 0; i < n; i++) {
            BigInteger excludeGcd;
            if (i == 0) {
                excludeGcd = sufGcd[1];
            } else if (i == n - 1) {
                excludeGcd = preGcd[n - 2];
            } else {
                excludeGcd = preGcd[i - 1].gcd(sufGcd[i + 1]);
            }
            if (excludeGcd.compareTo(globalGcd) > 0) {
                Poly remainingPoly = new Poly();
                remainingPoly.getTerms().putAll(expPoly.getTerms());
                remainingPoly.getTerms().remove(monos[i]);
                Poly isolatedPoly = new Poly();
                isolatedPoly.addTerm(monos[i], coeffs[i]);
                String cand = getSingleExpStr(remainingPoly) 
                            + "*" 
                            + getSingleExpStr(isolatedPoly);
                if (cand.length() < minLen) {
                    minLen = cand.length();
                    bestStr = cand;
                }
            }
        }
        return bestStr;
    }

    private String getExpStrBaseSep(Poly expPoly, int currentMinLen) {
        String bestStr = null;
        int minLen = currentMinLen;
        for (BigInteger candBase : expPoly.getTerms().values()) {
            BigInteger base = candBase.abs();
            if (base.compareTo(BigInteger.ONE) <= 0) {
                continue;
            }
            Poly quotientPoly = new Poly();
            Poly remainderPoly = new Poly();
            boolean hasRemainder = false;

            for (Map.Entry<Mono, BigInteger> entry : expPoly.getTerms().entrySet()) {
                Mono mono = entry.getKey();
                BigInteger coef = entry.getValue();

                BigInteger[] qr = coef.divideAndRemainder(base);
                BigInteger q = qr[0];
                BigInteger r = qr[1];
                BigInteger half = base.divide(BigInteger.valueOf(2));

                if (r.compareTo(half) > 0) {
                    q = q.add(BigInteger.ONE);
                    r = r.subtract(base);
                } else if (r.compareTo(half.negate()) < 0) {
                    q = q.subtract(BigInteger.ONE);
                    r = r.add(base);
                }

                if (!q.equals(BigInteger.ZERO)) {
                    quotientPoly.addTerm(mono, q);
                }
                if (!r.equals(BigInteger.ZERO)) {
                    remainderPoly.addTerm(mono, r);
                    hasRemainder = true;
                }
            }

            if (!quotientPoly.getTerms().isEmpty()) {
                String cand = getSingleExpStr(quotientPoly.multiply(base));
                
                if (hasRemainder) {
                    cand = cand + "*" + getSingleExpStr(remainderPoly);
                }

                if (cand.length() < minLen) {
                    minLen = cand.length();
                    bestStr = cand;
                }
            }
        }
        return bestStr;
    }

    private String getSingleExpStr(Poly expPoly) {
        BigInteger bestPower = BigInteger.ONE;
        Poly bestPoly = expPoly;
        int min = expPattern(expPoly, BigInteger.ONE, true).length();
        BigInteger gcd = expPoly.getGcd();
        if (gcd.compareTo(BigInteger.ONE) > 0) {
            ArrayList<BigInteger> powers = new ArrayList<>();
            powers.add(gcd);
            for (int i = 2; i <= 9; i++) {
                BigInteger k = BigInteger.valueOf(i);
                if (gcd.remainder(k).equals(BigInteger.ZERO)) {
                    BigInteger partical = gcd.divide(k); 
                    if (partical.compareTo(BigInteger.ONE) > 0) {
                        powers.add(partical);
                    }
                }
            }
            for (BigInteger power : powers) {
                Poly fixedPoly = expPoly.divide(power);
                int cur = expPattern(fixedPoly, power, true).length();
                if (cur < min) {
                    min = cur;
                    bestPoly = fixedPoly;
                    bestPower = power;
                }
            }
        }
        return expPattern(bestPoly, bestPower, false);
    }

    private String expPattern(Poly poly, BigInteger expPower, boolean isTest) {
        String base = poly.exprToString(isTest);
        boolean isExpr = true;
        if (poly.getTerms().size() == 1) {
            Map.Entry<Mono, BigInteger> single = poly.getTerms().entrySet().iterator().next();
            int trueCount = 0;
            boolean isCoefOne = single.getValue().equals(BigInteger.ONE);
            if (isCoefOne) {
                trueCount++;
            }
            boolean isPowerXZero = single.getKey().getPowerX().equals(BigInteger.ZERO);
            if (isPowerXZero) {
                trueCount++;
            }
            boolean isPowerYZero = single.getKey().getPowerY().equals(BigInteger.ZERO);
            if (isPowerYZero) {
                trueCount++;
            }
            boolean isExpEmpty = single.getKey().getExpPoly().getTerms().isEmpty();
            if (isExpEmpty) {
                trueCount++;
            }
            if (trueCount >= 3) {
                isExpr = false;
            }
        }
        if (isExpr) {
            base = "(" + base + ")";
        }
        String rst = "exp(" + base + ")";
        if (!expPower.equals(BigInteger.ONE)) {
            rst = rst + "^" + String.valueOf(expPower);
        }
        return rst;
    }

    private BigInteger getGcd() {
        BigInteger gcd = BigInteger.ZERO;
        for (BigInteger coef : this.terms.values()) {
            gcd = gcd.gcd(coef.abs()); 
            // 一旦 gcd 降为 1，就不必继续算了，已经是最小公因数
            if (gcd.equals(BigInteger.ONE)) {
                return BigInteger.ONE;
            }
        }
        return gcd;
    }

    private Poly divide(BigInteger k) {
        Poly result = new Poly();
        for (Map.Entry<Mono, BigInteger> entry : this.terms.entrySet()) {
            // 系数除以 k
            BigInteger newCoef = entry.getValue().divide(k);
            result.addTerm(entry.getKey(), newCoef);
        }
        return result;
    }

    private Poly multiply(BigInteger k) {
        Poly result = new Poly();
        for (Map.Entry<Mono, BigInteger> entry : this.terms.entrySet()) {
            BigInteger newCoef = entry.getValue().multiply(k);
            result.addTerm(entry.getKey(), newCoef);
        }
        return result;
    }
}
