package com.example.excelcomparatorpro.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CompareResult {

    private List<CompareDataItem> rows = new ArrayList<>();
    private String title1;
    private String title2;
    private String matchResultFilename;

}
