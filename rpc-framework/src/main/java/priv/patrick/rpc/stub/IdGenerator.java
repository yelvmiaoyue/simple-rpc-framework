package priv.patrick.rpc.stub;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Patrick_zhou
 */
public class IdGenerator {
    private static AtomicInteger id=new AtomicInteger(1);

    private IdGenerator(){}

    public static Integer get(){
        return id.getAndIncrement();
    }
}
