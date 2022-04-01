package com.qm.study.markDownToc;

import com.github.houbb.markdown.toc.core.impl.AtxMarkdownToc;
import com.github.houbb.markdown.toc.vo.TocGen;

import java.io.File;
import java.util.List;

/**
 * @author qiumeng
 * @version 1.0
 * @description
 * @date 2021/7/4 19:45
 */
public class MarkDownTocTest {


     // private static final Logger logger= LoggerFactory.getLogger(MarkDownTocTest.class);

    public static final String PREFIX = "/src/main/学习/";
    public static final String PREFIX_MIAN = "/src/main/面试题/";

    public static void main(String[] args) {

        File emptyFile = new File("");


        String[] str = new String[]{
                PREFIX + "A.操作系统",
                PREFIX + "B.计算机网络",
                PREFIX + "C.数据结构与算法",
                PREFIX + "C.数据结构与算法",
                PREFIX + "C.数据结构与算法/arithmetic",
                PREFIX + "C.数据结构与算法/labuladong",
                PREFIX + "D.java",
                PREFIX + "D.java/collection",
                PREFIX + "E.Java并发",
                PREFIX + "F.Jvm",
                PREFIX + "G.数据库/Mysql",
                PREFIX + "G.数据库/Redis",
                PREFIX + "H.Spring",
                PREFIX + "H.Spring/SpringBoot",
                PREFIX + "H.Spring/SpringCloud",
                PREFIX + "H.Spring/SpringMvc",
                PREFIX + "I.分布式",
                PREFIX + "J.Mybatis",
                PREFIX + "k.leetcode",
                PREFIX + "L.Web",
                PREFIX + "M.设计模式",
                PREFIX + "N.系统架构",
                PREFIX + "O.代码技巧",
                PREFIX_MIAN,
        };

        for (String s : str) {
            StringBuilder sb = new StringBuilder();
            sb.append(emptyFile.getAbsolutePath());
            sb.append(s);
            try {
                List<TocGen> tocGens = AtxMarkdownToc.newInstance()
                        .subTree(false)
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
