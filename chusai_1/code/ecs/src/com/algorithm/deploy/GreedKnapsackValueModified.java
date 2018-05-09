package com.algorithm.deploy;


import com.algorithm.input.Input;
import com.algorithm.util.Flavor;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * 升序解
     * @param needs
     * @param input
     * @return
     */
    public List<int[]> solveAsc(int[] needs, Input input) {
        return solve(needs, input, true);
    }

    /**
     * 降序解
     * @param needs
     * @param input
     * @return
     */
    public List<int[]> solveDsc(int[] needs, Input input) {
        return solve(needs, input, false);
    }


    /**
     * 使用贪心背包求解
     * @param needs
     * @param input
     * @param asc 升序还是降序
     * @return
     */
    private List<int[]> solve(int[] needs, Input input, boolean asc) {
        List<int[]> res = new ArrayList<>();

        int totalCount = 0;
        for ( int need : needs ) {
            totalCount += need;
        }

        double[] valueMap = new double[needs.length];
        for ( int i = 1; i < valueMap.length; i++ ) {
            valueMap[i] = Flavor.CPU[i] / (double)input.cpu + Flavor.MEM[i] / (double)input.mem;
        }

        while ( totalCount > 0 ) {
            int[] rank = new int[totalCount];
            double[] values = new double[totalCount];
            int idx = 0;
            if ( asc ) {
                for (int i = 0; i < needs.length; i++) {
                    for (int j = 0; j < needs[i]; j++) {
                        rank[idx] = i;
                        values[idx] = valueMap[i];
                        idx++;
                    }
                }
            } else {
                for (int i = needs.length - 1; i >= 0; i--) {
                    for (int j = 0; j < needs[i]; j++) {
                        rank[idx] = i;
                        values[idx] = valueMap[i];
                        idx++;
                    }
                }
            }
            List<Integer> oneChoose = zeroOnePack(input, rank, values);
            int[] vms = new int[needs.length];
            for ( int flavor : oneChoose ) {
                needs[flavor]--;
                vms[flavor]++;
                totalCount--;
            }
            res.add(vms);
        }
        return res;
    }

    /**
     * 解带两个约束的 01 背包问题
     * @param rank
     * @param values
     * @return
     */
    private List<Integer> zeroOnePack(Input input, int[] rank, double[] values) {

        int N = rank.length, W1 = input.cpu, W2 = input.mem;
        double[][][] dp = new double[N+1][W1+1][W2+1];
        int[][][] choose = new int[N+1][W1+1][W2+2];

        for ( int i = 1; i <= N; i++ ) {
            int flavor = rank[i-1];
            for ( int j = 1; j <= W1; j++ ) {
                for ( int k = 1; k <= W2; k++ ) {
                    if ( j < Flavor.CPU[flavor] ) {
                        System.arraycopy(dp[i-1][j], 1, dp[i][j], 1, W2);
                        System.arraycopy(choose[i-1][j], 1, choose[i][j], 1, W2);
                        k = W2;
                        continue;
                    }
                    if ( k < Flavor.MEM[flavor] ) {
                        System.arraycopy(dp[i-1][j], 1, dp[i][j], 1, Flavor.MEM[flavor]-1);
                        System.arraycopy(choose[i-1][j], 1, choose[i][j], 1, Flavor.MEM[flavor]-1);
                        k = Flavor.MEM[flavor] - 1;
                        continue;
                    }
                    double value_tmp = dp[i-1][j - Flavor.CPU[flavor]][k - Flavor.MEM[flavor]] + values[i-1];

                    if ( dp[i-1][j][k] > value_tmp ) { // 大于等于和大于有无区别
                        dp[i][j][k] = dp[i-1][j][k];
                        choose[i][j][k] = choose[i-1][j][k];
                    } else {
                        dp[i][j][k] = value_tmp;
                        choose[i][j][k] = i;
                    }
                }
            }
        }

        List<Integer> res = new ArrayList<>();
        int ii = N, jj = W1, kk = W2;
        while ( ii > 0 && jj > 0 && kk > 0) {
            if ( choose[ii][jj][kk] == ii ) {
                int flavor = rank[ii-1];
                res.add(flavor);
                jj -= Flavor.CPU[flavor];
                kk -= Flavor.MEM[flavor];
            }
            ii--;
        }

        return res;
    }

}

