<%@ page import="java.util.Random" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="ct" uri="/tags" %>
<html>
<head>
    <title>${title}</title>
</head>
<body>
<div>
    context path: ${ctx.path()}
</div>
<div>
    properties: custom.user :${user}
</div>
<main>
    ${m.name} : ${message} （我想<a href="/jinjin.htm">静静</a>）
</main>
<ct:footer/>
</body>
</html>