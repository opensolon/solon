<html>
<body>
<#list map?keys as k>
<div><b>${k}:</b>${map[k]}</div>
</#list>
</body>
</html>