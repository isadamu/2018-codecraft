package com.algorithm.util;

/**
 * 每一个 flavor 的规格
 *
 *  flavor1 1 1024 1
    flavor2 1 2048 2
    flavor3 1 4096 4
    flavor4 2 2048 2
    flavor5 2 4096 4
    flavor6 2 8192 8
    flavor7 4 4096 4
    flavor8 4 8192 8
    flavor9 4 16384 16
    flavor10 8 8192 8
    flavor11 8 16384 16
    flavor12 8 32768 32
    flavor13 16 16384 16
    flavor14 16 32768 32
    flavor15 16 65536 64
 */
public class Flavor {

    public static final int MAX_RANK = 15;

    public static final int[] CPU = {-1,1,1,1,2,2,2,4,4,4,8,8,8,16,16,16};

    public static final int[] MEM = {-1,1,2,4,2,4,8,4,8,16,8,16,32,16,32,64};
}
