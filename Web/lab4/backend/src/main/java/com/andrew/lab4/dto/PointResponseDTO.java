package com.andrew.lab4.dto;

import com.andrew.lab4.model.Point;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PointResponseDTO {
    private double x;
    private double y;
    private double r;
    private boolean hit;
    private String curTime;
    private long execTime;

    public PointResponseDTO (@NotNull Point point) {
        this.x = point.getX();
        this.y = point.getY();
        this.r = point.getR();
        this.hit = point.isHit();
        this.curTime = point.getCurTime();
        this.execTime = point.getExecTime();
    }

    public Point toPoint() {
        return Point.builder()
            .x(x)
            .y(y)
            .r(r)
            .hit(hit)
            .curTime(curTime)
            .execTime(execTime)
            .build();
    }
}
