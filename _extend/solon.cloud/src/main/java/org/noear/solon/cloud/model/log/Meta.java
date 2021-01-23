package org.noear.solon.cloud.model.log;

/**
 * @author noear 2021/1/22 created
 */
public class Meta {
    private String tag1;
    private String tag2;
    private String tag3;
    private String tag4;
    private String tag5;
    private String summary;

    public String tag1() {
        return tag1;
    }

    public Meta tag1(String tag1){
        this.tag1 = tag1;
        return this;
    }

    public String tag2() {
        return tag2;
    }

    public Meta tag2(String tag2){
        this.tag2 = tag2;
        return this;
    }

    public String tag3() {
        return tag3;
    }

    public Meta tag3(String tag3){
        this.tag3 = tag3;
        return this;
    }

    public String tag4() {
        return tag4;
    }

    public Meta tag4(String tag4){
        this.tag4 = tag4;
        return this;
    }

    public String tag5() {
        return tag5;
    }

    public Meta tag5(String tag5){
        this.tag5 = tag5;
        return this;
    }

    public String summary() {
        return summary;
    }

    public Meta summary(String summary){
        this.summary = summary;
        return this;
    }
}
