package com.algorithm.data;

import com.algorithm.predict.sample.Smoothness;
import com.algorithm.util.DateUtil;
import com.algorithm.vm.Flavor;
import com.algorithm.vm.VM;

import java.util.List;

/**
 * 创建日期：2018-04-23
 *
 * 提供数据转化的方法
 *
 * @author long
 */
public class DataTransform {

    public static final int WEEK_LEN = 7;

    /**
     * 统计每天的 VM 数量
     * @param datas
     * @return
     */
    public static int[][] toDayArray(List<VM> datas) {
        int data_len_day = DateUtil.diffByDay(datas.get(0).date, datas.get(datas.size()-1).date) + 1;

        int[][] vms_day = new int[data_len_day][Flavor.MAX_RANK+1];

        for ( VM vm : datas ) {
            if ( vm.flavor <= 0 || vm.flavor > Flavor.MAX_RANK ) {
                continue;
            }
            vms_day[DateUtil.diffByDay(datas.get(0).date, vm.date)][vm.flavor]++;
        }

        Smoothness.maxReplaceByWeek(vms_day);   // 最大值衰减！！！！！

        return vms_day;
    }

    /**
     * 统计每 7天的 VM 数量，滑动步长为 1 天
     * @param datas
     * @return
     */
    public static int[][] toDayWeekArray(List<VM> datas) {

        int[][] vms_day = toDayArray(datas);

        int[][] vms_day_week = new int[vms_day.length-WEEK_LEN+1][Flavor.MAX_RANK+1];

        int[] sum_week = new int[Flavor.MAX_RANK+1];
        for ( int i = 0; i < vms_day.length; i++ ) {
            for ( int flavor = 1; flavor <= Flavor.MAX_RANK; flavor++ ) {
                sum_week[flavor] += vms_day[i][flavor];
            }
            if ( i >= WEEK_LEN ) {
                for ( int flavor = 1; flavor <= Flavor.MAX_RANK; flavor++ ) {
                    sum_week[flavor] -= vms_day[i-WEEK_LEN][flavor];
                }
            }
            if ( i + 1 >= WEEK_LEN ) {
                for (int flavor = 1; flavor <= Flavor.MAX_RANK; flavor++) {
                    vms_day_week[i-WEEK_LEN+1][flavor] = sum_week[flavor];
                }
            }
        }

        return vms_day_week;
    }

    /**
     * 取到按照每一天的求和结果。
     * @param datas
     * @return
     */
    public static int[] toDayCumsum(List<VM> datas) {
        int[][] vms_day = toDayArray(datas);

        for ( int i = 1; i < vms_day.length; i++ ) {
            for ( int flavor = 1; flavor <= Flavor.MAX_RANK; flavor++ ) {
                vms_day[i][flavor] += vms_day[i-1][flavor];
            }
        }

        int[] vms_cumsum = new int[vms_day.length];
        for ( int i = 0; i < vms_day.length; i++ ) {
            for ( int flavor = 1; flavor <= Flavor.MAX_RANK; flavor++ ) {
                vms_cumsum[i] += vms_day[i][flavor];
            }
        }

        return vms_cumsum;
    }

    /**
     * 辅助划分出每一种flavor在一段时间里面占总量的比例
     * @param trainData
     * @return
     */
    public static double[] helpPrecent(List<VM> trainData, int percent_len) {

        int data_len_day = DateUtil.diffByDay(trainData.get(0).date, trainData.get(trainData.size()-1).date) + 1;

        int use_to_percent = data_len_day < percent_len ? data_len_day : percent_len;

        int[][] vms_day = toDayArray(trainData);

        for ( int i = 1; i < vms_day.length; i++ ) {
            for ( int flavor = 1; flavor <= Flavor.MAX_RANK; flavor++ ) {
                vms_day[i][flavor] += vms_day[i-1][flavor];
            }
        }

        // 统计每天的 VM 数量
        int[] flavor_counts = new int[Flavor.MAX_RANK+1];
        double sum = 0;
        for ( int flavor = 1; flavor <= Flavor.MAX_RANK; flavor++ ) {
            flavor_counts[flavor] = vms_day[vms_day.length-1][flavor] - vms_day[vms_day.length-use_to_percent][flavor];
            sum += flavor_counts[flavor];
        }


        double[] percent = new double[Flavor.MAX_RANK+1];
        for ( int flavor = 1; flavor <= Flavor.MAX_RANK; flavor++ ) {
            percent[flavor] = flavor_counts[flavor] / sum;
        }

        return percent;
    }

}
