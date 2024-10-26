package org.noear.solon.data.sqlink.base.toBean.handler.impl.varchar;


import org.noear.solon.data.sqlink.base.toBean.handler.ITypeHandler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CharTypeHandler implements ITypeHandler<Character>
{
    @Override
    public Character getValue(ResultSet resultSet, int index, Class<?> c) throws SQLException
    {
        String string = resultSet.getString(index);
        return string == null ? null : string.charAt(0);
    }

    @Override
    public void setValue(PreparedStatement preparedStatement, int index, Character character) throws SQLException
    {
        preparedStatement.setString(index, character.toString());
    }
}
