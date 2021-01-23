package org.noear.solon.cloud.extend.water.service;

import org.noear.mlog.Level;
import org.noear.mlog.LoggerSimple;
import org.noear.mlog.Marker;
import org.noear.mlog.utils.TagMarker;
import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudLogger;
import org.noear.solon.cloud.extend.water.WaterProps;
import org.noear.water.log.WaterLogger;

import java.util.Date;

/**
 * @author noear
 * @since 1.2
 */
public class CloudLoggerImp extends LoggerSimple implements  CloudLogger {
    private WaterLogger logger;

    public CloudLoggerImp(String name) {
        super(name);
        logger = new WaterLogger(name);
    }

    public CloudLoggerImp(Class<?> clz) {
        super(clz);
        String name = WaterProps.instance.getLogLogger();

        if (Utils.isNotEmpty(name)) {
            logger = new WaterLogger(name);
        }
    }

    @Override
    public void append(Level level, Marker marker, Object content) {
        org.noear.water.log.Level level1 = org.noear.water.log.Level.of(level.code / 10);

        TagMarker tags = null;

        if (marker != null) {
            if (marker instanceof TagMarker) {
                tags = (TagMarker) marker;
            }
        }

        String summary = null;

        if (clz != null) {
            if (tags == null) {
                tags = new TagMarker();
            }

            tags.tag4(clz.getName());
            summary = clz.getTypeName();
        }

        if (content instanceof Throwable) {
            content = Utils.getFullStackTrace((Throwable) content);
        } else {
            if ((content instanceof String) == false) {
                content = ONode.stringify(content);
            }
        }

        if (logger == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("[").append(level.name()).append("]");
            sb.append(new Date());

            if (marker != null) {
                sb.append(marker.formatAsString());
            }

            sb.append("::");

            if (content instanceof String) {
                sb.append(content);
            } else {
                sb.append(ONode.loadObj(content));
            }

            if (level == Level.ERROR) {
                System.err.println(sb.toString());
            } else {
                System.out.println(sb.toString());
            }
        } else {
            if (tags == null) {
                logger.append(level1, null, null, null, null, summary, content);
            } else {
                logger.append(level1, tags.tag1(), tags.tag2(), tags.tag3(), tags.tag4(), summary, content);
            }
        }
    }
}
