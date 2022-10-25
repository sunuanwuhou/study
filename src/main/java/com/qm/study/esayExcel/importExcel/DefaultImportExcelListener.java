package com.qm.study.esayExcel.importExcel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.ReadCellData;

import java.util.Map;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/10/21 23:08
 */
public class DefaultImportExcelListener<T> extends AbstractImportExcelListener<T> {


    private ImportExcelContext importExcelContext;

    private int maxRow = 1000;

    private int threshold = 0;


    public DefaultImportExcelListener(Class<T> aClass, ImportListenParam param) {
        super(aClass, param);
    }

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        //公共表头校验
    }

    @Override
    public void invoke(T t, AnalysisContext analysisContext) {
        //可以判断行数大小

        threshold++;
        importExcelContext.getImportList().add(t);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}
