package com.tdp2.eukanuber.model;

import java.io.Serializable;

public class Rating implements Serializable {
    private String sum;
    private String count;

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}

