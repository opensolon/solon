package features.expr;

import org.junit.jupiter.api.Test;
import org.noear.solon.expression.snel.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2025/3/11 created
 */
public class Query1Test {
    @Test
    public void case1() {
        // 设置查询上下文
        Map<String,Object> context = new HashMap<>();
        context.put("age", 25);
        context.put("salary", 4000);
        context.put("isMarried", true);

        // 构建条件查询表达式树: (age > 18 AND salary < 5000) OR (NOT isMarried)
        VariableNode ageField = new VariableNode("age");
        ConstantNode ageValue = new ConstantNode(18);
        ComparisonNode ageComparison = new ComparisonNode(ComparisonOp.gt, ageField, ageValue);

        VariableNode salaryField = new VariableNode("salary");
        ConstantNode salaryValue = new ConstantNode(5000);
        ComparisonNode salaryComparison = new ComparisonNode(ComparisonOp.lt, salaryField, salaryValue);

        LogicalNode andNode = new LogicalNode(LogicalOp.AND, ageComparison, salaryComparison);

        VariableNode notMarriedField = new VariableNode("isMarried");
        ConstantNode notMarriedValue = new ConstantNode(false);
        ComparisonNode notMarriedComparison = new ComparisonNode(ComparisonOp.eq, notMarriedField, notMarriedValue);

        LogicalNode orNode = new LogicalNode(LogicalOp.OR, andNode, notMarriedComparison);

        // 计算条件查询表达式的值
        boolean result = orNode.eval(context::get);
        System.out.println("Result: " + result);  // 输出: Result: true
        assert result;

        PrintUtil.printTree(orNode);
    }
}