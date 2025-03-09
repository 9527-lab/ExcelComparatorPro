package com.example.excelcomparatorpro.controller;


import com.example.excelcomparatorpro.dto.CompareResult;
import com.example.excelcomparatorpro.param.CompareParam;
import com.example.excelcomparatorpro.service.ExcelCompareService;
import com.example.excelcomparatorpro.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ComparatorController {

    @Autowired
    private ExcelCompareService compareService;


    @PostMapping("/compare")
    public Result compare(@RequestBody CompareParam compareParam) {
        CompareResult result = compareService.compare(compareParam);
        return Result.ok().data("rows", result.getRows())
                          .data("title1", result.getTitle1())
                          .data("title2", result.getTitle2())
                          .data("matchResultFilename", result.getMatchResultFilename());
    }

}
