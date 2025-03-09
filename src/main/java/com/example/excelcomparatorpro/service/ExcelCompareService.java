package com.example.excelcomparatorpro.service;

import com.example.excelcomparatorpro.dto.CompareResult;
import com.example.excelcomparatorpro.param.CompareParam;


public interface ExcelCompareService {
    CompareResult compare(CompareParam compareParam);

}
