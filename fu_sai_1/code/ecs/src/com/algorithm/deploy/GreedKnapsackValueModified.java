package com.algorithm.deploy;

import com.algorithm.vm.Flavor;

/**
 * 贪心背包 value 值修改版
 *
 * 1. 还是背包加贪心
 * 2. 改成双重约束，也就是cpu和内存都是约束
 * 3. 每一种flavor的value值改为: (flavor.cpu / 物理机总CPU) + (flavor.mem / 物理机总mem)
 *
 * @date 2018-03-24
 * @author long
 */
public class GreedKnapsackValueModified {

    private int cpu_limit = 0;

    private int mem_limit = 0;

    /**
     * 构造方法，需要传入 物理机的 cpu和 mem上限
     * @param cpu_limit
     * @param mem_limit
     */
    public GreedKnapsackValueModified(int cpu_limit, int mem_limit) {
        this.cpu_limit = cpu_limit;
        this.mem_limit = mem_limit;
    }

    /**
     * 使用贪心背包求解
     * @param needs
     * @return
     */
    public int[] solve(int[] needs) {
        return zeroOnePack(cpu_limit, mem_limit, needs, Flavor.CPU, Flavor.MEM);

    }

    /**
     * 解带两个约束的 01 背包问题
     * @param max1  物理机 cpu
     * @param max2  物理机 mem
     * @param needs 18维，每一种flavor有多少个
     * @param ws1  18维，每一种flavor对应的cpu限制
     * @param ws2  18维，每一种flavor对应的mem限制
     * @return
     */
    private int[] zeroOnePack(int max1, int max2, int[] needs, int[] ws1, int[] ws2) {

        double[] values = new double[Flavor.MAX_RANK+1];
        for ( int flavor = 1; flavor <= Flavor.MAX_RANK; flavor++ ) {
            values[flavor] = Flavor.CPU[flavor] / (double)cpu_limit + Flavor.MEM[flavor] / (double)mem_limit;
        }

        int N = needs.length, W1 = max1, W2 = max2;
        double[][][] dp = new double[N][W1+1][W2+1];
        int[][][] choose = new int[N][W1+1][W2+1];

        for ( int i = 1; i < N; i++ ) {
            for ( int j = 1; j <= W1; j++ ) {
                for ( int k = 1; k <= W2; k++ ) {
                    dp[i][j][k] = dp[i-1][j][k];
                    choose[i][j][k] = 0;
                    for ( int count = 1; count <= needs[i]; count++ ) {
                        if ( j < ws1[i]*count || k < ws2[i]*count ) {
                            break;
                        }
                        double value_tmp = dp[i-1][j-ws1[i]*count][k-ws2[i]*count] + values[i]*count;
                        if ( value_tmp >= dp[i][j][k] ) { // 大于等于和大于有无区别
                            dp[i][j][k] = value_tmp;
                            choose[i][j][k] = count;
                        }
                    }
                }
            }
        }

        int[] res = new int[needs.length];
        int ii = N-1, jj = W1, kk = W2;
        while ( ii > 0 && jj > 0 && kk > 0) {
            if ( choose[ii][jj][kk] != 0 ) { // 注意到这里如果没有保存会出现bug，日
                int save1 = jj, save2 = kk;
                res[ii] = choose[ii][save1][save2];
                jj -= ws1[ii] * choose[ii][save1][save2];
                kk -= ws2[ii] * choose[ii][save1][save2];
            }
            ii--;
        }

        return res;
    }

}

