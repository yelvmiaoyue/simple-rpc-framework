package priv.patrick.rpc.stub;

import java.io.Serializable;

/**
 * @author Patrick_zhou
 */
public class Argument implements Serializable {
    private Class<?> type;
    private Object value;

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Argument{" +
                "type=" + type +
                ", value=" + value +
                '}';
    }
}
