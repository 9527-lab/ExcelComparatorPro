package com.example.excelcomparatorpro.util;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PropertyPlaceholderHelper;

import java.util.List;
import java.util.Properties;

public class CustomerTitleHandler implements CellWriteHandler {
    private final String title1;
    private final String title2;
    PropertyPlaceholderHelper placeholderHelper = new PropertyPlaceholderHelper("${", "}");
    public CustomerTitleHandler(String title1, String title2) {
        this.title1 = title1;
        this.title2 = title2;
    }
    @Override
    public void beforeCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row, Head head, Integer integer, Integer integer1, Boolean aBoolean) {
        if (head != null) {
            List<String> headNameList = head.getHeadNameList();
            if (!CollectionUtils.isEmpty(headNameList)) {
                Properties properties = new Properties();
                properties.setProperty("title1", title1);
                properties.setProperty("title2", title2);
                headNameList.set(0, placeholderHelper.replacePlaceholders(headNameList.get(0), properties));
            }
        }
    }

}
