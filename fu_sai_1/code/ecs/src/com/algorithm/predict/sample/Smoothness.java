package com.algorithm.predict.sample;

import com.algorithm.data.DataTransform;
import com.algorithm.input.Input;
import com.algorithm.util.DateUtil;
import com.algorithm.vm.Flavor;

import java.util.Arrays;

/**
 * 创建日期：2018-04-26
 *
 * 平滑函数
 *
 * @author long
 */
public class Smoothness {

    /**
     * 中值滤波
     * @param vms
     * @return
     */
    public static int[] mediaSmooth(int[] vms) {

        int[] smooth = new int[vms.length];

        smooth[0] = vms[0];
        smooth[vms.length-1] = vms[vms.length-1];

        for ( int i = 1; i < vms.length-1; i++ ) {
                int[] three = new int[3];
                three[0] = vms[i-1];
                three[1] = vms[i];
                three[2] = vms[i+1];
                Arrays.sort(three);
                smooth[i] = three[1];
        }

        return smooth;
    }


    /**
     * 将最大值换成中值
     * @param vms
     * @return
     */
    public static void maxReplaceByWeek(int[][] vms) {

        for ( int end = vms.length-1; end >= 0; end -= DataTransform.WEEK_LEN) {
            int begin = end - DataTransform.WEEK_LEN + 1;
            if ( begin < 0 ) {
                begin = 0;
            }
            for ( int flavor = 1; flavor <= Flavor.MAX_RANK; flavor++ ) {
                int arg = argmax(flavor, begin, end, vms);
                vms[arg][flavor] = findmaxTwo(flavor, begin, end, vms);
//                int media = media(flavor, begin, end, vms);
//                if ( media > 0 ) {
//                    vms[arg][flavor] = media;
//                } else {
//                    vms[arg][flavor] = average(flavor, begin, end, vms);
//                }
            }
        }

    }

    /**
     * 求取一段时间里面某种flavor的均值
     * @param flavor
     * @param begin
     * @param end
     * @param vms
     * @return
     */
    private static int average(int flavor, int begin, int end, int[][] vms) {
        int total = 0;
        for ( int i = begin; i <= end; i++ ) {
            total += vms[i][flavor];
        }
        return (int)Math.floor(((double)total)/(end-begin+1));
    }

    /**
     * 求取某段时间里面某种flavor的中值
     * @param flavor
     * @param begin
     * @param end
     * @param vms
     * @return
     */
    private static int media(int flavor, int begin, int end, int[][] vms) {
        int[] copy = new int[end-begin+1];
        for ( int i = begin, idx = 0; i <= end; i++, idx++ ) {
            copy[idx] = vms[i][flavor];
        }
        Arrays.sort(copy);
        return copy[copy.length/2];
    }

    /**
     * 求取某段时间里面某种flavor的最大值（idx）
     * @param flavor
     * @param begin
     * @param end
     * @param vms
     * @return
     */
    private static int argmax(int flavor, int begin, int end, int[][] vms) {
        int arg = -1;
        int max = Integer.MIN_VALUE;
        for ( int i = begin; i <= end; i++) {
            if ( vms[i][flavor] > max ) {
                max = vms[i][flavor];
                arg = i;
            }
        }
        return arg;
    }

    /**
     * 求取某段时间里面某种flavor的第二大值
     * @param flavor
     * @param begin
     * @param end
     * @param vms
     * @return
     */
    private static int findmaxTwo(int flavor, int begin, int end, int[][] vms) {
        int[] copy = new int[end-begin+1];
        for ( int i = begin, idx = 0; i <= end; i++, idx++ ) {
            copy[idx] = vms[i][flavor];
        }
        Arrays.sort(copy);
        return copy[copy.length-1];
    }


}
