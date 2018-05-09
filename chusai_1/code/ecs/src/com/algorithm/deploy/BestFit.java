package com.algorithm.deploy;


import com.algorithm.input.Input;
import com.algorithm.util.Flavor;

import java.util.ArrayList;
import java.util.List;

/**
 * Best Fit 最佳适应算法
 *
 * ************************************
 * 如何才是最适应？想法：
 *
 *    首先，一台物理机，想让分满，那么分配的值必然要与物理机的 CPU 以及 MEN 大小匹配。
 *
 *    那么将适应定义为最接近这个比例的分配方案。
 *
 *    定义比例： CPU / MEM
 *
 *    定义分数： -1 * abs(当前分配比例 - 物理机比例)
 *
 *    这样就是分数越大越好。
 * ************************************
 *    操作步骤：
 *
 *    1. 遍历当前所有打开的 物理机，尝试放入 虚拟机，并计算比例。
 *    2. 再新打开一台物理机，尝试放入 虚拟机，并计算比例。
 *    3. 遍历上面的分数，取分数最大的第一个方案来放置这个 虚拟机。
 *
 *    4. (新加) 计算一个最少使用的总物理机数量，在开启的物理机数量等于它时，转入尽量不开启新物理机模式。
 *
 *    复杂度：O(mn)，m为虚拟机个数，n为最后定下的物理机个数。
 * ************************************
 *
 * @data 2018-03-23
 * @author long
 */
public class BestFit {

    private int limit = Integer.MAX_VALUE;

    /**
     * 定一个 Server内部类，方便计算
     */
    private class Server {

        public int[] vms;

        public int cpuUse;

        public int memUse;

        public Server() {
            this.vms = new int[Flavor.MAX_RANK+1];
            this.cpuUse = 0;
            this.memUse = 0;
        }

    }

    /**
     * Best Fit 升序
     * @param needs
     * @param input
     * @return
     */
    public List<int[]> solveAsc(int[] needs, Input input) {
        return solve(needs, input, true);
    }

    /**
     * Best Fit 降序
     * @param needs
     * @param input
     * @return
     */
    public List<int[]> solveDsc(int[] needs, Input input) {
        return solve(needs, input, false);
    }

    /**
     * 按照适应性原则求解
     * @param needs
     * @param input
     * @param asc
     * @return
     */
    private List<int[]> solve(int[] needs, Input input, boolean asc) {
        initLimit(needs, input);
        List<Server> open = new ArrayList<>();
        if ( asc ) {
            for (int i = 0; i < needs.length; i++) {
                for (int j = 0; j < needs[i]; j++) {
                    putIn(open, i, input);
                }
            }
        } else {
            for (int i = needs.length-1; i >= 0; i--) {
                for (int j = 0; j < needs[i]; j++) {
                    putIn(open, i, input);
                }
            }
        }
        List<int[]> res = new ArrayList<>();
        for ( Server server : open ) {
            res.add(server.vms);
        }
        return res;
    }

    /**
     * 按照适应性原则放入一台 vm
     * @param open
     * @param flavor
     * @param input
     */
    private void putIn(List<Server> open, int flavor, Input input) {
        Server bestServer = null;
        double bestScore = Double.NEGATIVE_INFINITY;
        for ( Server server : open ) {
            if ( server.cpuUse + Flavor.CPU[flavor] > input.cpu
                    || server.memUse + Flavor.MEM[flavor] > input.mem ) {
                continue;
            }
            double score = computeScore(server.cpuUse + Flavor.CPU[flavor],
                    server.memUse + Flavor.MEM[flavor], input);
            if ( score > bestScore ) {
                bestServer = server;
                bestScore = score;
            }
        }
        double scoreNew = computeScore(Flavor.CPU[flavor], Flavor.MEM[flavor], input);
        if ( (bestScore == Double.NEGATIVE_INFINITY) || (scoreNew > bestScore && open.size() < limit )) {
            Server server = new Server();
            server.vms[flavor]++;
            server.cpuUse = Flavor.CPU[flavor];
            server.memUse = Flavor.MEM[flavor];
            open.add(server);
        } else {
            bestServer.vms[flavor]++;
            bestServer.cpuUse += Flavor.CPU[flavor];
            bestServer.memUse += Flavor.MEM[flavor];
        }
    }

    /**
     * 计算分数
     * @param cpu
     * @param mem
     * @param input
     * @return
     */
    private double computeScore(int cpu, int mem, Input input) {
        double score1 = cpu / (double)mem;
        double score2 = input.cpu / (double)input.mem;
        return -1 * Math.abs(score1 - score2);
    }

    private void initLimit(int[] needs, Input input) {
        int cpu = 0, mem = 0;
        for ( int i = 1; i < needs.length; i++ ) {
            cpu += Flavor.CPU[i] * needs[i];
            mem += Flavor.MEM[i] * needs[i];
        }

        this.limit = Math.ceil(cpu / input.cpu) > Math.ceil(mem / input.mem) ?
                (int)Math.ceil(cpu / input.cpu) : (int)Math.ceil(mem / input.mem);

    }

}
