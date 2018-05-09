package com.algorithm.predict.normalization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 创建日期：2018-04-23
 *
 * 最大最小归一化
 *
 * @author long
 */
public class MaxMinNormalization {

    private double[] min_x = null;

    private double[] max_x = null;

    private double min_y = -1;

    private double max_y = -1;

    /**
     * 归一化 X
     * @param X_train
     * @return
     */
    public List<double[]> minMaxNormalizationX(List<double[]> X_train) {
        this.min_x = new double[X_train.get(0).length];
        Arrays.fill(min_x, Double.POSITIVE_INFINITY);
        this.max_x = new double[X_train.get(0).length];
        Arrays.fill(max_x, Double.NEGATIVE_INFINITY);

        for ( double[] x : X_train ) {
            for ( int i = 0; i < x.length; i++ ) {
                if ( x[i] < min_x[i] ) {
                    min_x[i] = x[i];
                }
                if ( x[i] > max_x[i] ) {
                    max_x[i] = x[i];
                }
            }
        }

        List<double[]> X_norm = new ArrayList<>();
        for ( double[] x : X_train ) {
            X_norm.add(helpNormalizationX(x));
        }
        return X_norm;
    }

    /**
     * 归一化一个样本
     * @param x
     * @return
     */
    public double[] helpNormalizationX(double[] x) {
        double[] x_norm = new double[x.length];
        for ( int i = 0; i < x.length; i++ ) {
            x_norm[i] = (x[i] - min_x[i]) / (max_x[i] - min_x[i]);
        }
        return x_norm;
    }

    /**
     * 归一化 Y
     * @param Y_train
     * @return
     */
    public List<Double> minMaxNormalizationY(List<Double> Y_train) {
        this.min_y = Double.POSITIVE_INFINITY;
        this.max_y = Double.NEGATIVE_INFINITY;

        for ( double y : Y_train ) {
            if ( y < min_y ) {
                min_y = y;
            }
            if ( y > max_y ) {
                max_y = y;
            }
        }

        List<Double> norm = new ArrayList<>();
        for ( double y : Y_train ) {
            norm.add((y - min_y)/(max_y - min_y));
        }

        return norm;
    }

    /**
     * 帮助一个 Y 进行恢复
     * @param y
     * @return
     */
    public Double recoveryY(double y) {
        return y * (max_y - min_y) + min_y;
    }


}
