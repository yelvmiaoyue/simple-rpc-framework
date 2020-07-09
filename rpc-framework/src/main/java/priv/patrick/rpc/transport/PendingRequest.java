package priv.patrick.rpc.transport;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class PendingRequest {
    private Map<Integer, CompletableFuture<Object>> requestMap=new ConcurrentHashMap<>();

    public void put(ResponseFuture responseFuture) {
        requestMap.put(responseFuture.getId(),responseFuture.getResult());
    }


    public CompletableFuture<Object> remove(Integer id) {
        return requestMap.remove(id);
    }
}
