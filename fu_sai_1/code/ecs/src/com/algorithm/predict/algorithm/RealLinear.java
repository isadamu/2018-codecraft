package com.algorithm.predict.algorithm;

import com.algorithm.input.Input;
import com.algorithm.predict.models.XYLinearSolver;
import com.algorithm.predict.sample.RealLinearSample;
import com.algorithm.util.DateUtil;
import com.algorithm.vm.Flavor;
import com.algorithm.vm.VM;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：2018-04-20
 *
 * 真正的线性回归！！！
 *
 * @author long
 */
public class RealLinear {

    private final int WEEK_LEN = 7;

    private int sample_num = 0;

    /**
     * 构造函数，需要一系列参数
     * @param sample_num
     */
    public RealLinear(int sample_num) {
        this.sample_num = sample_num;
    }

    /**
     * 入口函数
     * @param trainDatas
     * @param input
     * @return
     */
    public int[] predict(List<VM> trainDatas, Input input) {

        XYLinearSolver[] models = helpTrain(trainDatas, input);

        return helpPredict(models, input, trainDatas);
    }

    /**
     * 模型训练部分
     * @param trainDatas
     * @param input
     * @return
     */
    private XYLinearSolver[] helpTrain(List<VM> trainDatas, Input input) {

        int data_len = DateUtil.diffByDay(trainDatas.get(0).date, trainDatas.get(trainDatas.size()-1).date) + 1;

        if ( sample_num + WEEK_LEN - 1 > data_len ) {
            sample_num = data_len - WEEK_LEN + 1;
        }

        RealLinearSample rls = new RealLinearSample(trainDatas, input, sample_num);
        rls.generateSamples();
        List<double[]> X_train = rls.getX_train();
        List<double[]> Y_train = rls.getY_train();
        List<Double> X = new ArrayList<>();
        for ( double[] x : X_train ) {
            X.add(x[0]);
        }

        XYLinearSolver[] models = new XYLinearSolver[Flavor.MAX_RANK+1]; // 每一种flavor一个模型
        for ( int flavor = 1; flavor <= Flavor.MAX_RANK; flavor++ ) {
            if ( !input.flavorToPredict[flavor] ) {
                continue;
            }

            List<Double> Y_split = new ArrayList<>();
            for ( double[] y_train : Y_train ) {
                Y_split.add(y_train[flavor]);
            }

            models[flavor] = new XYLinearSolver();
            models[flavor].fit(X, Y_split);
        }
        return models;
    }

    /**
     * 模型预测部分
     * @param models
     * @param input
     * @param trainDatas
     * @return
     */
    private int[] helpPredict(XYLinearSolver[] models, Input input, List<VM> trainDatas) {
        double[] res = new double[Flavor.MAX_RANK+1];

        int begin = DateUtil.diffByDay(trainDatas.get(trainDatas.size()-1).date, input.begin) + sample_num;
        int end = DateUtil.diffByDay(trainDatas.get(trainDatas.size()-1).date, input.end) + sample_num - 1;

        for (int x = end; x > begin; x -= WEEK_LEN) {   // 一周一周的预测
            for ( int flavor = 1; flavor <= Flavor.MAX_RANK; flavor++ ) {
                if ( !input.flavorToPredict[flavor] ) {
                    continue;
                }
                double y = models[flavor].predict(x);
                if ( x - begin + 1 >= WEEK_LEN ) { // 剩下的时间跨度足够一星期
                    res[flavor] += y;
                } else {
                    res[flavor] += y * ( (double)(x - begin + 1) / WEEK_LEN ); // 不足的时候按照比例衰减
                }
            }
        }

        int[] predict = new int[Flavor.MAX_RANK+1];
        for ( int flavor = 1; flavor <= Flavor.MAX_RANK; flavor++ ) {
            if ( !input.flavorToPredict[flavor] || res[flavor] <= 0 ) { // 可能预测结果小于零
                continue;
            }
            predict[flavor] = (int)(res[flavor] + 0.5);
        }

        return predict;
    }


}
