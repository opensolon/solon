package com.dtflys.forest.solon.properties;

/**
 * @author noear
 * @since 1.11
 */
public class ForestConvertProperties {

    private ForestConverterItemProperties text;

    private ForestConverterItemProperties json;

    private ForestConverterItemProperties xml;

    private ForestConverterItemProperties binary;

    private ForestConverterItemProperties protobuf;

    public ForestConverterItemProperties getText() {
        return text;
    }

    public void setText(ForestConverterItemProperties text) {
        this.text = text;
    }

    public ForestConverterItemProperties getJson() {
        return json;
    }

    public void setJson(ForestConverterItemProperties json) {
        this.json = json;
    }

    public ForestConverterItemProperties getXml() {
        return xml;
    }

    public void setXml(ForestConverterItemProperties xml) {
        this.xml = xml;
    }

    public ForestConverterItemProperties getBinary() {
        return binary;
    }

    public void setBinary(ForestConverterItemProperties binary) {
        this.binary = binary;
    }

    public ForestConverterItemProperties getProtobuf() {
        return protobuf;
    }

    public void setProtobuf(ForestConverterItemProperties protobuf) {
        this.protobuf = protobuf;
    }
}
