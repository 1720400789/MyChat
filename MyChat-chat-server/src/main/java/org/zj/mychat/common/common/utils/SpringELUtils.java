package org.zj.mychat.common.common.utils;

import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * SpringEL 表达式工具类
 */
public class SpringELUtils {

    /**
     * 解析器
     */
    private static final ExpressionParser PARSER = new SpelExpressionParser();

    /**
     *
     */
    private static final DefaultParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    /**
     *
     * @param method
     * @return
     */
    public static String getMethodKey(Method method) {
        return method.getDeclaringClass() + "#" + method.getName();
    }

    /**
     * 解析 SpringEL 表达式
     * @param method 方法对象，用于获取键值对中的 key
     * @param args 参数数组，对应于键值对中的 value
     * @param key SpEL 表达式
     * @return 结果
     */
    public static String parseSpEL(Method method, Object[] args, String key) {
        String[] params = Optional.ofNullable(PARAMETER_NAME_DISCOVERER.getParameterNames(method))
                .orElse(new String[]{});
        // 构造上下文
        EvaluationContext context = new StandardEvaluationContext(); // el 解析需要的上下文对象
        for (int i = 0; i < params.length; i ++) {
            context.setVariable(params[i], args[i]); // 将所有参数都作为原材料放进去
        }
        // 解析并执行 SpEL 表达式，返回执行结果
        Expression expression = PARSER.parseExpression(key);
        return expression.getValue(context, String.class);
    }

    /*
    最简示例：
    public static void main(String[] args) {
        List<Integer> primes = new ArrayList<Integer>();
	    primes.addAll(Arrays.asList(2,3,5,7,11,13,17));

        // 创建解析器
        ExpressionParser parser = new SpelExpressionParser();
        // 构造上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("primes",primes);

        // 解析表达式
        // 这里是 SpringEL 表达式的写法，表示从 primes 中取出值大于 10 的
        Expression exp =parser.parseExpression("#primes.?[#this>10]");
        // 求值
        List<Integer> primesGreaterThanTen = (List<Integer>)exp.getValue(context);
}

     */
}
