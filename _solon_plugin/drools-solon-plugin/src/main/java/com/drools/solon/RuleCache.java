package com.drools.solon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author <a href="mailto:hongwen0928@outlook.com">Karas</a>
 * @date 2019/9/28
 * @since 1.0.0
 */
public class RuleCache implements Runnable{

    private Logger logger = LoggerFactory.getLogger(RuleCache.class);

    private final KieTemplate kieTemplate;

    public RuleCache(KieTemplate kieTemplate) {
        this.kieTemplate = kieTemplate;
    }

    @Override
    public void run() {
        logger.debug("===>>开始更新规则文件");
        kieTemplate.doRead0();
    }
}
