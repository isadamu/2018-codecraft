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

    public static String[] covert(int[] needs, List<List<int[]>> deploy, Input input) {
        List<String> out = new ArrayList<>();
        int count = 0;
        for ( int i = 1; i < needs.length; i++ ) {
            if ( !input.flavorToPredict[i] ) {
                continue;
            }
            out.add("flavor" + i + " " + needs[i]);
            count += needs[i];
        }
        out.add(0, count + "");

        for (int serverType = 0; serverType < deploy.size(); serverType++) {
            if ( deploy.get(serverType).isEmpty() ) {
                continue;
            }

            List<int[]> servers = deploy.get(serverType);
            out.add("");
            out.add(input.serverName[serverType] + " " + servers.size());

            for (int i = 0; i < servers.size(); i++) {
                int[] single = servers.get(i);
                StringBuilder sb = new StringBuilder();
                sb.append(input.serverName[serverType]).append('-').append(i+1);
                for (int j = 1; j < single.length; j++) {
                    if (single[j] == 0) {
                        continue;
                    }
                    sb.append(" flavor").append(j).append(' ').append(single[j]);
                }
                out.add(sb.toString());
            }
        }
        return out.toArray(new String[0]);
    }
}
