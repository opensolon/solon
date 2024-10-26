package org.noear.solon.data.sqlink.base.expression;

public enum SqlOperator
{
    POS("+", true),                             // +
    NEG("-", true),                             // -
    NOT("NOT", true),                             // !
    COMPL("~", true),                           // ~
    PREINC("++", true),                         // ++ _
    PREDEC("--", true),                         // -- _
    POSTINC("++"),                        // _ ++
    POSTDEC("--"),                        // _ --


    OR("OR"),                             // ||
    AND("AND"),                            // &&
    BITOR("|"),                           // |
    BITXOR("^"),                          // ^
    BITAND("&"),                          // &
    EQ("="),                             // ==
    NE("<>"),                             // !=
    LT("<"),                              // <
    GT(">"),                              // >
    LE("<="),                             // <=
    GE(">="),                             // >=
    SL("<<"),                             // <<
    SR(">>"),                             // >>
    USR(">>>"),                           // >>>
    PLUS("+"),                            // +
    MINUS("-"),                           // -
    MUL("*"),                             // *
    DIV("/"),                             // /
    MOD("%"),                             // %


    BITOR_ASG("|="),                      // |=
    BITXOR_ASG("^="),                     // ^=
    BITAND_ASG("&="),                     // &=

    SL_ASG("<<="),                        // <<=
    SR_ASG(">>="),                        // >>=
    USR_ASG(">>>="),                      // >>>=
    PLUS_ASG("+="),                       // +=
    MINUS_ASG("-="),                      // -=
    MUL_ASG("*="),                        // *=
    DIV_ASG("/="),                        // /=
    MOD_ASG("%="),                        // %=

    LIKE,
    IN,
    IS,
    BETWEEN,
    EXISTS(true),
    ;

    private final String operator;
    private final boolean isLeft;

    SqlOperator()
    {
        this.operator = name();
        this.isLeft = false;
    }

    SqlOperator(boolean isLeft)
    {
        this.operator = name();
        this.isLeft = isLeft;
    }

    SqlOperator(String operator)
    {
        this.operator = operator;
        this.isLeft = false;
    }

    SqlOperator(String operator, boolean isLeft)
    {
        this.operator = operator;
        this.isLeft = isLeft;
    }

    public String getOperator()
    {
        return operator;
    }

    public boolean isLeft()
    {
        return isLeft;
    }
}
