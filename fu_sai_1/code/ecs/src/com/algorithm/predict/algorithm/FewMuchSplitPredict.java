package com.algorithm.predict.algorithm;

import com.algorithm.input.Input;
import com.algorithm.util.DateUtil;
import com.algorithm.vm.Flavor;
import com.algorithm.vm.VM;

import java.util.List;

/**
 * 创建日期：2018-04-22
 *
 * 对于数量多的 Flavor 用线性回归
 * 对于数量少的 Flavor 用平均
 *
 * @author long
 */
public class FewMuchSplitPredict {

    private double learning_rate = 0.0;

    private String optimizer = "SGD";

    private int step = 10000;

    private int visible = 0;

    /**
     * 以占比为标准，定义出占比的门限
     */
    private double percent_thresh = 0;

    private int sample_num = 0;

    public FewMuchSplitPredict(double learning_rate, String optimizer, int step, int visible, double percent_thresh, int sample_num) {
        this.learning_rate = learning_rate;
        this.optimizer = optimizer;
        this.step = step;
        this.visible = visible;
        this.percent_thresh = percent_thresh;
        this.sample_num = sample_num;
    }

    /**
     * 预测（包含线性以及平均）
     * @param trainDatas
     * @param input
     * @return
     */
    public int[] predict(List<VM> trainDatas, Input input) {

        NormalAverage na = new NormalAverage(sample_num / 7);
        int[] predict_avg = na.myPredictByNormalAverage(trainDatas, input);

        RealLinear rl = new RealLinear(sample_num);
        int[] predict_linear = rl.predict(trainDatas, input);

//        QuadraticByCumsum rlbc = new QuadraticByCumsum(learning_rate, optimizer, step, visible, sample_num);
//        int[] predict_linear = rlbc.predict(trainDatas, input);

        double[] percent = helpPrecent(trainDatas, sample_num);

        int[] res = new int[Flavor.MAX_RANK+1];
        for ( int flavor = 1; flavor <= Flavor.MAX_RANK; flavor++ ) {
            if ( !input.flavorToPredict[flavor] ) {
                continue;
            }
            if ( percent[flavor] < percent_thresh || predict_linear[flavor] <= 0 ) {
                res[flavor] = predict_avg[flavor];
            } else {
                res[flavor] = predict_linear[flavor];
            }
        }

        return res;
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
