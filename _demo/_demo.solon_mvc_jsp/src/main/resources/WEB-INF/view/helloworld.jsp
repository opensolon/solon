<%@ page import="java.util.Random" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="ct" uri="/tags" %>
<html>
<head>
    <title>${title}</title>
</head>
<body>
<header>
    context path: ${ctx.path()}
</header>
<main>
    ${m.name} : ${message} （我想<a href="/jinjin.htm">静静</a>）
</main>
<ct:footer/>
</body>
</html>