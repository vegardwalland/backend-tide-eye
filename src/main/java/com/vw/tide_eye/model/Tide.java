package com.vw.tide_eye.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class Tide implements Serializable {

    @Id
    private String harbor;
    private String lastUpdated;
    private ArrayList<SurgeData> surgeData;
}

