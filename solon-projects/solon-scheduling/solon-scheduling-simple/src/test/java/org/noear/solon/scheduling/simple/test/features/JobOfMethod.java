/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.scheduling.simple.test.features;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Managed;
import org.noear.solon.scheduling.annotation.Scheduled;

import java.util.Date;

/**
 * @author noear 2021/12/28 created
 */
@Slf4j
@Managed
public class JobOfMethod {
    @Scheduled(fixedRate = 1000 * 3)
    public void job21() {
        log.warn(new Date() + ": 1000*3");
    }

    @Scheduled(cron = "1s")
    public void job22() {
        log.warn(new Date() + ": 1s");
    }

    @Scheduled(cron = "0/10 * * * * ? *", zone = "+00")
    public void job23() {
        log.warn(new Date() + ": 0/10 * * * * ? *");
    }

    @Scheduled(cron = "0/10 * * * * ? *", zone = "CET")
    public void job24() {
        log.warn(new Date() + ": 0/10 * * * * ? *");
    }

    @Scheduled(cron = "0/10 * * * * ? *", zone = "Asia/Shanghai")
    public void job25() {
        log.warn(new Date() + ": 0/10 * * * * ? *");
    }

    @Scheduled(cron = "00 38 17 11 3 ? 2024")
    public void job26() {
        log.warn(new Date() + ": 00 38 17 11 3 ? 2024");
    }

    @Scheduled(cron = "* 22 11 6 4 ? 2024")
    public void job27() {
        log.warn(new Date() + ": * 13 11 6 4 ? 2024");
    }
}