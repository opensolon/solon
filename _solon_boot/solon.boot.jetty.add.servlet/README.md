用于支持 Servlet 注解：

```java
@WebServlet("/heihei/*")
public class HeheServlet extends HttpServlet {
    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        res.getWriter().write("OK");
    }
}
```


```java
@WebFilter("/hello/*")
public class HelloFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletResponse.getWriter().write("OK");
        //filterChain.doFilter(servletRequest,servletResponse);
    }
}
```


```java
@WebServlet("/hello/*")
public class HelloServlet implements Servlet {
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {

    }

    @Override
    public ServletConfig getServletConfig() {
        return null;
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        servletResponse.getWriter().write("NO");
    }

    @Override
    public String getServletInfo() {
        return null;
    }

    @Override
    public void destroy() {

    }
}
```


```java
/**
 * 给定侦听器类必须实现以下一个或多个接口：
 *
 * ServletContextAttributeListener
 * ServletRequestListener
 * ServletRequestAttributeListener
 * HttpSessionListener
 * HttpSessionAttributeListener
 * */
@WebListener
public class TmpListener implements ServletRequestListener {

    @Override
    public void requestInitialized(ServletRequestEvent sre) {
        System.out.println("我被触发了");
    }
}
```