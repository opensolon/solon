package org.noear.solon.ai.rag.repository;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;

import org.junit.jupiter.api.Test;
import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.rag.Document;
import org.noear.solon.ai.rag.RepositoryStorable;
import org.noear.solon.ai.rag.splitter.TokenSizeTextSplitter;
import org.noear.solon.net.http.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MilvusRepositoryTest {
    private static final Logger log = LoggerFactory.getLogger(MilvusRepositoryTest.class);
    private static String milvusUri = "http://183.237.106.204:19530";

    private ConnectConfig connectConfig = ConnectConfig.builder()
            .uri(milvusUri)
            .build();
    private MilvusClientV2 client = new MilvusClientV2(connectConfig);
    @Test
    public void rag_case1() throws Exception {
        try {
            //1.构建模型
            ChatModel chatModel = TestUtils.getChatModelOfGiteeai();

            //2.构建知识库
            MilvusRepository repository = new MilvusRepository(
                    TestUtils.getEmbeddingModelOfGiteeai(),
                    client); //3.初始化知识库

            repository.dropCollection();
            repository.buildCollection();

            load(repository, "https://solon.noear.org/article/about?format=md");
            load(repository, "https://h5.noear.org/more.htm");
            load(repository, "https://h5.noear.org/readme.htm");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    @Test
    public void searchTest() throws Exception {
        try {
            //1.构建模型
            ChatModel chatModel = TestUtils.getChatModelOfGiteeai();

            //2.构建知识库
            MilvusRepository repository = new MilvusRepository(
                    TestUtils.getEmbeddingModelOfGiteeai(),
                    client); 
            //3.初始化知识库
            List<Document> list = repository.search("solon");
            if (list != null) {
                for (Document doc : list) {
                    System.out.println(doc.getId() + ":" + doc.getScore()+":" + doc.getUrl() + "【" + doc.getContent() + "】");
                }
            }

            list = repository.search("spring");
            if (list != null) {
                for (Document doc : list) {
                    System.out.println(doc.getId() + ":" + doc.getScore() +":" + doc.getUrl() + "【" + doc.getContent() + "】");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    private void load(RepositoryStorable repository, String url) throws IOException {
        String text = HttpUtils.http(url).get(); //1.加载文档（测试用）
        List<Document> documents = new TokenSizeTextSplitter(200).split(text).stream().map(doc -> {
        	doc.url(url);
        	return doc;
        }).collect(Collectors.toList()); //2.分割文档（确保不超过 max-token-size）
        repository.store(documents); //（推入文档）
    }
}