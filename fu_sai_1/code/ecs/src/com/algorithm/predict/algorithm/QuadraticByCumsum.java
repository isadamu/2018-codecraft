package com.algorithm.predict.algorithm;

import com.algorithm.data.DataTransform;
import com.algorithm.input.Input;
import com.algorithm.predict.models.LinearRegression;
import com.algorithm.predict.normalization.MaxMinNormalization;
import com.algorithm.predict.sample.QuadraticByCumsumSample;
import com.algorithm.util.DateUtil;
import com.algorithm.vm.Flavor;
import com.algorithm.vm.VM;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：2018-04-20
 *
 * &&&&&&&&&&&&&&&&&&&&&&&&&&&&
 *
 * 目前存在问题，在线上训练不出结果
 *
 * &&&&&&&&&&&&&&&&&&&&&&&&&&&
 *
 * 使用累加和的方式来进行线性回归
 *
 * 使用百分比计算！！！
 *
 * @author long
 */
public class QuadraticByCumsum {

    private MaxMinNormalization norm = null;

    private double learning_rate = 0.0;

    private String optimizer = "SGD";

    private int step = 1000;

    private int visible = 0;

    private int sample_num = 0;

    /**
     * 构造函数，需要一系列参数
     * @param sample_num
     */
    public QuadraticByCumsum(double learning_rate, String optimizer, int step, int visible, int sample_num) {
        this.learning_rate = learning_rate;
        this.optimizer = optimizer;
        this.step = step;
        this.visible = visible;
        this.sample_num = sample_num;
    }

    /**
     * 入口函数
     * @param trainDatas
     * @param input
     * @return
     */
    public int[] predict(List<VM> trainDatas, Input input) {

        // 检查一下数据长度是否有这么多天
        int data_len = DateUtil.diffByDay(trainDatas.get(0).date, trainDatas.get(trainDatas.size()-1).date) + 1;
        if ( sample_num  > data_len ) {
            sample_num = data_len;
        }

        QuadraticByCumsumSample rlbcs = new QuadraticByCumsumSample(trainDatas, sample_num);
        rlbcs.generateSamples();
        List<double[]> X_train = rlbcs.getX_train();
        List<Double> Y_train = rlbcs.getY_train();

        double[] percent = DataTransform.helpPrecent(trainDatas, sample_num);

        LinearRegression model = helpTrain(X_train, Y_train);

        return helpPredict(model, input, trainDatas, percent);
    }

    /**
     * 模型训练部分
     * @param X_train
     * @param Y_train
     * @return
     */
    private LinearRegression helpTrain(List<double[]> X_train, List<Double> Y_train) {

        List<double[]> X = new ArrayList<>();
        for ( double[] x : X_train ) {
            X.add(new double[]{x[0], x[0]*x[0]});
        }

        norm = new MaxMinNormalization();
        X = norm.minMaxNormalizationX(X);
        List<Double> Y = norm.minMaxNormalizationY(Y_train);

        LinearRegression model = new LinearRegression(learning_rate, optimizer, step, visible);

        model.fit(X, Y);

        return model;
    }

    /**
     * 模型预测部分
     * @param model
     * @param input
     * @param trainDatas
     * @return
     */
    private int[] helpPredict(LinearRegression model, Input input, List<VM> trainDatas, double[] percent) {

        // 预测最后时间累积了多少虚拟机

        int begin = DateUtil.diffByDay(trainDatas.get(trainDatas.size()-1).date, input.begin) + sample_num - 1;
        int end = DateUtil.diffByDay(trainDatas.get(trainDatas.size()-1).date, input.end) + sample_num - 1;

        double[] x_predict_1 = {begin, begin*begin};
        double[] x_predict_2 = {end, end*end};

        x_predict_1 = norm.helpNormalizationX(x_predict_1);
        x_predict_2 = norm.helpNormalizationX(x_predict_2);

        double res_1 = model.predict(x_predict_1);
        res_1 = norm.recoveryY(res_1);

        double res_2 = model.predict(x_predict_2);
        res_2 = norm.recoveryY(res_2);

        // 预测在需要预测之间累积了多少虚拟机（可能训练数据与预测时间之间存在间隔）
        double res_total = res_2 - res_1;

        int[] predict = new int[Flavor.MAX_RANK+1];
        if ( res_total <= 0 ) {
            return predict;
        }
        for ( int flavor = 1; flavor <= Flavor.MAX_RANK; flavor++ ) {
            if ( !input.flavorToPredict[flavor] ) { // 可能预测结果小于零
                continue;
            }
            predict[flavor] = (int)(percent[flavor] * res_total + 0.5);
        }

        return predict;
    }


}
