package com.drools.solon;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;
import org.noear.solon.Utils;

import com.drools.solon.common.Constants;
import com.drools.solon.listener.DefaultAgendaEventListener;
import com.drools.solon.listener.DefaultProcessEventListener;
import com.drools.solon.listener.DefaultRuleRuntimeEventListener;
import com.drools.solon.util.FileUtil;

/**
 * @author <a href="mailto:hongwen0928@outlook.com">Karas</a>
 * @date 2019/9/26
 * @since 1.0.0
 */
public class KieTemplate extends KieAccessor {

    /**
     * 如果没有分布式的缓存工具，则使用本地缓存
     */
    private Map<String, String> CACHE_RULE = new ConcurrentHashMap<>();

    private static Map<String, BlockingQueue<KieSession>> SESSION = new ConcurrentHashMap<>();
    private static Map<String, BlockingQueue<KieBase>> BASE = new ConcurrentHashMap<>();

//    @Deprecated
//    private ClassLoader classLoader;

    public KieTemplate() {
        setCharset("UTF-8");
    }




//    @Deprecated
//    public void setBeanClassLoader(ClassLoader classLoader) {
//        this.classLoader = classLoader;
//    }


    /**
     * 根据文件名获取KieSession
     * @param fileName  文件名，可以输入多个（需要带后缀）
     * @return  KieSession
     */
    public KieSession getKieSession(String... fileName) {
        List<String> ds = new ArrayList<>();
        for (String name : fileName) {
            String content = CACHE_RULE.get(name);
            if (Utils.isBlank(content)) {
                ds = doReadTemp(fileName);
                return decodeToSession(ds.toArray(new String[]{}));
            }
            ds.add(CACHE_RULE.get(name));
        }
        return decodeToSession(ds.toArray(new String[]{}));
    }

    /**
     * 规则文件，决策表解析成字符串
     * @param realPath  决策表路径
     * @return  字符串
     */
    public String encodeToString(String realPath) {
        File file = new File(realPath);
        if (!file.exists()) {
            return null;
        }
        // drl文件
        if (realPath.endsWith(Constants.SUFFIX_DRL)) {
            return read(file);
        }
        InputStream is = null;
        try {
            is = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            logger.error("file not fount.");
        }
        // excel文件 xls和xlsx都支持
        // @author <a href="mailto:312182539@qq.com">fbf</a>
        if (realPath.endsWith(Constants.SUFFIX_EXCEL)||realPath.endsWith(Constants.SUFFIX_EXCEL_2007)) {
            return new SpreadsheetCompiler().compile(is, InputType.XLS);
        }
        // csv文件
        if (realPath.endsWith(Constants.SUFFIX_CSV)) {
            return new SpreadsheetCompiler().compile(is, InputType.CSV);
        }
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                logger.error("close stream error=>", e);
            }
        }
        return "package src.main.resources;";
    }

    /**
     * 读取drl文件
     */
    private String read(File file) {
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis, getCharset());
            br = new BufferedReader(isr);
            StringBuilder builder = new StringBuilder();
            String line;
            boolean tf = true;
            while ((line = br.readLine()) != null) {
                if (tf) {
                    builder.append(line);
                    tf = false;
                } else {
                    builder.append("\n").append(line);
                }
            }
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return null;
    }


    /**
     * 把字符串解析成KieSession
     * @param drl   规则文件字符串
     * @return  KieSession
     */
    public KieSession decodeToSession(String... drl) {
        KieHelper kieHelper = new KieHelper();
        for (String s : drl) {
            kieHelper.addContent(s, ResourceType.DRL);
        }

        if (getVerify() != null && Constants.LISTENER_OPEN.equalsIgnoreCase(getVerify())) {
            Results results = kieHelper.verify();
            if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)) {
                List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);
                for (Message message : messages) {
                    logger.error("Error: {}", message.getText());
                }
                throw new IllegalStateException("Compilation errors.");
            }

        }

        KieBaseConfiguration config = kieHelper.ks.newKieBaseConfiguration();

        if (EventProcessingOption.STREAM.getMode().equalsIgnoreCase(getMode())) {
            config.setOption(EventProcessingOption.STREAM);
        } else {
            config.setOption(EventProcessingOption.CLOUD);
        }
        KieBase kieBase = kieHelper.build(config);
        KieSession kieSession = kieBase.newKieSession();
        if (getListener() == null || !Constants.LISTENER_CLOSE.equalsIgnoreCase(getListener())) {
            kieSession.addEventListener(new DefaultRuleRuntimeEventListener());
            kieSession.addEventListener(new DefaultAgendaEventListener());
            kieSession.addEventListener(new DefaultProcessEventListener());
        }
        return kieSession;
    }

    /**
     * 获取绝对路径下的规则文件对应的KieBase
     * @param classPath     绝对路径/文件目录
     * @return KieBase
     */
    public KieBase getKieBase(String classPath) throws Exception {
        KieServices kieServices = KieServices.Factory.get();
        KieFileSystem kfs = kieServices.newKieFileSystem();
        Resource resource = ResourceFactory.newFileResource(classPath);
        kfs.write(resource);
        KieBuilder kieBuilder = kieServices.newKieBuilder(kfs).buildAll();
        if (kieBuilder.getResults().getMessages(Message.Level.ERROR).size() > 0) {
            throw new Exception();
        }
        KieContainer kieContainer = kieServices.newKieContainer(kieServices.getRepository()
                .getDefaultReleaseId());
        return kieContainer.getKieBase();
    }

    /**
     * 私有，do开头，0结尾的方法全部为私有
     */
    public void doRead0() {
        // 先存入1级缓存
        String pathTotal = getPath();
        if (pathTotal == null || pathTotal.length() == 0) {
            return;
        }
        String[] pathArray = pathTotal.split(KieAccessor.PATH_SPLIT);
        List<File> fileList = new ArrayList<>();
        for (String path : pathArray) {
            FileUtil.fileList(path, fileList);
        }
        for (File file : fileList) {
            String fileName = file.getName();
            String content = encodeToString(file.getPath());
            CACHE_RULE.put(fileName, content);
        }
        // 有Redis则存入Redis
        // ....

    }

    private List<String> doReadTemp(String... fileName) {
        String pathTotal = getPath();
        if(Utils.isBlank(pathTotal)){
            throw new IllegalArgumentException("path must be not null");
        }
        // 转换成集合
        List<String> fl = Arrays.asList(fileName);
        // 存放临时规则文件
        List<String> ds = new ArrayList<>();
        // 先存入1级缓存
        String[] pathArray = pathTotal.split(KieAccessor.PATH_SPLIT);
        List<File> fileList = new ArrayList<>();
        for (String path : pathArray) {
            FileUtil.fileList(path, fileList);
        }
        for (File file : fileList) {
            if (fl.contains(file.getName())) {
                String content = encodeToString(file.getPath());
                ds.add(content);
                CACHE_RULE.put(file.getName(), content);
            }
        }
        return ds;
    }


}
