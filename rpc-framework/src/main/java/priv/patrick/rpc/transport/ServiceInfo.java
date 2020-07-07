package priv.patrick.rpc.transport;

import java.net.URI;
import java.util.List;

public class ServiceInfo {
    private List<URI> uris;

    public ServiceInfo(List<URI> uris) {
        this.uris = uris;
    }

    public List<URI> getUris() {
        return uris;
    }
}
