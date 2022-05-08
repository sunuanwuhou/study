package com.qm.study.java.Tuply;


public class TwoTuple<A, B> {

    public final A first;

    public final B second;

    public TwoTuple(A a, B b){
        first = a;
        second = b;
    }

    public String toString(){
        return "(" + first + ", " + second + ")";
    }

    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }
}