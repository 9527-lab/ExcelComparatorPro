package com.example.excelcomparatorpro.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.example.excelcomparatorpro.dto.CompareResult;
import com.example.excelcomparatorpro.model.ExcelModel;
import com.example.excelcomparatorpro.param.CompareParam;
import com.example.excelcomparatorpro.util.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class ComparatorController {

    @Value("${base-config.upload-path}")
    private String uploadPath;


    @PostMapping("/compare")
    public Result compare(@RequestBody CompareParam compareParam) {
        File file1 = new File(uploadPath, compareParam.getFile1Md5());
        if (!file1.exists()) {
            return Result.error().message("文件1不存在");
        }
        File file2 = new File(uploadPath, compareParam.getFile2Md5());
        if (!file2.exists()) {
            return Result.error().message("文件2不存在");
        }
        ExcelReaderSheetBuilder builder1 = null;
        if (StringUtils.hasLength(compareParam.getFile1SheetName())){
            builder1 = EasyExcel.read(file1).sheet(compareParam.getFile1SheetName());
        }else {
            builder1 = EasyExcel.read(file1).sheet(0);
        }
        ExcelReaderSheetBuilder builder2 = null;
        if (StringUtils.hasLength(compareParam.getFile2SheetName())){
            builder2 = EasyExcel.read(file2).sheet(compareParam.getFile2SheetName());
        }else {
            builder2 = EasyExcel.read(file2).sheet(0);
        }
        List<ExcelModel> dataList1 = builder1.doReadSync();
        List<ExcelModel> dataList2 = builder2.doReadSync();
        dataList1 = dataList1.stream().filter(item -> compareParam.getLevels().contains(item.getLevel())).collect(Collectors.toList());
        dataList2 = dataList2.stream().filter(item -> compareParam.getLevels().contains(item.getLevel())).collect(Collectors.toList());
        Set<String> ids = new HashSet<>();
        ids.addAll(dataList1.stream().map(ExcelModel::getId).collect(Collectors.toList()));
        ids.addAll(dataList2.stream().map(ExcelModel::getId).collect(Collectors.toList()));
        List<CompareResult> results = new ArrayList<>();
        for (String id : ids) {
            List<String> filterResult1 = dataList1.stream().filter(item -> item.getId().equals(id)).map(item -> String.valueOf(item.getQuantity())).collect(Collectors.toList());
            List<String> filterResult2 = dataList2.stream().filter(item -> item.getId().equals(id)).map(item -> String.valueOf(item.getQuantity())).collect(Collectors.toList());
            // 判断两个列表是否相等
            boolean areEqual = CollectionUtil.isEqualList(filterResult1, filterResult2);
            if (!areEqual){
                CompareResult compareResultItem = new CompareResult();
                compareResultItem.setId(id);
                if (!CollectionUtils.isEmpty(filterResult1)){
                    compareResultItem.setQuantity1(String.join(",", filterResult1));
                }else {
                    compareResultItem.setQuantity1("null");
                }
                if (!CollectionUtils.isEmpty(filterResult2)){
                    compareResultItem.setQuantity2(String.join(",", filterResult2));
                }else {
                    compareResultItem.setQuantity2("0");
                }
                results.add(compareResultItem);
            }
        }
        if (!CollectionUtils.isEmpty(results)){
            String matchResultFilename = "match_result_" + MD5.create().digestHex(compareParam.getFile1Md5(), compareParam.getFile2Md5()) + StrUtil.DOT + "xlsx";
            EasyExcel.write(uploadPath + File.separator + matchResultFilename, CompareResult.class)
                    .autoCloseStream(false).sheet("匹配结果").doWrite(results);
            return Result.ok().data("rows", results).data("matchResultFilename", matchResultFilename);
        }
        return Result.ok().data("rows", results);
    }

}
