package com.qm.study.esayExcel.importExcel;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * 导入上下文
 */
public class ImportExcelContext<T> {

    //导入后的数据
    private List<T> importList = Lists.newArrayList();

    public List<T> getImportList() {
        return importList;
    }

    public void setImportList(List<T> importList) {
        this.importList = importList;
    }
}
