package com.algorithm.deploy;


import com.algorithm.input.Input;
import com.algorithm.util.Flavor;

import java.util.ArrayList;
import java.util.List;

/**
 * First Fit 下次适应算法
 *
 * @date 2018-03-23
 * @author long
 */
public class FirstFit {

    /**
     * First Fit 升序版
     * @param needs
     * @param input
     * @return
     */
    public List<int[]> solveAsc(int[] needs, Input input) {
        List<int[]> res = new ArrayList<>();
        List<Integer> needsTotal = new ArrayList<>();
        for ( int i = 1; i < needs.length; i++ ) {
            for ( int j = 0; j < needs[i]; j++ ) {
                needsTotal.add(i);
            }
        }

        while ( !needsTotal.isEmpty() ) {
            int[] src = new int[2];
            src[0] = input.cpu;
            src[1] = input.mem;
            int[] vms = new int[needs.length];
            boolean deploy = true;
            while ( deploy ) {
                int idx = 0;
                while ( idx < needsTotal.size() ) {
                    int flavor = needsTotal.get(idx);
                    if ( src[0] >= Flavor.CPU[flavor] && src[1] >= Flavor.MEM[flavor] ) {
                        src[0] -= Flavor.CPU[flavor];
                        src[1] -= Flavor.MEM[flavor];
                        break;
                    }
                    idx++;
                }
                if ( idx == needsTotal.size() ) {
                    deploy = false;
                } else {
                    vms[needsTotal.get(idx)]++;
                    needsTotal.remove(idx);
                }
            }
            res.add(vms);
        }
        return res;
    }

    /**
     * First Fit 降序版
     * @param needs
     * @param input
     * @return
     */
    public List<int[]> solveDsc(int[] needs, Input input) {
        List<int[]> res = new ArrayList<>();
        List<Integer> needsTotal = new ArrayList<>();
        for ( int i = 1; i < needs.length; i++ ) {
            for ( int j = 0; j < needs[i]; j++ ) {
                needsTotal.add(i);
            }
        }

        while ( !needsTotal.isEmpty() ) {
            int[] src = new int[2];
            src[0] = input.cpu;
            src[1] = input.mem;
            int[] vms = new int[needs.length];
            boolean deploy = true;
            while ( deploy ) {
                int idx = needsTotal.size() - 1;
                while ( idx >= 0 ) {
                    int flavor = needsTotal.get(idx);
                    if ( src[0] >= Flavor.CPU[flavor] && src[1] >= Flavor.MEM[flavor] ) {
                        src[0] -= Flavor.CPU[flavor];
                        src[1] -= Flavor.MEM[flavor];
                        break;
                    }
                    idx--;
                }
                if ( idx == -1 ) {
                    deploy = false;
                } else {
                    vms[needsTotal.get(idx)]++;
                    needsTotal.remove(idx);
                }
            }
            res.add(vms);
        }
        return res;
    }

}
