package com.algorithm.predict.models;

import com.algorithm.input.Input;
import com.algorithm.util.DateUtil;
import com.algorithm.util.Flavor;
import com.algorithm.vm.VM;

import java.util.Arrays;
import java.util.List;

/**
 * 平均算法，这就是基本线，算法总不能比这个还差吧
 *
 * @date 2018年4月12日15:18:12
 * @author long
 */
public class Average {

    private int week_num = 4;

    private final int WEEK_LEN = 7;

    private double[][] week_count = null;

    public Average() {}

    public Average(int week_num) {
        this.week_num = week_num;
    }

    /**
     * fit训练数据
     * @param trainDatas
     */
    public void fit(List<VM> trainDatas) {

        if ( DateUtil.diffByDay(trainDatas.get(0).date, trainDatas.get(trainDatas.size()-1).date) + 1 < week_num * WEEK_LEN ) {
            week_num = (DateUtil.diffByDay(trainDatas.get(0).date, trainDatas.get(trainDatas.size()-1).date) + 1) / WEEK_LEN;
        }

        week_count = new double[week_num*WEEK_LEN][Flavor.MAX_RANK+1];

        int idx = trainDatas.size()-1, max_len = week_num * WEEK_LEN;
        while ( idx >= 0 ) {
            VM vm = trainDatas.get(idx);

            int len = DateUtil.diffByDay(vm.date, trainDatas.get(trainDatas.size()-1).date) + 1;
            if ( len > max_len ) {
                break;
            }

            if ( vm.flavor <= 0 || vm.flavor > Flavor.MAX_RANK ) {
                idx--;
                continue;
            }

            week_count[max_len - len][vm.flavor] += 1.0;

            idx--;
        }

    }

    /**
     * 无加权的平均
     * @param input
     * @return
     */
    public double[] predictNormal(Input input) {

        double[] weights = new double[week_num];
        Arrays.fill(weights, 1.0 / (double)week_num);

        return predictWeighted(input, weights);
    }

    /**
     * 加权平均，权重需要输入
     * @param input
     * @param weights
     * @return
     */
    public double[] predictWeighted(Input input, double[] weights) {
        double[] pred = new double[Flavor.MAX_RANK+1];

        double[][] weekday_average = helpWeekDayAverage(weights);

        int diff = DateUtil.diffByDay(input.begin, input.end);
        for ( int i = 0; i < diff; i++ ) {
            double[] the_day_av = weekday_average[(i%WEEK_LEN)+1];
            for ( int flavor = 1; flavor <= Flavor.MAX_RANK; flavor++ ) {
                pred[flavor] += the_day_av[flavor];
            }
        }

        return pred;
    }

    /**
     * 辅助平均
     * @param weights
     * @return
     */
    private double[][] helpWeekDayAverage(double[] weights) {

        int shift = weights.length - week_num;

        double[][] weekday_average = new double[WEEK_LEN+1][Flavor.MAX_RANK+1];
        for ( int i = 1; i <= WEEK_LEN; i++ ) {
            for ( int j = i-1; j < week_count.length; j += WEEK_LEN ) {
                double weight = weights[j/WEEK_LEN + shift];
                for (int flavor = 1; flavor <= Flavor.MAX_RANK; flavor++) {
                    weekday_average[i][flavor] += week_count[j][flavor] * weight;
                }
            }
        }

        return weekday_average;
    }

}
