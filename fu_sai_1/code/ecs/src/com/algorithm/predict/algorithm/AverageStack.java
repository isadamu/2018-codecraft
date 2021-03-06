package com.algorithm.predict.algorithm;

import com.algorithm.input.Input;
import com.algorithm.vm.Flavor;
import com.algorithm.vm.VM;

import java.util.List;

/**
 * 创建日期：2018-04-20
 *
 * 平均集成
 *
 * @author long
 */
public class AverageStack {

    private int model_count = 0;

    public int[] predict(List<VM> trainDatas, Input input) {

        int[] needs = new int[Flavor.MAX_RANK+1];
        int[] new_predict;

        NormalAverage na = new NormalAverage(4);
        new_predict = na.myPredictByNormalAverage(trainDatas, input);
        helpAdd(needs, new_predict);

        RealLinear rl = new RealLinear(60);
        new_predict = rl.predict(trainDatas, input);
        helpAdd(needs, new_predict);

//        QuadraticByCumsum rlbc = new QuadraticByCumsum(0.00001, "SGD_M", 60*50, 1, 60);
//        new_predict = rlbc.predict(trainDatas, input);
//        helpAdd(needs, new_predict);


        for (int flavor = 1; flavor <= Flavor.MAX_RANK; flavor++ ) {
            needs[flavor] = (int)(needs[flavor] / (double)model_count + 0.5);
        }
        return needs;
    }

    private void helpAdd(int[] needs, int[] new_predict) {
        model_count++;
        for ( int flavor = 1; flavor <= Flavor.MAX_RANK; flavor++ ) {
            needs[flavor] += new_predict[flavor];
        }
    }

}
