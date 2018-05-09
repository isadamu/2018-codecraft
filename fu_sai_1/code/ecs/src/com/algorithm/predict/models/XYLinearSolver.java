package com.algorithm.predict.models;

import java.util.List;

/**
 * 创建日期：2018-04-22
 *
 * 直接求解线性函数（一元一次）
 *
 * y = kx + b
 *
 * @author long
 */
public class XYLinearSolver {

    private double k = 0.0;

    private double b = 0.0;

    /**
     * 训练部分
     * @param X
     * @param Y
     */
    public void fit(List<Double> X, List<Double> Y) {

        double m = X.size();

        double sum1 = 0.0;
        for ( int i = 0; i < m; i++ ) {
            sum1 += X.get(i) * Y.get(i);
        }

        double sum2 = 0.0;
        for ( int i = 0; i < m; i++ ) {
            for ( int j = 0; j < m; j++ ) {
                sum2 += X.get(i) * Y.get(j);
            }
        }
        sum2 /= m;

        double sum3 = 0.0;
        for ( int i = 0; i < m; i++ ) {
            sum3 += X.get(i) * X.get(i);
        }

        double sum4 = 0.0;
        for ( int i = 0; i < m; i++ ) {
            for ( int j = 0; j < m; j++ ) {
                sum4 += X.get(i) * X.get(j);
            }
        }
        sum4 /= m;

        if ( sum3 - sum4 == 0 ) { // 注意斜率为零的情况
            k = 0.0;
        } else {
            k = (sum1 - sum2) / (sum3 - sum4);
        }

        double sum5 = 0.0;
        for ( int i = 0; i < m; i++ ) {
            sum5 += Y.get(i);
        }

        double sum6 = 0.0;
        for ( int i = 0; i < m; i++ ) {
            sum6 += X.get(i);
        }
        sum6 *= k;

        b = (sum5 - sum6) / m;

    }

    /**
     * 预测部分
     * @param x
     * @return
     */
    public double predict(double x) {
        return k * x + b;
    }

}
