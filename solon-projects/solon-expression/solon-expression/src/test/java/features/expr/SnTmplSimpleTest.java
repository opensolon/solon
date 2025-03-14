package features.expr;

import org.junit.jupiter.api.Test;
import org.noear.solon.expression.snel.SnEL;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SnTmplSimpleTest {

    @Test
    public void testSimpleText() {
        String template = "Hello, World!";
        String result = SnEL.evalTmpl(template);
        assertEquals("Hello, World!", result);
    }

    @Test
    public void testSingleVariable() {
        String template = "Hello, #{name}!";
        Map<String, String> context = new HashMap<>();
        context.put("name", "John");
        String result = SnEL.evalTmpl(template, context);
        assertEquals("Hello, John!", result);
    }

    @Test
    public void testVariableAtStart() {
        String template = "#{greeting}, World!";
        Map<String, String> context = new HashMap<>();
        context.put("greeting", "Hi");
        String result = SnEL.evalTmpl(template, context);
        assertEquals("Hi, World!", result);
    }

    @Test
    public void testVariableAtEnd() {
        String template = "Hello, #{name}";
        Map<String, String> context = new HashMap<>();
        context.put("name", "Jane");
        String result = SnEL.evalTmpl(template, context);
        assertEquals("Hello, Jane", result);
    }

    @Test
    public void testTwoVariables() {
        String template = "#{greeting}, #{name}!";
        Map<String, String> context = new HashMap<>();
        context.put("greeting", "Hello");
        context.put("name", "Bob");
        String result = SnEL.evalTmpl(template, context);
        assertEquals("Hello, Bob!", result);
    }

    @Test
    public void testEmptyVariable() {
        String template = "Hello, #{name}!";
        Map<String, String> context = new HashMap<>();
        context.put("name", "");
        String result = SnEL.evalTmpl(template, context);
        assertEquals("Hello, !", result);
    }

    @Test
    public void testMissingVariable() {
        String template = "Hello, #{name}!";
        String result = SnEL.evalTmpl(template);
        assertEquals("Hello, null!", result);
    }

    @Test
    public void testVariableWithSpace() {
        String template = "Hello, #{  name  }!";
        Map<String, String> context = new HashMap<>();
        context.put("name", "Alice");
        String result = SnEL.evalTmpl(template, context);
        assertEquals("Hello, Alice!", result);
    }

    @Test
    public void testTextWithHashButNoBrace() {
        String template = "Hello, #name!";
        String result = SnEL.evalTmpl(template);
        assertEquals("Hello, #name!", result);
    }

    @Test
    public void testTextWithBraceButNoHash() {
        String template = "Hello, {name}!";
        String result = SnEL.evalTmpl(template);
        assertEquals("Hello, {name}!", result);
    }
}