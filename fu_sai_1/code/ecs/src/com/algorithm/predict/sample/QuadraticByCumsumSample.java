package com.algorithm.predict.sample;

import com.algorithm.data.DataTransform;
import com.algorithm.vm.Flavor;
import com.algorithm.vm.VM;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：2018-04-20
 *
 * 使用另外的方式来给线性回归采样
 * 也就是 flavor 的数量按照时间累加
 *
 * @author long
 */
public class QuadraticByCumsumSample {

    private int sample_num = 4;

    private int[] cumsum_vms = null;

    private List<VM> trainData = null;

    private List<double[]> X = null;

    private List<Double> Y = null;

    public QuadraticByCumsumSample(List<VM> trainDatas, int sample_num) {
        this.trainData = trainDatas;
        this.sample_num = sample_num;
        init();
    }

    /**
     * 从数据中生成样本（周为最小单位）
     * @return
     */
    public void generateSamples() {

        this.X = new ArrayList<>();
        this.Y = new ArrayList<>();

        for ( int sample = sample_num, idx = cumsum_vms.length-1; sample > 0; sample--, idx-- ) {
            double[] x = new double[1];
            x[0] = sample;

            double y = cumsum_vms[idx];

            X.add(0, x);
            Y.add(0, y);
        }


    }

    /**
     * 初始化，主要统计一下数据的时间跨度
     */
    private void init() {
        // 统计按天统计的 VM 总和
        this.cumsum_vms = DataTransform.toDayCumsum(trainData);
    }

    /**
     * 取到生成的 X
     * @return
     */
    public List<double[]> getX_train() {
        return this.X;
    }

    /**
     * 取到生成的 Y
     * @return
     */
    public List<Double> getY_train() {
        return this.Y;
    }

}
