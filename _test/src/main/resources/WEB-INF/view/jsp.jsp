<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="ct" uri="/tags" %>
<html>
<head>
    <title>${title}</title>
</head>
<body>
jsp::${msg}；i18n::${i18n.get("login.title")}

<ct:footer />
</body>
</html>