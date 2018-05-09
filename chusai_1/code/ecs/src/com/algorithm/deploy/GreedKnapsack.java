package com.algorithm.deploy;

import com.algorithm.input.Input;
import com.algorithm.util.Flavor;

import java.util.ArrayList;
import java.util.List;

/**
 * 题目的部署是十分典型的背包问题
 * 使用背包问题的相关算法来求解
 *
 * @date 2018-3-17 09:33:36
 * @author long
 */
public class GreedKnapsack {

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
     * @return
     */
    private List<int[]> solve(int[] needs, Input input, boolean asc) {
        List<int[]> res = new ArrayList<>();

        int totalCount = 0;
        for ( int need : needs ) {
            totalCount += need;
        }
        while ( totalCount > 0 ) {
            int[] rank = new int[totalCount], values = new int[totalCount], sizes = new int[totalCount];
            int idx = 0;
            if ( asc ) {
                for (int i = 0; i < needs.length; i++) {
                    for (int j = 0; j < needs[i]; j++) {
                        rank[idx] = i;
                        values[idx] = Flavor.CPU[i];
                        sizes[idx] = Flavor.MEM[i];
                        idx++;
                    }
                }
            } else {
                for (int i = needs.length - 1; i >= 0; i--) {
                    for (int j = 0; j < needs[i]; j++) {
                        rank[idx] = i;
                        values[idx] = Flavor.CPU[i];
                        sizes[idx] = Flavor.MEM[i];
                        idx++;
                    }
                }
            }
            int valueMax = 0, packVolumn = 0;
            if (input.target == 0) {
                valueMax = input.cpu;
                packVolumn = input.mem;
            } else {
                valueMax = input.mem;
                packVolumn = input.cpu;
                int[] tmp = values;
                values = sizes;
                sizes = tmp;
            }
            List<Integer> oneChoose = zeroOnePack(valueMax, packVolumn, rank, values, sizes);
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
     *  解 01背包问题
     * @param valueMax 最大价值上限
     * @param packVolumn 背包大小
     * @param rank 哪一款 VM
     * @param values 价值大小
     * @param sizes 重量大小
     * @return 所选择的物品
     */
    private List<Integer> zeroOnePack(int valueMax,
                                             int packVolumn, int[] rank, int[] values, int[] sizes) {

        int N = rank.length, W = packVolumn;
        int[][] dp = new int[N+1][W+1];
        int[][] choose = new int[N+1][W+1];
        for ( int i = 1; i <= N; i++ ) {
            for ( int j = 1; j <= W; j++ ) {
                if ( j < sizes[i-1] ) {
                    dp[i][j] = dp[i-1][j];
                    choose[i][j] = choose[i-1][j];
                    continue;
                }
                int value_tmp = dp[i-1][j - sizes[i-1]] + values[i-1];
                if ( value_tmp > valueMax || dp[i-1][j] > value_tmp ) { // 大于等于和大于有无区别
                    dp[i][j] = dp[i-1][j];
                    choose[i][j] = choose[i-1][j];
                } else {
                    dp[i][j] = value_tmp;
                    choose[i][j] = i;
                }
            }
        }

        List<Integer> res = new ArrayList<>();
        int ii = N, jj = W;
        while ( ii > 0 && jj > 0 ) {
            if ( choose[ii][jj] == ii ) {
                res.add(rank[ii-1]);
                jj -= sizes[ii-1];
            }
            ii--;
        }

        return res;
    }

}
