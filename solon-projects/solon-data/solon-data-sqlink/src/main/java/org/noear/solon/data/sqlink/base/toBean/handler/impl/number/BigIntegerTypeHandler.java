package org.noear.solon.data.sqlink.base.toBean.handler.impl.number;

import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BigIntegerTypeHandler implements ITypeHandler<BigInteger>
{
    @Override
    public BigInteger getValue(ResultSet resultSet, int index, Class<?> c) throws SQLException
    {
        BigDecimal decimal = resultSet.getBigDecimal(index);
        return decimal == null ? null : decimal.toBigInteger();
    }

    @Override
    public void setValue(PreparedStatement preparedStatement, int index, BigInteger bigInteger) throws SQLException
    {
        preparedStatement.setBigDecimal(index, new BigDecimal(bigInteger));
    }

}
