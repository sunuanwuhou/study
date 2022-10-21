package com.qm.study.esayExcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;

import java.util.Map;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/10/21 23:08
 */
public class ImportListener<T> implements ReadListener<T> {


    private  ImportExcelContext importExcelContext;

    private int row = 0;

    public ImportListener() {

    }

    public ImportListener(ImportExcelContext importExcelContext) {
        this.importExcelContext = importExcelContext;
    }



    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        //公共表头校验
    }

    @Override
    public void invoke(T t, AnalysisContext analysisContext) {
        //可以判断行数大小
        row++;
        importExcelContext.getImportList().add(t);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
