package model;

import org.apache.commons.math3.analysis.integration.IterativeLegendreGaussIntegrator;
import org.apache.commons.math3.analysis.integration.UnivariateIntegrator;
import org.apache.commons.math3.linear.*;
import java.util.*;

public class Solver {
    private final UnivariateIntegrator integral;
    private final HashMap<Character,Integer> domain = new HashMap<>(){{
        put('a',0);
        put('b',2);
    }};
    private double h;

    public Solver(){
        this.integral = new IterativeLegendreGaussIntegrator(
                10,
                1e-6,
                1e-6
        );

    }
    // base function
    private double e(double x, int i){
        if (h * (i - 1) <= x && x <= h * i){
            return x/h - i + 1;
        } else if (h * i <= x && x <= h * (i + 1)) {
            return -x/h + i +1;
        } else {
            return 0;
        }
    }
    // derivative of the base function
    private double eDerivative(double x, int i){
        if (h * (i - 1) <= x && x <= h * i){
            return 1/h;
        } else if (h * i <= x && x <= h * (i + 1)) {
            return -1/h;
        } else {
            return 0;
        }
    }
    // E function
    private double E(double x){
        return x <= 1 ? 3 : 5;
    }

    // create Matrix and fill it with B integrals
    private RealMatrix generateMatrixB(int n){

        RealMatrix B = new Array2DRowRealMatrix(n,n);

        for (int i = 0; i < n;i++){
            for(int j = 0; j < n;j++){
                double gaussLegendreIntegral = 0.0;

                if (Math.abs(i-j) <= 1) {


                    double from = (double) (domain.get('b') * Math.max(Math.max(i, j) - 1, domain.get('a'))) / n;
                    double to = (double) (domain.get('b') * Math.min(Math.min(i, j) + 1, n)) / n;

                    int finalI = i;
                    int finalJ = j;
                    gaussLegendreIntegral = integral.integrate(
                            Integer.MAX_VALUE,
                            x -> E(x) * eDerivative(x, finalI) * eDerivative(x, finalJ),
                            from,
                            to
                    );
                }
                B.setEntry(i,j, gaussLegendreIntegral - 3 * e(0,i) * e(0,j));
            }
        }
        return B;
    }
    // generate L vector and fill it with L function
    private RealVector generateVectorL(int n){

        RealVector L = new ArrayRealVector(n,0);

        for(int i = 0; i < n;i++){
            L.setEntry(i,-30*e(0,i));
        }
        return L;
    }
    // solve the equation
    public double[] solve (int n){

        setH(n);
        double[] solution = Arrays.copyOf(new LUDecomposition(generateMatrixB(n))
                .getSolver()
                .solve(generateVectorL(n))
                .toArray(),n+1);
        solution[n]=0;
        return solution;
    }

    private void setH(int n){
        this.h = (double) (domain.get('b') - domain.get('a')) / n;
    }

    public double getCombination(double x, double[] solution){
        double value = 0;
        for(double coefficient: solution){
            value+=coefficient*x;
        }
        return value;
    }

    public double getH() {
        return h;
    }
}
