package com.vw.tide_eye.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class SurgeData implements Serializable {
    @Id
    private Integer year;
    private Integer month;
    private Integer day;
    private Integer hour;
    //private Integer prognosis;
    private Float surge;
    private Float tide;
    private Float total;
    /*private Float surgePercentage0;
    private Float surgePercentage25;
    private Float surgePercentage50;
    private Float surgePercentage75;
    private Float surgePercentage100;*/

}
