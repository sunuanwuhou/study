package com.qm.study.esayExcel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class ImportExcelDto {
    @ExcelProperty(value = "名字", index = 0)
    private String valueField;

    @ExcelProperty(value = "年龄", index = 1)
    private String indexField;

}