package com.qm.study.esayExcel.importExcel;

import com.alibaba.excel.read.listener.ReadListener;

/**
 * @author 01399578
 * @version 1.0
 */
public abstract class AbstractImportExcelListener<T> implements ReadListener<T> {

    //上下文
    private ImportExcelContext<T> excelContext;

    private final Class<T> cla;

    private ImportListenParam param;


    public AbstractImportExcelListener(Class<T> aClass, ImportListenParam param) {
        cla = aClass;
        this.param = param;
    }

    public ImportExcelContext<T> getExcelContext() {
        return excelContext;
    }

    public void setExcelContext(ImportExcelContext<T> excelContext) {
        this.excelContext = excelContext;
    }

    public Class<T> getCla() {
        return cla;
    }

    public ImportListenParam getParam() {
        return param;
    }

    public void setParam(ImportListenParam param) {
        this.param = param;
    }
}
