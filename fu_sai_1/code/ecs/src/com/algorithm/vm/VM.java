package com.algorithm.vm;

import com.algorithm.util.DateUtil;

import java.util.Calendar;

public class VM {

    public String id;

    public int flavor;

    public Calendar date;

    public VM() {}

    public VM(String id, String flavor, String date) {
        this.id = id;
        this.flavor = Integer.parseInt(flavor.substring(6));
        this.date = DateUtil.stringToCalender(date);
    }

    public VM(String[] contents) {
        this(contents[0], contents[1], contents[2]);
    }

    @Override
    public String toString() {
        return "VM{" +
                "id='" + id + '\'' +
                ", flavor=" + flavor +
                ", date=" + date +
                '}';
    }

}
