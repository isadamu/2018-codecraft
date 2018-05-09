package com.algorithm.main;

import com.algorithm.deploy.*;
import com.algorithm.input.Input;
import com.algorithm.predict.algorithm.*;
import com.algorithm.data.DataClean;
import com.algorithm.util.DateUtil;
import com.algorithm.util.ResToString;
import com.algorithm.vm.VM;

import java.util.Calendar;
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


        /**********************************/
        /********** 训练数据清洗 ************/
        List<VM> trainDatas = DataClean.clearDatas(ecsContent);


        /****************************************/
        /************** 解析 Input ***************/
        Input input = new Input(inputContent);


        /**************************************/
        /************** 预测算法 ***************/

//        AverageStack as = new AverageStack();
//        int[] needs = as.predict(trainDatas, input);

//        MaxStack ms = new MaxStack();
//        int[] needs = ms.predict(trainDatas, input);

//        NormalAverage na = new NormalAverage(4);
//        int[] needs = na.myPredictByNormalAverage(trainDatas, input);

//        RealLinear rl = new RealLinear(30);
//        int[] needs = rl.predict(trainDatas, input);

//        RealLinear2 rl = new RealLinear2(60);
//        int[] needs = rl.predict(trainDatas, input);

//        QuadraticByCumsum rl = new QuadraticByCumsum(0.003, "SGD", 10000, 0,60);
//        int[] needs = rl.predict(trainDatas, input);

        FewMuchSplitPredict fmsp = new FewMuchSplitPredict(0.003, "SGD", 10000, 0,0.02, 60);
        int[] needs = fmsp.predict(trainDatas, input);

        /*************************************************/
        /********************* 部署算法 *******************/
        MyDeploy md = new MyDeploy();
        List<List<int[]>> deploy = md.deploy(needs, input);

        return ResToString.covert(needs, deploy, input);
    }


}
