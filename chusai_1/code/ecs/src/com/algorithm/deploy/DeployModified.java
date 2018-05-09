package com.algorithm.deploy;

import com.algorithm.input.Input;
import com.algorithm.util.Flavor;

import java.util.List;

/**
 * 处理预测出的数据
 *
 * 可能最后一台物理机上只部署了一台或者很少的虚拟机
 *
 * 可以将这最后一台给删了
 *
 * @date 2018年4月13日21:15:03
 * @author long
 */
public class DeployModified {

    private double threshold = 0.5;

    public DeployModified() {}

    public DeployModified(double threshold) {
        this.threshold = threshold;
    }

    /**
     * 如果最后一台的资源占用低于 threshold，就把最后一台物理机给删除了
     * 如果大于 threshold，那么就给它补满
     * @param needs
     * @param deploy
     */
    public void modify(Input input, int[] needs, List<int[]> deploy) {

        if ( deploy.size() <= 1 ) { // 如果只有一台，那显然不用操作了，删没了怎么办
            return;
        }

        int[] last = deploy.get(deploy.size()-1);

        int cpu_used = 0, mem_used = 0;
        for (int i = 1; i < last.length; i++) {
            if ( !input.vmsCanUse[i] ) {
                continue;
            }
            cpu_used += last[i] * Flavor.CPU[i];
            mem_used += last[i] * Flavor.MEM[i];
        }

        double usage = 0.0;
        if ( input.target == 0 ) {
            usage = ((double)cpu_used) / input.cpu;
        } else if ( input.target == 1 ) {
            usage = ((double)mem_used) / input.mem;
        }

        if ( usage < threshold ) {
            for (int i = 1; i < last.length; i++) {
                if ( !input.vmsCanUse[i] ) {
                    continue;
                }
                needs[i] -= last[i];
            }
            deploy.remove(deploy.size()-1);
        } else {

            int[] order = null;
            if ( input.target == 0 ) {  // 补cpu的顺序还是有一点讲究的
                order = new int[]{13, 10, 7, 14, 11, 8, 15, 12, 9, 4, 5, 6, 1, 2, 3};
            } else if ( input.target == 1 ) { // 补mem不需要讲究
                order = new int[]{15, 12, 9, 14, 11, 8, 13, 10, 7, 6, 5, 4, 3, 2, 1};
            }

            for ( int i = 0; i < order.length; ) {
                int flavor = order[i];
                if ( !input.vmsCanUse[flavor] ) {
                    i++;
                    continue;
                }
                if ( cpu_used + Flavor.CPU[flavor] > input.cpu
                        || mem_used + Flavor.MEM[flavor] > input.mem ) {
                    i++;
                    continue;
                }

                cpu_used += Flavor.CPU[flavor];
                mem_used += Flavor.MEM[flavor];

                needs[flavor] += 1;
                last[flavor] += 1;
            }

        }
    }

}
