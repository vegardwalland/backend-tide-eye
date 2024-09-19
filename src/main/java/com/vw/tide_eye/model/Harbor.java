package com.vw.tide_eye.model;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Harbor implements Serializable {
    private String name;
    private double latitude;
    private double longitude;
}
