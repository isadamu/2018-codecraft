package com.algorithm.predict.models;

import java.util.List;

/**
 * 线性回归类
 *
 * 按照 sklearn，提供 train 和 predict
 *
 * @modified 2018-03-27 10:04:03
 * @date 2018-03-17 15:12:05
 * @author long
 */
public class LinearRegression {

    // 学习率
    private double learning_rate = 0.0001;

    // 学习精度
    private int max_step = 1000;

    private double min_loss = 0.000001;

    private int visible = 2;

    private double[] weights = null;

    // 优化器 "SGD" 或者 "BatchGD" 或者 "SGD_M"
    private String optimizer = "SGD";

    public LinearRegression() {}

    public LinearRegression(double learning_rate, String optimizer, int step, int visible) {
        this.learning_rate = learning_rate;
        this.optimizer = optimizer;
        this.max_step = step;
        this.visible = visible;
    }

    /**
     * 模型训练，得到权重
     * @param X_train
     * @param Y_train
     * @return
     */
    public void fit(List<double[]> X_train, List<Double> Y_train) {

        double[][] X = addBiasToX(X_train);
        double[] Y = new double[Y_train.size()];
        for ( int i = 0; i < Y_train.size(); i++ ) {
            Y[i] = Y_train.get(i);
        }

        this.weights = new double[X[0].length];

        int step = 0;
        int idx = 0;
        while ( step <= this.max_step ) {
            double loss = computeLoss(X, Y, this.weights);
            if ( loss <= this.min_loss ) {
                break;
            }
            if ( this.visible >= 2 ) {
                System.out.println("step = " + step + ", Loss = " + loss);
            }
            if ( optimizer == "SGD" ) {
                this.weights = SGD(X[idx], Y[idx], this.weights);
                idx++;
                idx %= X.length;
            } else if ( optimizer == "BatchGD" ) {
                this.weights = BatchGD(X, Y, this.weights);
            } else if ( optimizer == "SGD_M") {
                this.weights = ModifiedSGD(X[idx], Y[idx], this.weights);
                idx++;
                idx %= X.length;
            } else {
                System.err.println("There is no optimizer " + optimizer);
                System.exit(1);
            }
            step++;
        }
        if ( this.visible >= 1) {
            System.out.println("********************************************");
            System.out.println("train over.\n" + "Loss = " + computeLoss(X, Y, weights));
            System.out.println("********************************************");
        }
    }

    /**
     * 预测 y 值
     * @param X
     * @return
     */
    public double predict(double[] X) {

        return computeY(addBiasToSample(X), this.weights);
    }

    /**
     * 给输入的样本添加 bias
     * @param X
     * @return
     */
    private double[][] addBiasToX(List<double[]> X) {
        double[][] xAndBias = new double[X.size()][];
        for ( int i = 0; i < X.size(); i++ ) {
            xAndBias[i] = addBiasToSample(X.get(i));
        }
        return xAndBias;
    }

    /**
     * 添加 bias，也就是在数组上增加一维，并将其值置为 1.0
     * @param X
     * @return
     */
    private double[] addBiasToSample(double[] X) {
        double[] xAndBias = new double[X.length+1];
        System.arraycopy(X, 0, xAndBias, 1, X.length);
        xAndBias[0] = 1.0;
        return xAndBias;
    }

    /**
     * 随机梯度下降
     * @param X
     * @param Y
     * @param weights
     * @return
     */
    private double[] SGD(double[] X, double Y, double[] weights) {
        double[] weightsNew = new double[weights.length];
        for ( int i = 0; i < weightsNew.length; i++ ) {
            double gradient = this.learning_rate * X[i] * (Y - computeY(X, weights));
            weightsNew[i] = weights[i] + gradient;
        }
        return weightsNew;
    }

    /**
     * 随机梯度下降(魔改版)
     * @param X
     * @param Y
     * @param weights
     * @return
     */
    private double[] ModifiedSGD(double[] X, double Y, double[] weights) {
        double[] weightsNew = new double[weights.length];
        for ( int i = 0; i < weightsNew.length; i++ ) {
            double gradient = this.learning_rate * X[i] * (Y - computeY(X, weights));
            if ( Y - computeY(X, weights) > 0 ) {
                gradient *= 5;
            }
            weightsNew[i] = weights[i] + gradient;
        }
        return weightsNew;
    }

    /**
     * 批梯度下降
     * @param X
     * @param Y
     * @param weights
     * @return
     */
    private double[] BatchGD(double[][] X, double[] Y, double[] weights) {
        double[] weightsNew = new double[weights.length];
        for (int i = 0; i < weightsNew.length; i++) {
            double gradient = 0.0;
            for ( int j = 0; j < X.length; j++ ) {
                gradient += X[j][i] * (Y[j] - computeY(X[j], weights));
            }
            weightsNew[i] = weights[i] + this.learning_rate * gradient;
        }
        return weightsNew;
    }

    /**
     * 损失函数
     * @param X
     * @param Y
     * @param weights
     * @return
     */
    private double computeLoss(double[][] X, double[] Y, double[] weights) {
        double loss = 0;
        for ( int i = 0; i < Y.length; i++) {
            double y = computeY(X[i], weights);
            double tmp = y - Y[i];
            loss += tmp * tmp;
        }
        return loss / 2;
    }

    /**
     * 计算预测值
     * @param X
     * @param weights
     * @return
     */
    private double computeY(double[] X, double[] weights) {
        return helpMultiply(X, weights);
    }

    /**
     * 乘法辅助
     * @param a
     * @param b
     * @return
     */
    private double helpMultiply( double[] a, double[] b ) {
        double res = 0.0;
        for ( int i = 0; i < a.length; i++ ) {
            res += a[i] * b[i];
        }
        return res;
    }

}
