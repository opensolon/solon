package org.noear.solon.cloud.extend.etcd.impl;

import static com.google.common.base.Charsets.UTF_8;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.options.WatchOption;
import io.grpc.stub.StreamObserver;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.noear.solon.cloud.CloudProps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author luke
 * @since 2.2
 */
public class EtcdClient {
    private static final Logger log = LoggerFactory.getLogger(EtcdClient.class);

    private Client real;
    private int sessionTimeout;

    public EtcdClient(CloudProps cloudProps, int sessionTimeout){
        String[] endpoints = cloudProps.getServer().split(",");
        endpoints = toURI(endpoints);
        this.real = Client.builder()
                .endpoints(endpoints)
                .build();
        this.sessionTimeout = sessionTimeout;
    }

    public KeyValue get(String key){

        KeyValue kv = null;
        try{
            // 获取KV客户端
            KV kvClient = real.getKVClient();

            // 设置要获取的键
            ByteSequence byteKey = ByteSequence.from(key, UTF_8);

            // 发送请求并获取响应
            CompletableFuture<GetResponse> getFuture = kvClient.get(byteKey);
            GetResponse response = getFuture.get();

            //如果有重复key？ 就只取第一个
            if(response.getKvs().size()>0){
                kv = response.getKvs().get(0);
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return kv;
    }

    public String getValueString(String key){
        return get(key).getValue().toString(UTF_8);
    }

    public List<KeyValue> getByPrefix(String key) throws ExecutionException, InterruptedException {
        ByteSequence byteKey = ByteSequence.from(key,UTF_8);

        CompletableFuture<GetResponse> getResponseCompletableFuture =
                real.getKVClient().get(ByteSequence.from(key,
                                UTF_8),
                        GetOption.newBuilder().withPrefix(ByteSequence.from(key, UTF_8)).build());

        return getResponseCompletableFuture.get().getKvs();
    }

    public boolean put(String key,String value){
        try {
            real.getKVClient()
                    .put(ByteSequence.from(key,UTF_8), ByteSequence.from(value,UTF_8)).get();
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
        return true;
    }

    public void putWithLease(String key,String value) {
        Lease leaseClient = real.getLeaseClient();

        leaseClient.grant(sessionTimeout).thenAccept(result -> {
            // 租约ID
            long leaseId = result.getID();

            // 准备好put操作的client
            KV kvClient = real.getKVClient();

            // put操作时的可选项，在这里指定租约ID
            PutOption putOption = PutOption.newBuilder().withLeaseId(leaseId).build();

            // put操作
            kvClient.put(ByteSequence.from(key, UTF_8), ByteSequence.from(value, UTF_8), putOption)
                    .thenAccept(putResponse -> {
                        // put操作完成后，再设置无限续租的操作
                        leaseClient.keepAlive(leaseId, new StreamObserver<LeaseKeepAliveResponse>() {

                            // 每次续租操作完成后，该方法都会被调用
                            @Override
                            public void onNext(LeaseKeepAliveResponse value) {
                                log.debug("Etcd key lease renewal completed: {}", key);
                            }

                            @Override
                            public void onError(Throwable t) {
                                log.error(t.getMessage(), t);
                            }

                            @Override
                            public void onCompleted() {
                            }
                        });
                    });
        });
    }

    public boolean remove(String key){
        try {
            real.getKVClient()
                    .delete(ByteSequence.from(key,UTF_8)).get();
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
        return true;
    }

    public void attentionKey(String key,Watch.Listener listener){

        real.getWatchClient().watch(ByteSequence.from(key,UTF_8),listener);
    }

    public void attentionKeysWithPrefix(String prefix,Watch.Listener listener){
        WatchOption watchOpts = WatchOption.newBuilder()
                .withPrefix(ByteSequence.from(prefix, UTF_8))
                .build();

        real.getWatchClient().watch(ByteSequence.from(prefix, UTF_8), watchOpts,
                listener);
    }

    /**
     * 关闭
     */
    public void close() {
        if (real != null) {
            real.close();
        }
    }

    //jetcd客户端要求endpoints必须是能直接转化为URI的格式，我就给它手动加上协议头了
    public String[] toURI(String[] endpoints){
        for (int i=0;i< endpoints.length;i++) {
            if(!endpoints[i].startsWith("http")){
                endpoints[i] = "http://" + endpoints[i];
            }
        }
        return endpoints;
    }
}
