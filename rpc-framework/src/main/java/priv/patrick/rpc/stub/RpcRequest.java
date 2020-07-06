package priv.patrick.rpc.stub;

import java.io.Serializable;

public class RpcRequest implements Serializable {
    private String interfaceName;
    private String methodName;
    private Argument[] arguments;

    public RpcRequest(String interfaceName, String methodName, Argument[] arguments) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.arguments = arguments;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Argument[] getArguments() {
        return arguments;
    }

    public void setArguments(Argument[] arguments) {
        this.arguments = arguments;
    }
}
