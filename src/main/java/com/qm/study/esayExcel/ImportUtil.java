package com.qm.study.esayExcel;

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
public class ImportUtil<T> {


    public List<T> getExcelContent(MultipartFile file, Class cla) {
        ImportExcelContext<T> tImportExcelContext = new ImportExcelContext<>();
        try {
            InputStream inputStream = file.getInputStream();
            ImportListener<T> listener = new ImportListener<T>(tImportExcelContext);
            EasyExcelFactory.read(inputStream, cla, listener).sheet().doRead();
        } catch (Exception e) {
            //
        }
        return tImportExcelContext.getImportList();
    }

    public static void main(String[] args) {
        new ImportUtil<ImportExcelDto>().getExcelContent(null, ImportExcelDto.class);
    }
}
