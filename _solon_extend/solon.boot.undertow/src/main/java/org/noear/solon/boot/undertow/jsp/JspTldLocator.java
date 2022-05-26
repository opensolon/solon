package org.noear.solon.boot.undertow.jsp;


import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.jasper.deploy.FunctionInfo;
import org.apache.jasper.deploy.TagAttributeInfo;
import org.apache.jasper.deploy.TagFileInfo;
import org.apache.jasper.deploy.TagInfo;
import org.apache.jasper.deploy.TagLibraryInfo;
import org.apache.jasper.deploy.TagLibraryValidatorInfo;
import org.apache.jasper.deploy.TagVariableInfo;
import org.jboss.annotation.javaee.Icon;
import org.jboss.metadata.javaee.spec.DescriptionGroupMetaData;
import org.jboss.metadata.javaee.spec.ParamValueMetaData;
import org.jboss.metadata.parser.jsp.TldMetaDataParser;
import org.jboss.metadata.parser.util.NoopXMLResolver;
import org.jboss.metadata.web.spec.AttributeMetaData;
import org.jboss.metadata.web.spec.FunctionMetaData;
import org.jboss.metadata.web.spec.TagFileMetaData;
import org.jboss.metadata.web.spec.TagMetaData;
import org.jboss.metadata.web.spec.TldMetaData;
import org.jboss.metadata.web.spec.VariableMetaData;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.JarClassLoader;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.util.ScanUtil;
import org.noear.solon.ext.SupplierEx;

/**
 * Jsp Tld 定位器
 */
public class JspTldLocator {
    public static HashMap<String, TagLibraryInfo> createTldInfos(String webinfo_path) throws IOException {

        List<URL> urls = getURLs();

        HashMap<String, TagLibraryInfo> tagLibInfos = new HashMap<>();

        //加载外部jar包的.tld（也可能包括自己的了）
        for (URL url : urls) {
            if (url.toString().endsWith(".jar")) {
                try {
                    String file_uri = URLDecoder.decode(url.getFile(), Solon.encoding());

                    JarFile jarFile = new JarFile(file_uri);

                    final Enumeration<JarEntry> entries = jarFile.entries();

                    while (entries.hasMoreElements()) {
                        final JarEntry entry = entries.nextElement();

                        if (entry.getName().endsWith(".tld")) {
                            loadTagLibraryInfo(tagLibInfos, () -> {
                                JarEntry fileEntry = jarFile.getJarEntry(entry.getName());
                                return jarFile.getInputStream(fileEntry);
                            });
                        }
                    }
                } catch (Throwable ex) {
                    EventBus.push(ex);
                }
            }
        }


        //自己的.tld
        try {
            ScanUtil.scan(JarClassLoader.global(), webinfo_path, n -> n.endsWith(".tld")).forEach((uri) -> {
                loadTagLibraryInfo(tagLibInfos, () -> Utils.getResource(uri).openStream());
            });
        } catch (Throwable ex) {
            EventBus.push(ex);
        }

        return tagLibInfos;
    }

    static  List<URL> getURLs(){
        List<URL> urls = new ArrayList<>();

        String classPath = System.getProperty("java.class.path");

        if (classPath != null) {
            String separator = System.getProperty("path.separator");
            if(Utils.isEmpty(separator)){
                separator=":";
            }

            String[] list = classPath.split(separator);
            for (String uri : list) {
                //
                //加载系统java包，用于后续加载使用
                //
                if (uri.endsWith(".jar") || uri.indexOf(".jar ") > 0) {
                    try {
                        if (uri.startsWith("/")) {
                            uri = "file:" + uri;
                        }

                        URL url = URI.create(uri).toURL();
                        urls.add(url);
                    } catch (Throwable ex) {
                        //ex.printStackTrace();
                    }
                }
            }
        }

        return urls;
    }

    static void loadTagLibraryInfo(HashMap<String, TagLibraryInfo> tagLibInfos, SupplierEx<InputStream> supplier) {
        InputStream is = null;

        try {
            is = supplier.get();

            final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            inputFactory.setXMLResolver(NoopXMLResolver.create());
            XMLStreamReader xmlReader = inputFactory.createXMLStreamReader(is);
            TldMetaData tldMetadata = TldMetaDataParser.parse(xmlReader);
            TagLibraryInfo taglibInfo = getTagLibraryInfo(tldMetadata);
            if (!tagLibInfos.containsKey(taglibInfo.getUri())) {
                tagLibInfos.put(taglibInfo.getUri(), taglibInfo);
            }

        } catch (Throwable e) {
            EventBus.push(e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ignore) {
            }
        }
    }

