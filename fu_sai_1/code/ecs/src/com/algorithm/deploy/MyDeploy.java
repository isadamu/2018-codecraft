package com.algorithm.deploy;

import com.algorithm.input.Input;
import com.algorithm.vm.Flavor;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期：2018-04-19
 *
 * 部署函数
 *
 * @author long
 */
public class MyDeploy {

    /**
     * 部署算法
     * @param needs
     * @param input
     * @return
     */
    public List<List<int[]>> deploy(int[] needs, Input input) {

        List<List<int[]>> deploy = null;
        int[] needs_modify = null;
        double max_score = -1;

        int[] needs_copy = null;
        List<List<int[]>> tmp_deploy = null;
        double tmp_score = -1;

        needs_copy = new int[needs.length];
        System.arraycopy(needs, 0, needs_copy, 0, needs.length);
        tmp_deploy = deployByStrangeScore(needs_copy, input);
        tmp_score = helpDeployScore(tmp_deploy, input);
        if ( tmp_score > max_score ) {
            max_score = tmp_score;
            deploy = tmp_deploy;
            needs_modify = needs_copy;
        }

        needs_copy = new int[needs.length];
        System.arraycopy(needs, 0, needs_copy, 0, needs.length);
        tmp_deploy = deployByDsc(needs_copy, input);
        tmp_score = helpDeployScore(tmp_deploy, input);
        if ( tmp_score > max_score ) {
            max_score = tmp_score;
            deploy = tmp_deploy;
            needs_modify = needs_copy;
        }

        for ( int flavor = 1; flavor <= Flavor.MAX_RANK; flavor++ ) {
            needs[flavor] = needs_modify[flavor];
        }

        return deploy;
    }

    /**
     * 先分堆，然后分别部署
     * @param needs
     * @param input
     * @return
     */
    public List<List<int[]>> deployByDsc(int[] needs, Input input) {

        List<List<int[]>> deploy = new ArrayList<>();
        for ( int i = 0; i < input.cpu.length; i++ ) {
            deploy.add(new ArrayList<>());
        }

        int vm_count = 0;
        int[] needsCopy = new int[needs.length];
        for ( int flavor = 1; flavor < needs.length; flavor++ ) {
            vm_count += needs[flavor];
            needsCopy[flavor] = needs[flavor];
        }

        while ( vm_count > 0 ) {
            int[] vms = null;
            int sType = -1;
            double max_score = -1;
            for (int serverType = 0; serverType < input.cpu.length; serverType++) {
                GreedKnapsackValueModified gkcm = new GreedKnapsackValueModified(input.cpu[serverType], input.mem[serverType]);
                int[] put = gkcm.solve(needsCopy);
                double score = helpScore(put, input.cpu[serverType], input.mem[serverType]);
                if ( score >= max_score ) {
                    max_score = score;
                    sType = serverType;
                    vms = put;
                }
            }

            for ( int flavor = 1; flavor < vms.length; flavor++ ) {
                needsCopy[flavor] -= vms[flavor];
                vm_count -= vms[flavor];
            }

            /****** 如果最后一台物理机碎片较大， 将它删除 ******/
            if ( vm_count == 0 && max_score < 1.6) {
                for ( int flavor = 1; flavor < vms.length; flavor++ ) {
                    needs[flavor] -= vms[flavor];
                }
                continue;
            }

            deploy.get(sType).add(vms);
        }

        /************************************************/
        /************** 修改部署结果，尽量塞满 *************/
        DeployModified dm = new DeployModified();
        dm.modify(input, needs, deploy);

        return deploy;
    }


