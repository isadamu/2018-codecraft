package com.algorithm.util;

import com.algorithm.input.Input;

import java.util.ArrayList;
import java.util.List;

/**
 * 将最后的结果转成能够写出的字符串
 *
 * @author long
 */
public class ResToString {

    public static String[] covert(int[] needs, List<int[]> deploy, Input input) {
        List<String> out = new ArrayList<>();
        int count = 0;
        for ( int i = 1; i < needs.length; i++ ) {
            if ( !input.vmsCanUse[i] ) {
                continue;
            }
            out.add("flavor" + i + " " + needs[i]);
            count += needs[i];
        }
        out.add(0, count + "");

        out.add("");

        out.add(deploy.size() + "");

        for ( int i = 0; i < deploy.size(); i++ ) {
            int[] single = deploy.get(i);
            String str = "";
            str += (i + 1);
            for ( int j = 1; j < single.length; j++ ) {
                if ( single[j] == 0 ) {
                    continue;
                }
                str += " flavor" + j + " " + single[j];
            }
            out.add(str);
        }
        return out.toArray(new String[0]);
    }
}
