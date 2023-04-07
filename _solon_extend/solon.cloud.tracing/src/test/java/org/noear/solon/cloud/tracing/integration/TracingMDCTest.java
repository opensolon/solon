package org.noear.solon.cloud.tracing.integration;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.AbstractHttpTester;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import static org.junit.Assert.*;

/**
 * @author orangej
 * @since 2023/4/7
 */
@SolonTest(value = HelloApp.class, env = "test")
@RunWith(SolonJUnit4ClassRunner.class)
public class TracingMDCTest extends AbstractHttpTester {

    @Test
    public void test() throws Exception {
        String rsp = path("/hello").get();
        System.out.println("traceId is " + rsp);
        assertFalse(rsp.isEmpty());
    }

}