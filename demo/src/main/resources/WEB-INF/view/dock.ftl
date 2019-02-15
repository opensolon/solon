<html>
<head>
    <title>xxx</title>
</head>
<body>
<form enctype="multipart/form-data" action="/file/upload" method="post">
    <input type="file" name="fileUpload" multiple><button type="submit">上传</button>
</form>

<hr>

<form action="/file/array" method="post">
    <input type="text" name="aaa" value="1"><br>
    <input type="text" name="aaa" value="2"><br>
    <input type="text" name="ccc" value="ccc"><br>
    <button type="submit">提交</button>
</form>

</body>
</html>