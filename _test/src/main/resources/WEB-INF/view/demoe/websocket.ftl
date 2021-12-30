<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8"/>
    <title>go测试socket</title>
</head>
<body>
<script type="text/javascript">
    var sock = null;
    //服务器的地址
    var wsuri = "ws://127.0.0.1:18080/demoe/websocket?id=1&guid=2";
    window.onload = function() {
        console.log("onload");
        sock = new WebSocket(wsuri);
        sock.onopen = function() {
            //成功连接到服务器
            alert("connected to " + wsuri);
            sock.send("1111")
        }
        sock.onclose = function(e) {
            alert("connection closed (" + e.code + ")");
        }
        sock.onmessage = function(e) {
            //服务器发送通知
            //开始处理
            document.getElementById("rst").append("message received: " + e.data+"\n");
        }
    };

    function send() {
        var msg = document.getElementById('message').value;
        sock.send(msg);
    };

</script>
<h1>WebSocket Echo Test</h1>
<form>
    <p>
        Message: <input id="message" type="text" value="Hello, world!">
    </p>
</form>
<button onclick="send();">Send Message</button>
<div id="rst">

</div>

</body>
</html>