    /**
     * 先分堆，然后分别部署
     * @param needs
     * @param input
     * @return
     */
    public List<List<int[]>> deployByStrangeScore(int[] needs, Input input) {

        List<List<int[]>> deploy = new ArrayList<>();
        for ( int i = 0; i < input.cpu.length; i++ ) {
            deploy.add(new ArrayList<>());
        }

        int vm_count = 0;
        int[] needsCopy = new int[needs.length];
        for ( int flavor = 1; flavor < needs.length; flavor++ ) {
            vm_count += needs[flavor];
            needsCopy[flavor] = needs[flavor];
        }

        while ( vm_count > 0 ) {
            int[] vms = null;
            int sType = -1;
            double max_score = -1;
            int[][] puts = new int[input.cpu.length][];
            for (int serverType = 0; serverType < input.cpu.length; serverType++) {
                GreedKnapsackValueModified gkcm = new GreedKnapsackValueModified(input.cpu[serverType], input.mem[serverType]);
                int[] put = gkcm.solve(needsCopy);
                double score = helpScore(put, input.cpu[serverType], input.mem[serverType]);
                if ( score > max_score ) {
                    max_score = score;
                    sType = serverType;
                    vms = put;
                    puts[sType] = put;
                }
                else if (score == max_score){
                    max_score = score;
                    puts[serverType] = put;
                    sType = helpClassify(sType,serverType,input,needsCopy,put);
                    vms = puts[sType];
                }
            }

            for ( int flavor = 1; flavor < vms.length; flavor++ ) {
                needsCopy[flavor] -= vms[flavor];
                vm_count -= vms[flavor];
            }

            /****** 如果最后一台物理机碎片较大， 将它删除 ******/
            if ( vm_count == 0 && max_score < 1.7) {
                for ( int flavor = 1; flavor < vms.length; flavor++ ) {
                    needs[flavor] -= vms[flavor];
                }
                continue;
            }

            deploy.get(sType).add(vms);
        }

        /************************************************/
        /************** 修改部署结果，尽量塞满 *************/
        DeployModified dm = new DeployModified();
        dm.modify(input, needs, deploy);

        return deploy;
    }


    /**
     * 神奇的记分方式
     * @param sType
     * @param serverType
     * @param input
     * @param needsCopy
     * @param put
     * @return
     */
    private int helpClassify(int sType, int serverType, Input input, int[] needsCopy, int[] put) {
        double proportionBefore = (double)input.cpu[sType] / input.mem[sType];
        double proportionNow = (double)input.cpu[serverType] / input.mem[serverType];

        int[] leftVms = new int[needsCopy.length];
        for (int flavor = 1; flavor < put.length; flavor++){
            leftVms[flavor] = needsCopy[flavor] - put[flavor];
        }

        double cpu_count = 0.0;
        double mem_count = 0.0;
        for (int flavor = 1; flavor < leftVms.length; flavor++){
            cpu_count += leftVms[flavor] * Flavor.CPU[flavor];
            mem_count += leftVms[flavor] * Flavor.MEM[flavor];
        }

        //剩余VM的比例
        double proPortion = cpu_count / mem_count;
        if (Math.abs(proPortion - proportionBefore) >= Math.abs(proPortion - proportionNow)){
            return serverType;
        }
        else {
            return sType;
        }
    }


    /**
     * 辅助统计当前放置的分数
     * @param put
     * @param cpu_limit
     * @param mem_limit
     * @return
     */
    private double helpScore(int[] put, int cpu_limit, int mem_limit) {
        int cpu_count = 0, mem_count = 0;
        for ( int flavor = 1; flavor < put.length; flavor++ ) {
            cpu_count += put[flavor] * Flavor.CPU[flavor];
            mem_count += put[flavor] * Flavor.MEM[flavor];
        }
        return (cpu_count/(double)cpu_limit) + (mem_count/(double)mem_limit); // 乘或者加！！！！
    }


    /**
     * 计算部署得分
     * @param deploy
     * @return
     */
    private double helpDeployScore(List<List<int[]>> deploy, Input input) {
        int server_total_cpu = 0, server_total_mem = 0;
        int vm_total_cpu = 0, vm_total_mem = 0;

        for ( int i = 0; i < deploy.size(); i++ ) {
            List<int[]> server_deploy = deploy.get(i);
            server_total_cpu += server_deploy.size() * input.cpu[i];
            server_total_mem += server_deploy.size() * input.mem[i];
            for ( int[] server : server_deploy ) {
                for ( int flavor = 1; flavor <= Flavor.MAX_RANK; flavor++ ) {
                    vm_total_cpu += server[flavor] * Flavor.CPU[flavor];
                    vm_total_mem += server[flavor] * Flavor.MEM[flavor];
                }
            }
        }

        return (double)vm_total_cpu / server_total_cpu + (double)vm_total_mem / server_total_mem;
    }

}
