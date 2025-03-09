package com.example.excelcomparatorpro.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

@Data
@ColumnWidth(20)
public class CompareResult {

    @ExcelProperty("子项物料编码")
    private String id;
    @ExcelProperty(value = "A表实际用量")
    private String quantity1;
    @ExcelProperty(value = "B表实际用量")
    private String quantity2;
    @ExcelIgnore
    private boolean different;

}
