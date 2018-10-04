package com.animal.harness.hodoo.hodooharness.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
public class GPSData {
    private int id;
    private long total_time;
    private double sum;
    private long created;
    public GPSData () {
    }
}
