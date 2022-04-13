package com.qm.study.spring.ThreeLevelCache;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TestService1Test {

    @Mock
    private TestService2 mockTestService2;

    @InjectMocks
    private TestService1 testService1UnderTest;

    @Test
    public void testTest1() {
        // Setup
        // Run the test
        testService1UnderTest.test1();

        // Verify the results
    }
}
