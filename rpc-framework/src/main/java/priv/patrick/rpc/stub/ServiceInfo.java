package priv.patrick.rpc.stub;

import priv.patrick.rpc.transport.PendingRequest;

import java.net.URI;
import java.util.List;

public class ServiceInfo {
    private List<URI> uris;
    private PendingRequest pendingRequest;

    public ServiceInfo(List<URI> uris, PendingRequest pendingRequest) {
        this.uris = uris;
        this.pendingRequest = pendingRequest;
    }

    public List<URI> getUris() {
        return uris;
    }

    public void setUris(List<URI> uris) {
        this.uris = uris;
    }

    public PendingRequest getPendingRequest() {
        return pendingRequest;
    }

}
