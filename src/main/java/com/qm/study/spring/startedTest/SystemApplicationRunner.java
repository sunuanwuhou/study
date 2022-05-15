package com.qm.study.spring.startedTest;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class SystemApplicationRunner implements ApplicationRunner {



    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("The SystemApplicationRunner start to initialize ...");
    }
}

