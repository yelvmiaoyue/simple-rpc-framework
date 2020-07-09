package priv.patrick.rpc.transport;

import java.io.Serializable;

public class RpcResponse implements Serializable {
    private Integer id;
    private Object response;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    @Override
    public String toString() {
        return "RpcResponse{" +
                "id=" + id +
                ", response=" + response +
                '}';
    }
}
