package com.algorithm.predict.sample;

import com.algorithm.data.DataTransform;
import com.algorithm.input.Input;
import com.algorithm.vm.Flavor;
import com.algorithm.vm.VM;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：2018-04-20
 *
 * 真实线性回归的采样
 *
 * @author long
 */
public class RealLinearSample {

    private final int WEEK_LEN = 7;

    private int sample_num = 4;

    private int[][] day_vms = null;

    private List<VM> trainData = null;

    private Input input = null;

    private List<double[]> X = null;

    private List<double[]> Y = null;

    public RealLinearSample(List<VM> trainDatas, Input input, int sample_num) {
        this.trainData = trainDatas;
        this.input = input;
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

        for ( int sample = sample_num, idx = day_vms.length-1; sample > 0; sample--, idx-- ) {
            double[] x = new double[1];
            x[0] = sample;

            double[] y = new double[Flavor.MAX_RANK+1];

            for ( int i = 0, j = idx; i < WEEK_LEN; i++, j-- ) {
                for ( int flavor = 1; flavor <= Flavor.MAX_RANK; flavor++ ) {
                    y[flavor] += day_vms[j][flavor];
                }
            }

            X.add(0, x);
            Y.add(0, y);
        }
    }

    /**
     * 初始化，主要统计一下数据的时间跨度
     */
    private void init() {
        // 统计每天的 VM 数量
        this.day_vms = DataTransform.toDayArray(trainData);
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
    public List<double[]> getY_train() {
        return this.Y;
    }

}
