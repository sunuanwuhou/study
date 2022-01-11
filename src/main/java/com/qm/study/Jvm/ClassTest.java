package com.qm.study.Jvm;

/**
 * @author 01399578
 * @version 1.0
 * @description
 * @date 2022/1/11 8:05
 */
public class ClassTest {

    public static void main(String[] args) {

        try {

            Class<?> ClassTest = Class.forName("com.qm.study.Jvm.ClassTest");
            ClassTest classTest = (ClassTest) ClassTest.newInstance();


            Class<? extends ClassTest> classTest1 = new ClassTest().getClass();

            Class<ClassTest> classTest2 = ClassTest.class;


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
