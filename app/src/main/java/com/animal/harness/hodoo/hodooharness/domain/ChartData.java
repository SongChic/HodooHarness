package com.animal.harness.hodoo.hodooharness.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class ChartData {
    private float x;
    private float y;
}
