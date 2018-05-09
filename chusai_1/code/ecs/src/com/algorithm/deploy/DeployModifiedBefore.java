package com.algorithm.deploy;

import com.algorithm.input.Input;
import com.algorithm.util.Flavor;

/**
 * 部署前处理
 */
public class DeployModifiedBefore {

    /**
     * 如果目前的资源比较不均匀，分配时就会造成不均匀
     * 尝试先将资源往均匀上面补
     */
    public void modify(Input input, int[] needs, double threshold) {

        int cpu_used = 0, mem_used = 0;
        for (int flavor = 1; flavor < Flavor.MAX_RANK; flavor++) {
            if ( !input.vmsCanUse[flavor] ) {
                continue;
            }
            cpu_used += needs[flavor] * Flavor.CPU[flavor];
            mem_used += needs[flavor] * Flavor.MEM[flavor];
        }

        int tmp1 = cpu_used / input.cpu;
        if ( tmp1 * input.cpu < cpu_used ) {
            tmp1++;
        }

        int tmp2 = mem_used / input.mem;
        if ( tmp2 * input.mem < mem_used ) {
            tmp2++;
        }

        int min_num = tmp1 > tmp2 ? tmp1 : tmp2;

        int[] order = null;
        if ( input.target == 0 ) {  // 补cpu的顺序还是有一点讲究的
            if ( (double)cpu_used / (min_num * input.cpu) >= threshold ) {
                return;
            }
            order = new int[]{13, 10, 7, 14, 11, 8, 15, 12, 9, 4, 5, 6, 1, 2, 3};
        } else if ( input.target == 1 ) { // 补mem不需要讲究
            if ( (double)mem_used / (min_num * input.mem) >= threshold ) {
                return;
            }
            order = new int[]{15, 12, 9, 14, 11, 8, 13, 10, 7, 6, 5, 4, 3, 2, 1};
        }

        for ( int i = 0; i < order.length; ) {
            int flavor = order[i];
            if ( !input.vmsCanUse[flavor] ) {
                i++;
                continue;
            }
            if ( cpu_used + Flavor.CPU[flavor] > input.cpu * min_num
                    || mem_used + Flavor.MEM[flavor] > input.mem * min_num ) {
                i++;
                continue;
            }

            cpu_used += Flavor.CPU[flavor];
            mem_used += Flavor.MEM[flavor];

            needs[flavor] += 1;
        }


    }

}
