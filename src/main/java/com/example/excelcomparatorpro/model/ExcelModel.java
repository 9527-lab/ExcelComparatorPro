package com.example.excelcomparatorpro.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.example.excelcomparatorpro.converter.NumberConverter;
import lombok.Data;

@Data
public class ExcelModel {

    /**
     * BOM层级
     */
    @ExcelProperty(value = "BOM层级", converter = NumberConverter.class)
    private Integer level;

    /**
     * 子项物料编码
     */
    @ExcelProperty("子项物料编码")
    private String id;

    /**
     * 实际数量
     */
    @ExcelProperty(value = "实际数量", converter = NumberConverter.class)
    private Integer quantity;

}
