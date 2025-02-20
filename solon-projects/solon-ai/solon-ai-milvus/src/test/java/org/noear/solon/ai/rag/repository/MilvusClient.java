package org.noear.solon.ai.rag.repository;

import io.milvus.v2.client.ConnectConfig;
import io.milvus.v2.client.MilvusClientV2;

public class MilvusClient {
	private String uri="";
	
	private static MilvusClient instance = new MilvusClient();
	private  MilvusClientV2 client;
	
	public MilvusClientV2 getClient() {
		return client;
	}

	private  MilvusClient() {
		ConnectConfig connectConfig =ConnectConfig.builder()
		        .uri(uri)
		//        .token(String token)
		        //.username(String userName)
		        //.password(String password)
		        .build();
		client= new MilvusClientV2(connectConfig);
	}
	
	public static MilvusClient getInstance() {
		return instance;
	}
	
}
