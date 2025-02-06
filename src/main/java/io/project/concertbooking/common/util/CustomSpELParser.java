package io.project.concertbooking.common.util;

import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.function.Function;

public class CustomSpELParser {

    public static Object getDynamicValue(String[] parameterNames, Object[] args, String name) throws NoSuchMethodException {
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.registerFunction("convertToString", CustomSpELParser.class.getMethod("convertToString"));
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        return parser.parseExpression(name).getValue(context);
    }

    public static Function<Long, String> convertToString() {
        return String::valueOf;
    }
}
