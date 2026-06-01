import java.util.Scanner;

import ast.Expr;
import ast.Function;
import poly.Poly;

public class MainClass {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();
        if (n == 1) {
            String define = scanner.nextLine();
            Parser defParser = new Parser(define.split("=")[1]);
            Expr defRoot = defParser.parseExpr();
            Function.setNormalPoly(defRoot.toPoly());
        }
        int m = scanner.nextInt();
        scanner.nextLine();
        if (m == 1) {
            String def0 = "";
            String def1 = "";
            String defn = "";
            for (int i = 0; i < 3; i++) {
                String line = scanner.nextLine().replaceAll("\\s+", "");
                if (line.startsWith("f{0}")) {
                    def0 = line;
                } else if (line.startsWith("f{1}")) {
                    def1 = line;
                } else if (line.startsWith("f{n}")) {
                    defn = line;
                }
            }
            Parser defParser0 = new Parser(def0.split("=")[1]);
            Parser defParser1 = new Parser(def1.split("=")[1]);
            Parser defParsern = new Parser(defn.split("=")[1]);
            Expr defRoot0 = defParser0.parseExpr();
            Expr defRoot1 = defParser1.parseExpr();
            Expr defRootn = defParsern.parseExpr();
            Function.setRecPoly(defRoot0, defRoot1, defRootn);
        }
        String input = scanner.nextLine();
        Parser parser = new Parser(input);
        Expr root = parser.parseExpr();
        Poly poly = root.toPoly();
        System.out.println(poly.exprToString(false));
        scanner.close();
    }
}
