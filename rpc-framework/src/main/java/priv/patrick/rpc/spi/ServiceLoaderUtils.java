package priv.patrick.rpc.spi;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * SPI工具类
 *
 * @author Patrick_zhou
 */
public class ServiceLoaderUtils {
    private ServiceLoaderUtils() {
    }

    public synchronized static <S> S load(Class<S> service) {
        return StreamSupport.
                stream(ServiceLoader.load(service).spliterator(), false)
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    public synchronized static <S> List<S> loadAll(Class<S> service) {
        return StreamSupport.
                stream(ServiceLoader.load(service).spliterator(), false)
                .collect(Collectors.toList());
    }
}
