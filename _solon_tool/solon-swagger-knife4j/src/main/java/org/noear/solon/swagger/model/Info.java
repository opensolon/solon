package org.noear.solon.swagger.model;

import java.util.List;

/**
 * @author noear 2023/3/17 created
 */
public class Info {
    public String title;
    public String description;
    public String termsOfService;
    public String version;
    public List<InfoContact> contact;
    public List<InfoLicense> license;
}
