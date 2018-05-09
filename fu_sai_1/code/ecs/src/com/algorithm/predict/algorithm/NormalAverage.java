package com.algorithm.predict.algorithm;

import com.algorithm.input.Input;
import com.algorithm.predict.models.Average;
import com.algorithm.vm.Flavor;
import com.algorithm.vm.VM;

import java.util.List;

/**
 * 创建日期：2018-04-20
 *
 * 平均预测大法
 *
 * @author long
 */
public class NormalAverage {

    private int week_num = 0;

    public NormalAverage(int week_num) {
        this.week_num = week_num;
    }

    /**
     * 平均大法
     * @param trainDatas
     * @param input
     * @return
     */
    public int[] myPredictByNormalAverage(List<VM> trainDatas, Input input) {

        Average average = new Average(week_num);

        average.fit(trainDatas);

        double[] pred = average.predictNormal(input);

        int[] needs = new int[Flavor.MAX_RANK+1];
        for ( int flavor = 1; flavor <= Flavor.MAX_RANK; flavor++ ) {
            if ( !input.flavorToPredict[flavor] ) {
                continue;
            }
            needs[flavor] = (int)Math.rint(pred[flavor]);
        }

        return needs;
    }
}
