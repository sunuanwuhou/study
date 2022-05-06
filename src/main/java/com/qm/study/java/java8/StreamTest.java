// package com.qm.study.java.java8;
//
// import com.google.common.collect.Lists;
// import lombok.Getter;
// import lombok.Setter;
//
// import java.util.ArrayList;
// import java.util.Optional;
// import java.util.function.BinaryOperator;
//
// /**
//  * @author 01399578
//  * @version 1.0
//  * @description
//  * @date 2022/2/18 23:38
//  */
// @Setter
// @Getter
// public class StreamTest {
//
//
//     private String key;
//     private int value;
//
//     public static void main(String[] args) {
//
//
//         StreamTest streamTest = new StreamTest();
//         streamTest.setKey("1");
//         streamTest.setValue(2);
//
//         StreamTest streamTest1 = new StreamTest();
//         streamTest1.setKey("1");
//         streamTest1.setValue(2);
//
//         StreamTest streamTest2 = new StreamTest();
//         streamTest2.setKey("2");
//         streamTest2.setValue(2);
//
//         ArrayList<StreamTest> streamTests = Lists.newArrayList(streamTest, streamTest1, streamTest2);
//
//         Optional<StreamTest> reduce = streamTests.stream().reduce(new myReduce());
//
//
//
//     }
//
//
//     public  static  class myReduce implements BinaryOperator<StreamTest> {
//
//         @Override
//         public StreamTest apply(StreamTest streamTest, StreamTest streamTest2) {
//
//             return null;
//         }
//     }
//
// }
//
//
