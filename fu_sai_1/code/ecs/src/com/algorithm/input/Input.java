package com.algorithm.input;

import com.algorithm.util.DateUtil;
import com.algorithm.vm.Flavor;

import java.util.Calendar;

/**
 * 将 Input 数据转化为一个类
 */
public class Input {

    public String[] serverName;
    public int cpu[];
    public int mem[];
    public int disk[];

    public boolean[] flavorToPredict; // true 表示这个规格的 VM 在输入中

    public Calendar begin;
    public Calendar end;

    public Input(String[] inputContent) {

        int shift = 0;

        int serverNum = Integer.parseInt(inputContent[shift++].trim());
        this.serverName = new String[serverNum];
        this.cpu = new int[serverNum];
        this.mem = new int[serverNum];
        this.disk = new int[serverNum];
        for ( int i = 0; i < serverNum; i++, shift++ ) {
            String[] physicalMachine = inputContent[shift].trim().split(" ");
            this.serverName[i] = physicalMachine[0].trim();
            this.cpu[i] = Integer.parseInt(physicalMachine[1].trim());
            this.mem[i] = Integer.parseInt(physicalMachine[2].trim());
            this.disk[i] = Integer.parseInt(physicalMachine[3].trim());
        }

        shift++;

        int flavorCount = Integer.parseInt(inputContent[shift++]);
        this.flavorToPredict = new boolean[Flavor.MAX_RANK+1];
        for ( int i = 0; i < flavorCount; i++, shift++ ) {
            String[] flavor = inputContent[shift].trim().split(" ");
            this.flavorToPredict[Integer.parseInt(flavor[0].trim().substring(6))] = true;
        }

        shift++;

        this.begin = DateUtil.stringToCalender(inputContent[shift++]);
        this.end = DateUtil.stringToCalender(inputContent[shift]);

        if (DateUtil.getHourInDay(this.end) == 23) {  // 说明是 23:59:59 的格式，需要给它加一天
            DateUtil.addDay(this.end, 1);
        }

    }
}
