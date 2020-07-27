package priv.patrick.rpc.transport;

import java.util.concurrent.CompletableFuture;

/**
 * @author Patrick_zhou
 */
public class ResponseFuture {
    private Integer id;
    private CompletableFuture<Object> result;

    public ResponseFuture(Integer id, CompletableFuture<Object> result) {
        this.id = id;
        this.result = result;
    }

    public Integer getId() {
        return id;
    }

    public CompletableFuture<Object> getResult() {
        return result;
    }
}
