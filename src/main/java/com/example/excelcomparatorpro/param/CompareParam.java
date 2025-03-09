package com.example.excelcomparatorpro.param;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class CompareParam {

    private String file1Md5;
    private String file2Md5;
    private String file1SheetName;
    private String file2SheetName;
    private List<Integer> levels = new ArrayList<>();

}
