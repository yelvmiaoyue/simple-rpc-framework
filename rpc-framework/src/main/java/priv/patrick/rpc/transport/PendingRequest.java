package priv.patrick.rpc.transport;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Patrick_zhou
 */
public class PendingRequest {
    private Map<Integer, CompletableFuture<Object>> requestMap=new ConcurrentHashMap<>();
    //todo 超时机制

    public void put(ResponseFuture responseFuture) {
        requestMap.put(responseFuture.getId(),responseFuture.getResult());
    }

    public CompletableFuture<Object> remove(Integer id) {
        return requestMap.remove(id);
    }
}
