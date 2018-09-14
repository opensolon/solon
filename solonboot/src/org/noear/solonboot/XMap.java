package org.noear.solonboot;

import java.util.LinkedHashMap;
import java.util.Map;

/*应用参数（转换启动命令参数）*/
public class XMap extends LinkedHashMap<String, String> {

    public XMap(){
        super();
    }

    public XMap(Map<String,String> map){
        super(map);
    }


    public static XMap from(String[] args) {
        XMap d = new XMap();

        if (args != null) {
            int len = args.length;

            for (int i = 0; i < len; i++) {
                String arg = args[i].replaceAll("-*", "");

                if (arg.indexOf("=") > 0) {
                    String[] ss = arg.split("=");
                    d.put(ss[0], ss[1]);
                } else {
                    if (i + 1 < len) {
                        d.put(arg, args[i + 1]);
                    }
                    i++;
                }
            }
        }

        return d;
    }

    public int getInt(String key) {
        String temp = get(key);
        if (XUtil.isEmpty(temp)) {
            return 0;
        } else {
            return Integer.parseInt(temp);
        }
    }

    public long getLong(String key) {
        String temp = get(key);
        if (XUtil.isEmpty(temp)) {
            return 0l;
        } else {
            return Long.parseLong(temp);
        }
    }

    public double getDouble(String key) {
        String temp = get(key);
        if (XUtil.isEmpty(temp)) {
            return 0d;
        } else {
            return Double.parseDouble(temp);
        }
    }

    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        forEach((k, v) -> {
            sb.append("\"").append(k).append("\":");
            writeValue(sb,v);
            sb.append(";");
        });

        if(sb.length()>1){
            sb.deleteCharAt(sb.length()-1);
        }

        sb.append("}");

        return sb.toString();
    }

    private final static void writeValue(StringBuilder  _Writer,String val)
    {
        if (val == null)
        {
            _Writer.append("null");
        }
        else
        {
            _Writer.append('\"');

            int n = val.length();
            char c;
            for (int i = 0; i < n; i++)
            {
                c = val.charAt(i);
                switch (c)
                {
                    case '\\':
                        _Writer.append("\\\\"); //20110809
                        break;

                    case '\"':
                        _Writer.append("\\\"");
                        break;

                    case '\n':
                        _Writer.append("\\n");
                        break;

                    case '\r':
                        _Writer.append("\\r");
                        break;

                    case '\t':
                        _Writer.append("\\t");
                        break;

                    case '\f':
                        _Writer.append("\\f");
                        break;

                    case '\b':
                        _Writer.append("\\b");
                        break;

                    default:
                        _Writer.append(c);
                        break;
                }
            }

            _Writer.append('\"');
        }
    }
}