    static TagLibraryInfo getTagLibraryInfo(TldMetaData tldMetaData) {
        TagLibraryInfo tagLibraryInfo = new TagLibraryInfo();
        tagLibraryInfo.setTlibversion(tldMetaData.getTlibVersion());
        if (tldMetaData.getJspVersion() == null) {
            tagLibraryInfo.setJspversion(tldMetaData.getVersion());
        } else {
            tagLibraryInfo.setJspversion(tldMetaData.getJspVersion());
        }
        tagLibraryInfo.setShortname(tldMetaData.getShortName());
        tagLibraryInfo.setUri(tldMetaData.getUri());
        if (tldMetaData.getDescriptionGroup() != null) {
            tagLibraryInfo.setInfo(tldMetaData.getDescriptionGroup().getDescription());
        }
        // Validator
        if (tldMetaData.getValidator() != null) {
            TagLibraryValidatorInfo tagLibraryValidatorInfo = new TagLibraryValidatorInfo();
            tagLibraryValidatorInfo.setValidatorClass(tldMetaData.getValidator().getValidatorClass());
            if (tldMetaData.getValidator().getInitParams() != null) {
                for (ParamValueMetaData paramValueMetaData : tldMetaData.getValidator().getInitParams()) {
                    tagLibraryValidatorInfo.addInitParam(paramValueMetaData.getParamName(), paramValueMetaData.getParamValue());
                }
            }
            tagLibraryInfo.setValidator(tagLibraryValidatorInfo);
        }
        // Tag
        if (tldMetaData.getTags() != null) {
            for (TagMetaData tagMetaData : tldMetaData.getTags()) {
                TagInfo tagInfo = new TagInfo();
                tagInfo.setTagName(tagMetaData.getName());
                tagInfo.setTagClassName(tagMetaData.getTagClass());
                tagInfo.setTagExtraInfo(tagMetaData.getTeiClass());
                if (tagMetaData.getBodyContent() != null) {
                    tagInfo.setBodyContent(tagMetaData.getBodyContent().toString());
                }
                tagInfo.setDynamicAttributes(tagMetaData.getDynamicAttributes());
                // Description group
                if (tagMetaData.getDescriptionGroup() != null) {
                    DescriptionGroupMetaData descriptionGroup = tagMetaData.getDescriptionGroup();
                    if (descriptionGroup.getIcons() != null && descriptionGroup.getIcons().value() != null
                            && (descriptionGroup.getIcons().value().length > 0)) {
                        Icon icon = descriptionGroup.getIcons().value()[0];
                        tagInfo.setLargeIcon(icon.largeIcon());
                        tagInfo.setSmallIcon(icon.smallIcon());
                    }
                    tagInfo.setInfoString(descriptionGroup.getDescription());
                    tagInfo.setDisplayName(descriptionGroup.getDisplayName());
                }
                // Variable
                if (tagMetaData.getVariables() != null) {
                    for (VariableMetaData variableMetaData : tagMetaData.getVariables()) {
                        TagVariableInfo tagVariableInfo = new TagVariableInfo();
                        tagVariableInfo.setNameGiven(variableMetaData.getNameGiven());
                        tagVariableInfo.setNameFromAttribute(variableMetaData.getNameFromAttribute());
                        tagVariableInfo.setClassName(variableMetaData.getVariableClass());
                        tagVariableInfo.setDeclare(variableMetaData.getDeclare());
                        if (variableMetaData.getScope() != null) {
                            tagVariableInfo.setScope(variableMetaData.getScope().toString());
                        }
                        tagInfo.addTagVariableInfo(tagVariableInfo);
                    }
                }
                // Attribute
                if (tagMetaData.getAttributes() != null) {
                    for (AttributeMetaData attributeMetaData : tagMetaData.getAttributes()) {
                        TagAttributeInfo ari = new TagAttributeInfo();
                        ari.setName(attributeMetaData.getName());
                        ari.setType(attributeMetaData.getType());
                        ari.setReqTime(attributeMetaData.getRtexprvalue());
                        ari.setRequired(attributeMetaData.getRequired());
                        ari.setFragment(attributeMetaData.getFragment());
                        if (attributeMetaData.getDeferredValue() != null) {
                            ari.setDeferredValue("true");
                            ari.setExpectedTypeName(attributeMetaData.getDeferredValue().getType());
                        } else {
                            ari.setDeferredValue("false");
                        }
                        if (attributeMetaData.getDeferredMethod() != null) {
                            ari.setDeferredMethod("true");
                            ari.setMethodSignature(attributeMetaData.getDeferredMethod().getMethodSignature());
                        } else {
                            ari.setDeferredMethod("false");
                        }
                        tagInfo.addTagAttributeInfo(ari);
                    }
                }
                tagLibraryInfo.addTagInfo(tagInfo);
            }
        }
        // Tag files
        if (tldMetaData.getTagFiles() != null) {
            for (TagFileMetaData tagFileMetaData : tldMetaData.getTagFiles()) {
                TagFileInfo tfi = new TagFileInfo();
                tfi.setName(tagFileMetaData.getName());
                tfi.setPath(tagFileMetaData.getPath());
                tagLibraryInfo.addTagFileInfo(tfi);
            }
        }
        // Function
        if (tldMetaData.getFunctions() != null) {
            for (FunctionMetaData functionMetaData : tldMetaData.getFunctions()) {
                FunctionInfo fi = new FunctionInfo();
                fi.setName(functionMetaData.getName());
                fi.setFunctionClass(functionMetaData.getFunctionClass());
                fi.setFunctionSignature(functionMetaData.getFunctionSignature());
                tagLibraryInfo.addFunctionInfo(fi);
            }
        }

        return tagLibraryInfo;
    }
}