package com.example.excelcomparatorpro.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

@Data
@ColumnWidth(30)
public class CompareDataItem {

    @ExcelProperty("子项物料编码")
    private String id;
    @ExcelProperty(value = "${title1}")
    private String quantity1;
    @ExcelProperty(value = "${title2}")
    private String quantity2;
    @ExcelIgnore
    private boolean different;

}
