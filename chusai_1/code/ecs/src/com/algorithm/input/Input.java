package com.algorithm.input;

import com.algorithm.util.DateUtil;
import com.algorithm.util.Flavor;

import java.util.Calendar;

/**
 * 将 Input 数据转化为一个类
 */
public class Input {

    public int cpu;
    public int mem;
    public int disk;

    public boolean[] vmsCanUse; // true 表示这个规格的 VM 在输入中

    public int target;  // 0 表示 CPU，1 表示 内存

    public Calendar begin;
    public Calendar end;

    public Input(String[] inputContent) {

        String[] physicalMachine = inputContent[0].split(" ");
        this.cpu = Integer.parseInt(physicalMachine[0]);
        this.mem = Integer.parseInt(physicalMachine[1]);
        this.disk = Integer.parseInt(physicalMachine[2]);

        int flavorCount = Integer.parseInt(inputContent[2]);
        this.vmsCanUse = new boolean[Flavor.MAX_RANK+1];
        for ( int i = 3, end = flavorCount + 3; i < end; i++ ) {
            String[] flavor = inputContent[i].split(" ");
            vmsCanUse[Integer.parseInt(flavor[0].substring(6))] = true;
        }

        int targetIdx = flavorCount + 3 + 1;
        if ( inputContent[targetIdx].contains("CPU") ) {
            this.target = 0;
        } else {
            this.target = 1;
        }

        this.begin = DateUtil.stringToCalender(inputContent[targetIdx + 2]);
        this.end = DateUtil.stringToCalender(inputContent[targetIdx + 3]);
    }
}
