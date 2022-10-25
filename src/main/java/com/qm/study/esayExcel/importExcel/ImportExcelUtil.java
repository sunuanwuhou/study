package com.qm.study.esayExcel.importExcel;

import com.alibaba.excel.EasyExcelFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/10/21 23:10
 */
public class ImportExcelUtil<T> {


    /**构建通用导出
     * @param file
     * @param cla
     * @return java.util.List<T>
     * @author qiumeng(01399578)
     * @date 2022-10-25
     */
    public static <T> List<T> buildDefaultExcelContext(MultipartFile file, Class<T> cla) {
        return new ImportExcelUtil<T>().getExcelContent(file, new DefaultImportExcelListener<>(cla, null));
    }

    public static <T> List<T> buildDefaultExcelContext(MultipartFile file, Class<T> cla, ImportListenParam param) {
        return new ImportExcelUtil<T>().getExcelContent(file, new DefaultImportExcelListener<>(cla, param));
    }

    public static <T> List<T> buildCustomExcelContext(MultipartFile file, AbstractImportExcelListener<T> listener) {
        return new ImportExcelUtil<T>().getExcelContent(file, listener);
    }


    public List<T> getExcelContent(MultipartFile file, AbstractImportExcelListener<T> listener) {
        ImportExcelContext<T> tImportExcelContext = new ImportExcelContext<>();
        try {
            InputStream inputStream = file.getInputStream();
            listener.setExcelContext(tImportExcelContext);
            EasyExcelFactory.read(inputStream, listener.getClass(), listener).sheet().doRead();
        } catch (Exception e) {
            //
        }
        return tImportExcelContext.getImportList();
    }

}
