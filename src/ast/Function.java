package ast;

import poly.Poly;

public class Function {
    private static Poly normalPoly = null;
    private static Poly[] recPolys = new Poly[6];
    private static Expr refFn = null;
    private static int currentN = 0;
    
    public static int getCurrentN() {
        return currentN;
    }
    
    public static void setNormalPoly(Poly poly) {
        normalPoly = poly;
    }

    public static Poly getNormalPoly() {
        return normalPoly;
    }

    public static void setRecPoly(Expr f0, Expr f1, Expr fn) {
        recPolys[0] = f0.toPoly();
        recPolys[1] = f1.toPoly();
        refFn = fn;
        for (int n = 2; n <= 5; n++) {
            recPolys[n] = null;
        }
    }

    public static Poly getRecPoly(int n) {
        if (recPolys[n] == null) {
            int oldN = currentN;
            currentN = n;
            recPolys[n] = refFn.toPoly();
            currentN = oldN;
        }
        return recPolys[n];
    }
}
