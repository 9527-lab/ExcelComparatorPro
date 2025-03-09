package com.example.excelcomparatorpro.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

public class NumberConverter implements Converter<Integer> {

    @Override
    public Integer convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        // 获取 Excel 中的层级字段值
        String value = cellData.getStringValue();
        if (StringUtils.isNotBlank(value)) {
            // 使用正则表达式提取数字部分
            String numericPart = value.replaceAll("[^0-9]", "");
            // 如果有数字部分，则返回转换后的整数，否则返回 null
            return StringUtils.isNotEmpty(numericPart) ? Integer.parseInt(numericPart) : null;
        }
        return null;
    }

}
