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
public class RealLinear2 {

    private final int WEEK_LEN = 7;

    private int sample_num = 0;

    /**
     * 构造函数
     * @param sample_num
     */
    public RealLinear2(int sample_num) {
        this.sample_num = sample_num;
    }

    /**
     * 入口函数
     * @param trainDatas
     * @param input
     * @return
     */
    public int[] predict(List<VM> trainDatas, Input input) {

        XYLinearSolver model = helpTrain(trainDatas, input);

        return helpPredict(model, input, trainDatas);
    }

    /**
     * 模型训练部分
     * @param trainDatas
     * @param input
     * @return
     */
    private XYLinearSolver helpTrain(List<VM> trainDatas, Input input) {

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

        List<Double> Y_total = new ArrayList<>();
        for ( double[] y : Y_train ) {
            double sum = 0.0;
            for ( int flavor = 1; flavor <= Flavor.MAX_RANK; flavor++ ) {
                sum += y[flavor];
            }
            Y_total.add(sum);
        }

        XYLinearSolver model = new XYLinearSolver();

        model.fit(X, Y_total);

        return model;
    }

    /**
     * 模型预测部分
     * @param model
     * @param input
     * @param trainDatas
     * @return
     */
    private int[] helpPredict(XYLinearSolver model, Input input, List<VM> trainDatas) {

        int begin = DateUtil.diffByDay(trainDatas.get(trainDatas.size()-1).date, input.begin) + sample_num;
        int end = DateUtil.diffByDay(trainDatas.get(trainDatas.size()-1).date, input.end) + sample_num - 1;

        double sum = 0.0;
        for (int x = end; x > begin; x -= WEEK_LEN) {   // 一周一周的预测
            double y = model.predict(x);
            if ( x - begin + 1 >= WEEK_LEN ) { // 剩下的时间跨度足够一星期
                sum += y;
            } else {
                sum += y * ( (double)(x - begin + 1) / WEEK_LEN ); // 不足的时候按照比例衰减
            }
        }

        double[] percent = helpPrecent(trainDatas, sample_num);

        int[] predict = new int[Flavor.MAX_RANK+1];
        for ( int flavor = 1; flavor <= Flavor.MAX_RANK; flavor++ ) {
            if ( !input.flavorToPredict[flavor] || sum <= 0 ) { // 可能预测结果小于零
                continue;
            }
            predict[flavor] = (int)(percent[flavor]*sum + 0.5);
        }

        return predict;
    }

    /**
     * 辅助划分出每一种flavor在一段时间里面占总量的比例
     * @param trainData
     * @return
     */
    private double[] helpPrecent(List<VM> trainData, int percent_len) {

        int data_len_day = DateUtil.diffByDay(trainData.get(0).date, trainData.get(trainData.size()-1).date);
        data_len_day += 1;

        int use_to_percent = data_len_day < percent_len ? data_len_day : percent_len;

        // 统计每天的 VM 数量
        int[] flavor_counts = new int[Flavor.MAX_RANK+1];

        int idx = 0;
        for ( VM vm : trainData ) {
            int diff = DateUtil.diffByDay(vm.date, trainData.get(trainData.size()-1).date);
            if ( diff >= use_to_percent || vm.flavor <= 0 || vm.flavor > Flavor.MAX_RANK ) {
                continue;
            }
            flavor_counts[vm.flavor]++;
        }

        double sum = 0;
        for ( int flavor = 1; flavor <= Flavor.MAX_RANK; flavor++ ) {
            sum += flavor_counts[flavor];
        }

        double[] percent = new double[Flavor.MAX_RANK+1];
        for ( int flavor = 1; flavor <= Flavor.MAX_RANK; flavor++ ) {
            percent[flavor] = flavor_counts[flavor] / sum;
        }

        return percent;
    }

}
