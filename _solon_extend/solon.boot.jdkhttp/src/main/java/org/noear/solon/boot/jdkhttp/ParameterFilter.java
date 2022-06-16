package org.noear.solon.boot.jdkhttp;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import org.noear.solon.boot.ServerProps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.*;

public class ParameterFilter extends Filter {
    @Override
    public String description() {
        return "Parses the requested URI for parameters";
    }


    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        Map<String, Object> parameters = new HashMap<>();
        exchange.setAttribute("parameters", parameters);

        parseGetParameters(exchange, parameters);
        parsePostParameters(exchange, parameters);
        chain.doFilter(exchange);
    }

    private void parseGetParameters(HttpExchange exchange, Map<String, Object> parameters) throws UnsupportedEncodingException {
        URI requestedUri = exchange.getRequestURI();
        String query = requestedUri.getRawQuery();
        parseQuery(query, parameters);
    }

    private void parsePostParameters(HttpExchange exchange, Map<String, Object> parameters) throws IOException {
        String method = exchange.getRequestMethod();

        if ("POST".equalsIgnoreCase(method)
                || "PUT".equalsIgnoreCase(method)
                || "DELETE".equalsIgnoreCase(method)
                || "PATCH".equalsIgnoreCase(method)) {

            String ct = exchange.getRequestHeaders().getFirst("Content-Type");

            if (ct == null) {
                return;
            }

            if (ct.toLowerCase(Locale.US).startsWith("application/x-www-form-urlencoded") == false) {
                return;
            }

            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), ServerProps.request_encoding);
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            parseQuery(query, parameters);
        }
    }

    private void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException {

        if (query != null) {
            String pairs[] = query.split("[&]");

            for (String pair : pairs) {
                String param[] = pair.split("[=]");

                String key = null;
                String value = null;
                if (param.length > 0) {
                    key = URLDecoder.decode(param[0], ServerProps.request_encoding);
                }

                if (param.length > 1) {
                    value = URLDecoder.decode(param[1], ServerProps.request_encoding);
                }else{
                    value = "";
                }

                if (parameters.containsKey(key)) {
                    Object obj = parameters.get(key);

                    if (obj instanceof List<?>) {
                        List<String> values = (List<String>) obj;
                        values.add(value);
                    } else if (obj instanceof String) {
                        List<String> values = new ArrayList<String>();
                        values.add((String) obj);
                        values.add(value);
                        parameters.put(key, values);
                    }
                } else {
                    parameters.put(key, value);
                }
            }
        }
    }
}
