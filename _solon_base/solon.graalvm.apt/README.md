GraalVm agentlib


java -agentlib:native-image-agent=config-output-dir=/home/kl/graal/data/ -jar demo.jar
java -agentlib:native-image-agent=config-output-dir=./config/ -jar solondemo.jar
java -agentlib:native-image-agent=config-merge-dir=./config/ -jar solondemo.jar



https://www.eolink.com/news/post/15380.html

https://blog.csdn.net/github_38592071/article/details/128527206