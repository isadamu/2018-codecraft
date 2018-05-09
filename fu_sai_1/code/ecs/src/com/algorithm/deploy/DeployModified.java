package com.algorithm.deploy;

import com.algorithm.input.Input;
import com.algorithm.vm.Flavor;

import java.util.List;

/**
 * 修改时间：2018-04-20
 *
 * 处理预测出的数据
 *
 * 可能最后一台物理机上只部署了一台或者很少的虚拟机
 *
 * 可以将这最后一台给删了
 *
 * @author long
 */
public class DeployModified {

    /**
     * 将所有物理机塞满
     * @param input
     * @param needs
     * @param deploy
     */
    public void modify(Input input, int[] needs, List<List<int[]>> deploy) {

        for ( int serverType = 0; serverType < input.cpu.length; serverType++ ) {
            if ( deploy.get(serverType).isEmpty() ) {
                continue;
            }
            for ( int[] put : deploy.get(serverType) ) {
                helpModified(input, needs, put, input.cpu[serverType], input.mem[serverType]);
            }
        }
    }


    /**
     * 辅助修改
     * @param input
     * @param needs
     * @param put
     * @param cpu_limit
     * @param mem_limit
     */
    private void helpModified(Input input, int[] needs, int[] put, int cpu_limit, int mem_limit) {

        int cpu_used = 0, mem_used = 0;
        for (int i = 1; i < put.length; i++) {
            if ( !input.flavorToPredict[i] ) {
                continue;
            }
            cpu_used += put[i] * Flavor.CPU[i];
            mem_used += put[i] * Flavor.MEM[i];
        }

        int[] order = {18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        for (int i = 0; i < order.length; ) {
            int flavor = order[i];
            if (!input.flavorToPredict[flavor]) {
                i++;
                continue;
            }

            if (cpu_used + Flavor.CPU[flavor] > cpu_limit
                    || mem_used + Flavor.MEM[flavor] > mem_limit) {
                i++;
                continue;
            }

            cpu_used += Flavor.CPU[flavor];
            mem_used += Flavor.MEM[flavor];

            needs[flavor] += 1;
            put[flavor] += 1;
        }

    }

}
