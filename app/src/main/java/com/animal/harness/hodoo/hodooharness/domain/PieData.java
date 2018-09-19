package com.animal.harness.hodoo.hodooharness.domain;

import lombok.Data;

@Data
public class PieData {
    private float percent;
    private float start;
    private float end;
    private float animEnd;
    public PieData(){}
    public PieData ( float percent ) {
        this.percent = percent;
    }
    public PieData ( int start, int end ) {

    }

}
