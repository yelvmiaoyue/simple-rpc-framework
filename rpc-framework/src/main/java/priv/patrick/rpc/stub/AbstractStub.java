package priv.patrick.rpc.stub;

import priv.patrick.rpc.transport.ServiceInfo;

public abstract class AbstractStub {
    protected ServiceInfo serviceInfo;

    protected <T> T invoke(RpcRequest rpcRequest) {
        return null;
    }

    public void setServiceInfo(ServiceInfo serviceInfo) {
        this.serviceInfo = serviceInfo;
    }
}
