package com.qm.study.markDownToc;

import com.github.houbb.markdown.toc.core.impl.AtxMarkdownToc;

import java.io.File;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/7/4 19:45
 */
public class MarkDownTocTest {


    // private static final Logger logger= LoggerFactory.getLogger(MarkDownTocTest.class);

    public static final String PREFIX_MIAN = "/src/main/";

    public static void main(String[] args) {

        File emptyFile = new File("");


        String[] str = new String[]{
                PREFIX_MIAN,
        };

        for (String s : str) {
            StringBuilder sb = new StringBuilder();
            sb.append(emptyFile.getAbsolutePath());
            sb.append(s);
            try {
                AtxMarkdownToc.newInstance()
                        .subTree(true)
                        .genTocDir(sb.toString());
            } catch (Exception e) {
                System.out.println("log");
            }

        }

        // root = root + "/src/main/学习/8.数据库/Redis/Redis底层数据结构详解.md";
        // AtxMarkdownToc.newInstance()
        //         .genTocFile(root);

    }


}
