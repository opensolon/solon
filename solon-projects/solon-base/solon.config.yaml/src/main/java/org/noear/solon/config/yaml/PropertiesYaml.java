package org.noear.solon.config.yaml;

import org.noear.solon.Solon;
import org.noear.solon.core.Constants;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

/**
 * Yaml 属性
 *
 * @author noear
 * @since 1.5
 * */
public class PropertiesYaml extends Properties {

    private Yaml createYaml() {
        return new Yaml();
    }

    public void loadYml(InputStream inputStream) throws Exception {
        try(InputStreamReader is =  new InputStreamReader(inputStream)) {
            this.loadYml(is);
        }
    }
    public synchronized void loadYml(Reader reader) throws IOException {
        List<String> strList = new ArrayList<>();
        try(BufferedReader in = new BufferedReader(reader)){
            StringBuffer buffer = new StringBuffer();
            String line = null;
            while ((line = in.readLine()) != null){
                if(Constants.YML_SPLIT_PATTERN.matcher(line).matches()){
                    strList.add(buffer.toString());
                    buffer.setLength(0);
                }else{
                    buffer.append(line).append("\n");
                }
            }
            strList.add(buffer.toString());
        }
        AtomicReference<String> envAtomicReference = new AtomicReference<>(System.getProperty(Constants.SOLON_ENV));
        String str = null;
        for (int i = 0; i < strList.size(); i++) {
            str = strList.get(i);
            if(str == null || str.replaceAll(" ", "").length() == 0){
                continue;
            }
            Yaml yaml = createYaml();
            Object tmp = yaml.load(str);
            Map<Object, Object> temProp = new TreeMap<>();
            load0(temProp, "", tmp);
            if(temProp.size() == 0){
                continue;
            }
            if(envAtomicReference.get() == null && i ==0 && temProp.containsKey(Constants.SOLON_ENV)){
                envAtomicReference.set(String.valueOf(temProp.get(Constants.SOLON_ENV)));
            }
            if(temProp.containsKey(Constants.SOLON_ACTIVATE_ON_ENV)){
                if(Arrays.stream(String.valueOf(temProp.get(Constants.SOLON_ACTIVATE_ON_ENV)).split("\\|")).anyMatch(activateOnEnv->{
                    return activateOnEnv.replaceAll(" ", "").equals(envAtomicReference.get());
                })){
                    super.putAll(temProp);
                }
            }else{
                super.putAll(temProp);
            }
        }
    }


    private void load0(Map<Object, Object> temProp, String prefix, Object tmp) {
        if (tmp instanceof Map) {
            ((Map<String, Object>) tmp).forEach((k, v) -> {
                String prefix2 = prefix + "." + k;
                this.load0(temProp, prefix2, v);
            });
            return;
        }

        if (tmp instanceof List) {
            //do_put(prefix, tmp);
            int index = 0;
            for (Object v : ((List) tmp)) {
                String prefix2 = prefix + "[" + index + "]";
                this.load0(temProp, prefix2, v);
                index++;
            }
            return;
        }

        if (tmp == null) {
            this.put0(temProp, prefix, "");
        } else {
            this.put0(temProp, prefix, String.valueOf(tmp));
        }
    }

    private void put0(Map<Object, Object> temProp, String key, Object val) {
        if (key.startsWith(".")) {
            temProp.put(key.substring(1), val);
        } else {
            temProp.put(key, val);
        }
    }
}
