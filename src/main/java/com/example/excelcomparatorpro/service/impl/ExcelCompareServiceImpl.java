package com.example.excelcomparatorpro.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.builder.ExcelReaderSheetBuilder;
import com.example.excelcomparatorpro.dto.CompareDataItem;
import com.example.excelcomparatorpro.dto.CompareResult;
import com.example.excelcomparatorpro.exception.ServiceException;
import com.example.excelcomparatorpro.model.ExcelModel;
import com.example.excelcomparatorpro.param.CompareParam;
import com.example.excelcomparatorpro.service.ExcelCompareService;
import com.example.excelcomparatorpro.util.CustomerTitleHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExcelCompareServiceImpl implements ExcelCompareService {

    @Value("${base-config.upload-path}")
    private String uploadPath;

    @Override
    public CompareResult compare(CompareParam compareParam) {
        File file1 = new File(uploadPath, compareParam.getFile1Md5());
        if (!file1.exists()) {
            throw new ServiceException("文件1不存在");
        }
        File file2 = new File(uploadPath, compareParam.getFile2Md5());
        if (!file2.exists()) {
            throw new ServiceException("文件2不存在");
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
        List<ExcelModel> dataList1 = builder1.head(ExcelModel.class).doReadSync();
        List<ExcelModel> dataList2 = builder2.head(ExcelModel.class).doReadSync();
        dataList1 = dataList1.stream().filter(item -> item.getLevel() != null).collect(Collectors.toList());
        dataList2 = dataList2.stream().filter(item -> item.getLevel() != null).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(dataList1)){
            throw new ServiceException("文件1为非BOM表或数据为空");
        }
        if (CollectionUtils.isEmpty(dataList2)){
            throw new ServiceException("文件2为非BOM表或数据为空");
        }
        String title1 = "A表实际用量";
        String title2 = "B表实际用量";
        Optional<ExcelModel> optionalExcelModel1 = dataList1.stream().filter(item -> item.getLevel() == 0).findFirst();
        Optional<ExcelModel> optionalExcelModel2 = dataList2.stream().filter(item -> item.getLevel() == 0).findFirst();
        if (optionalExcelModel1.isPresent()){
            title1 = optionalExcelModel1.get().getId();
        }
        if (optionalExcelModel2.isPresent()){
            title2 = optionalExcelModel2.get().getId();
        }
        dataList1 = dataList1.stream().filter(item -> compareParam.getLevels().contains(item.getLevel())).collect(Collectors.toList());
        dataList2 = dataList2.stream().filter(item -> compareParam.getLevels().contains(item.getLevel())).collect(Collectors.toList());
        Set<String> ids = new HashSet<>();
        ids.addAll(dataList1.stream().map(ExcelModel::getId).collect(Collectors.toList()));
        ids.addAll(dataList2.stream().map(ExcelModel::getId).collect(Collectors.toList()));
        List<CompareDataItem> results = new ArrayList<>();
        for (String id : ids) {
            List<String> filterResult1 = dataList1.stream().filter(item -> item.getId().equals(id)).map(item -> String.valueOf(item.getQuantity())).collect(Collectors.toList());
            List<String> filterResult2 = dataList2.stream().filter(item -> item.getId().equals(id)).map(item -> String.valueOf(item.getQuantity())).collect(Collectors.toList());
            // 判断两个列表是否相等
            boolean areEqual = CollectionUtil.isEqualList(filterResult1, filterResult2);
            if (!areEqual){
                CompareDataItem compareResultItem = new CompareDataItem();
                compareResultItem.setId(id);
                if (!CollectionUtils.isEmpty(filterResult1)){
                    compareResultItem.setQuantity1(String.join(",", filterResult1));
                }else {
                    compareResultItem.setQuantity1("0");
                }
                if (!CollectionUtils.isEmpty(filterResult2)){
                    compareResultItem.setQuantity2(String.join(",", filterResult2));
                }else {
                    compareResultItem.setQuantity2("0");
                }
                results.add(compareResultItem);
            }
        }
        CompareResult compareResult = new CompareResult();
        compareResult.setTitle1(title1);
        compareResult.setTitle2(title2);
        log.debug("匹配title1：{}", title1);
        log.debug("匹配title2：{}", title2);
        if (!CollectionUtils.isEmpty(results)){
            String matchResultFilename = "match_result_" + MD5.create().digestHex(compareParam.getFile1Md5() + compareParam.getFile2Md5()) + StrUtil.DOT + "xlsx";
            EasyExcel.write(uploadPath + File.separator + matchResultFilename, CompareDataItem.class)
                    .registerWriteHandler(new CustomerTitleHandler(title1, title2))
                    .autoCloseStream(false).sheet("匹配结果").doWrite(results);
            log.debug("匹配结果文件：{}", matchResultFilename);
            compareResult.setMatchResultFilename(matchResultFilename);
            compareResult.setRows(results);
        }
        return compareResult;
    }
}
