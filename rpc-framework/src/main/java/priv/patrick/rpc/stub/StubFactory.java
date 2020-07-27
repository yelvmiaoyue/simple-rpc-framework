package priv.patrick.rpc.stub;

import com.itranswarp.compiler.JavaStringCompiler;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Patrick_zhou
 */
public class StubFactory {

    private final static String CLASS_TEMPLATE =
            "package priv.patrick.rpc.stub;\n" +
                    "\n" +
                    "public class %s extends AbstractStub implements %s {\n" +
                    "%s \n" +
                    "}";

    private final static String METHOD_TEMPLATE =
            "    @Override\n" +
                    "    public %s %s( %s ) {\n" +
                    "%s \n" +
                    "        return invoke(\n" +
                    "                        new RpcRequest(\n" +
                    "                                \"%s\",\n" +
                    "                                \"%s\",\n" +
                    "                                arguments\n" +
                    "                        )\n" +
                    "        );\n" +
                    "    }\n";

    private final static String ARGUMENTS_TEMPLATE =
            " Argument[] arguments = new Argument[%d];\n" +
                    "%s\n";


    private final static String ARGUMENT_TEMPLATE =
            "        arguments[%d] =new Argument();\n" +
                    "        arguments[%d].setType(%s);\n" +
                    "        arguments[%d].setValue(arg%d);\n";


    public static AbstractStub createStub(ServiceInfo serviceInfo, Class<?> serviceName) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        //模板类名
        String stubSimpleName = serviceName.getSimpleName() + "Stub";
        //模板类实现的接口名
        String interfaceName = serviceName.getName();
        //模板类全路径
        String stubFullName = "priv.patrick.rpc.stub." + stubSimpleName;

        StringBuilder methodSources = new StringBuilder();
        Method[] methods = serviceName.getMethods();
        //循环填充方法模板
        for (Method method : methods) {
            String returnType = method.getReturnType().getTypeName();
            String methodName = method.getName();
            StringBuilder parameters = new StringBuilder();
            StringBuilder arguments = new StringBuilder();

            Class<?>[] parameterTypes = method.getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                String name = parameterTypes[i].getName();
                //形参列表
                parameters.append(name).append(" arg").append(i).append(",");
                //请求参数
                String argument = String.format(ARGUMENT_TEMPLATE,
                        i, i, name + ".class", i, i);
                arguments.append(argument);
            }
            //最后删掉个逗号
            if (parameters.length() > 0) {
                parameters.deleteCharAt(parameters.length() - 1);
            }

            String argumentSource = String.format(ARGUMENTS_TEMPLATE,
                    parameterTypes.length, arguments);
            String methodSource = String.format(METHOD_TEMPLATE,
                    returnType, methodName, parameters, argumentSource, interfaceName, methodName);
            methodSources.append(methodSource);
        }

        String source = String.format(CLASS_TEMPLATE, stubSimpleName, interfaceName, methodSources);
        // 编译源代码
        JavaStringCompiler compiler = new JavaStringCompiler();
        Map<String, byte[]> results = compiler.compile(stubSimpleName + ".java", source);
        // 加载编译好的类
        Class<?> clazz = compiler.loadClass(stubFullName, results);
        AbstractStub stubInstance = (AbstractStub) clazz.newInstance();
        stubInstance.setServiceInfo(serviceInfo);
        return stubInstance;
    }
}
