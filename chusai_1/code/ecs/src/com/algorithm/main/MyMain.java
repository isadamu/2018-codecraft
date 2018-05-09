package com.algorithm.main;

import com.algorithm.deploy.*;
import com.algorithm.input.Input;
import com.algorithm.predict.models.Average;
import com.algorithm.util.DataClean;
import com.algorithm.util.Flavor;
import com.algorithm.util.ResToString;
import com.algorithm.vm.VM;

import java.util.List;

/**
 * 代码流程
 *
 * 放到自己写的类里面方便一点
 *
 * @author long
 * @date 2018-03-27 10:58:41
 */
public class MyMain {

    public String[] run(String[] ecsContent, String[] inputContent) {
        // 训练数据清洗
        List<VM> trainDatas = DataClean.clearDatas(ecsContent);

        // 解析 Input
        Input input = new Input(inputContent);

        // 预测算法
        int[] needs = myPredictByNormalAverage(trainDatas, input);

        // 部署前处理
//        DeployModifiedBefore dmb = new DeployModifiedBefore();
//        dmb.modify(input, needs, 0.95);

        // 部署算法
        List<int[]> deploy = myDeploy(needs, input);

        // 后处理
//        DeployModified dm = new DeployModified(0.5);
//        dm.modify(input, needs, deploy);

        return ResToString.covert(needs, deploy, input);
    }

    /**
     * 平均大法
     * @param trainDatas
     * @param input
     * @return
     */
    private int[] myPredictByNormalAverage(List<VM> trainDatas, Input input) {

        Average average = new Average(4);

        average.fit(trainDatas);

        double[] pred = average.predictNormal(input);

        int[] needs = new int[Flavor.MAX_RANK+1];
        for ( int flavor = 1; flavor <= Flavor.MAX_RANK; flavor++ ) {
            if ( !input.vmsCanUse[flavor] ) {
                continue;
            }
            needs[flavor] = (int)Math.rint(pred[flavor]);
        }

        return needs;
    }


    /**
     * 很简单，所有部署算法轮一遍
     * @param needs
     * @param input
     * @return
     */
    private List<int[]> myDeploy(int[] needs, Input input) {
        List<int[]> deploy = null;

        BestFit bf = new BestFit();
        deploy = helpDeploy(deploy, bf.solveAsc(myArrayCopy(needs), input));
        deploy = helpDeploy(deploy, bf.solveDsc(myArrayCopy(needs), input));

        FirstFit ff = new FirstFit();
        deploy = helpDeploy(deploy, ff.solveAsc(myArrayCopy(needs), input));
        deploy = helpDeploy(deploy, ff.solveDsc(myArrayCopy(needs), input));

        GreedKnapsack gk = new GreedKnapsack();
        deploy = helpDeploy(deploy, gk.solveAsc(myArrayCopy(needs), input));
        deploy = helpDeploy(deploy, gk.solveDsc(myArrayCopy(needs), input));

        GreedKnapsackValueModified gkvm = new GreedKnapsackValueModified();
        deploy = helpDeploy(deploy, gkvm.solveAsc(myArrayCopy(needs), input));
        deploy = helpDeploy(deploy, gkvm.solveDsc(myArrayCopy(needs), input));

        return deploy;
    }

    /**
     * 复制一个整形数组
     * @param array
     * @return
     */
    private int[] myArrayCopy(int[] array) {
        int[] copy = new int[array.length];
        System.arraycopy(array, 0, copy, 0, array.length);
        return copy;
    }

    /**
     * 比较两个 list 的 size，返回小的那一个
     * @param a
     * @param b
     * @return
     */
    private List<int[]> helpDeploy(List<int[]> a, List<int[]> b) {
        if ( a == null ) {
            return b;
        } else if ( b == null ) {
            return a;
        }
        return a.size() < b.size() ? a : b;
    }

}
