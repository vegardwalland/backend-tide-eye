package com.vw.tide_eye.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class Tide implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String harbor;
    private String lastUpdated;
    private ArrayList<SurgeData> surgeData;
}

