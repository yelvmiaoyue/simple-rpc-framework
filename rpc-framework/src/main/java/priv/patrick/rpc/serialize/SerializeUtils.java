package priv.patrick.rpc.serialize;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Patrick_zhou
 */
public class SerializeUtils {
    private static final Logger log = LoggerFactory.getLogger(SerializeUtils.class);


    private SerializeUtils() {
    }

    public static byte[] serialize(Object input) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        HessianOutput hessianOutput = new HessianOutput(byteArrayOutputStream);
        try {
            hessianOutput.writeObject(input);
        } catch (IOException e) {
            log.error("序列化出错：{}", e.toString());
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static <T> T deserialize(byte[] input) {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(input);
        HessianInput hessianInput = new HessianInput(byteArrayInputStream);
        try {
            return (T) hessianInput.readObject();
        } catch (IOException e) {
            log.error("反序列化出错：{}", e.toString());
            return null;
        }
    }
}
