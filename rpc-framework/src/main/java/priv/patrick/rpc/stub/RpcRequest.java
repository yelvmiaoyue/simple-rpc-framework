package priv.patrick.rpc.stub;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author Patrick_zhou
 */
public class RpcRequest implements Serializable {
    private Integer id;
    private String interfaceName;
    private String methodName;
    private Argument[] arguments;

    public RpcRequest(String interfaceName, String methodName, Argument[] arguments) {
        this.id = IdGenerator.get();
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.arguments = arguments;
    }

    public Integer getId() {
        return id;
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

    @Override
    public String toString() {
        return "RpcRequest{" +
                "id=" + id +
                ", interfaceName='" + interfaceName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", arguments=" + Arrays.toString(arguments) +
                '}';
    }
}
