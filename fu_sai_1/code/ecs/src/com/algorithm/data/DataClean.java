package com.algorithm.data;

import com.algorithm.vm.VM;

import java.util.ArrayList;
import java.util.List;

/**
 * 清理输入的训练数据，主要考虑输入数据可能有缺失的字段
 * @author long
 */
public class DataClean {

    /**
     * 清洗数据，并将数据切分，返回 VM 类
     * @param roughDatas
     * @return
     */
    public static List<VM> clearDatas(String[] roughDatas) {
        List<VM> clearedDatas = new ArrayList<>();
        for (int i = 0; i < roughDatas.length; i++) {
            if (roughDatas[i] == null || roughDatas[i].trim().equals("")) {
                continue;
            }
            String[] array = roughDatas[i].trim().split("\t");
            if ( array.length == 3 ) {
                clearedDatas.add(new VM(array));
            }
        }
        return clearedDatas;
    }

}
