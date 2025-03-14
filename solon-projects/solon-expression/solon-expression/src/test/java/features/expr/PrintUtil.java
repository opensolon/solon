package features.expr;

import org.noear.solon.expression.Expression;
import org.noear.solon.expression.snel.ComparisonNode;
import org.noear.solon.expression.snel.ConstantNode;
import org.noear.solon.expression.snel.LogicalNode;
import org.noear.solon.expression.snel.VariableNode;

import java.util.Collection;

/**
 * @author noear 2025/3/12 created
 */
public class PrintUtil {
    /**
     * 打印
     */
    public static void printTree(Expression node) {
        printTreeDo(node, 0);
    }

    static void printTreeDo(Expression node, int level) {
        if (node instanceof VariableNode) {
            System.out.println(prefix(level) + "Field: " + ((VariableNode) node).getName());
        } else if (node instanceof ConstantNode) {
            Object value = ((ConstantNode) node).getValue();
            if (value instanceof String) {
                System.out.println(prefix(level) + "Value: '" + value + "'");
            } else {
                System.out.println(prefix(level) + "Value: " + value);
            }

        } else if (node instanceof ComparisonNode) {
            ComparisonNode compNode = (ComparisonNode) node;
            System.out.println(prefix(level) + "Comparison: " + compNode.getOperator());

            printTreeDo(compNode.getLeft(), level + 1);
            printTreeDo(compNode.getRight(), level + 1);
        } else if (node instanceof LogicalNode) {
            LogicalNode opNode = (LogicalNode) node;
            System.out.println(prefix(level) + "Logical: " + opNode.getOperator());

            printTreeDo(opNode.getLeft(), level + 1);
            printTreeDo(opNode.getRight(), level + 1);
        }
    }

    static String prefix(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append("  ");
        }

        return sb.toString();
    }


    /// ////////


    public static void printTree2(Expression node) {
        StringBuilder buf = new StringBuilder();
        printTree2Do(node, buf);
        System.out.println(buf);
    }

    static void printTree2Do(Expression node, StringBuilder buf) {
        if (node instanceof VariableNode) {
            buf.append(((VariableNode) node).getName());
        } else if (node instanceof ConstantNode) {
            ConstantNode node1 = ((ConstantNode) node);
            Object value = node1.getValue();
            if (value instanceof String) {
                buf.append("'").append(value).append("'");
            } else {
                if (node1.isCollection()) {
                    buf.append("[");
                    for (Object item1 : ((Collection) value)) {
                        if (item1 instanceof String) {
                            buf.append("'").append(((String) item1)).append("'");
                        } else {
                            buf.append(item1);
                        }
                        buf.append(",");
                    }

                    if (buf.length() > 1) {
                        buf.setLength(buf.length() - 1);
                    }
                    buf.append("]");
                } else {
                    buf.append(value);
                }
            }
        } else if (node instanceof ComparisonNode) {
            ComparisonNode compNode = (ComparisonNode) node;

            buf.append("(");
            printTree2Do(compNode.getLeft(), buf);
            buf.append(" " + compNode.getOperator().getCode() + " ");
            printTree2Do(compNode.getRight(), buf);
            buf.append(")");

        } else if (node instanceof LogicalNode) {
            LogicalNode opNode = (LogicalNode) node;

            buf.append("(");

            if (opNode.getRight() != null) {
                //二元
                printTree2Do(opNode.getLeft(), buf);
                buf.append(" " + opNode.getOperator().getCode() + " ");
                printTree2Do(opNode.getRight(), buf);
            } else {
                //一元
                buf.append(opNode.getOperator().getCode() + " ");
                printTree2Do(opNode.getLeft(), buf);
            }
            buf.append(")");
        }
    }
}