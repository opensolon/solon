package labs;

import org.noear.snack4.ONode;
import org.noear.solon.annotation.Managed;
import org.noear.solon.data.sql.bound.RowConverter;
import org.noear.solon.data.sql.bound.RowConverterFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear 2024/12/5 created
 */
@Managed
public class RowConverterFactoryImpl implements RowConverterFactory<Object> {
    @Override
    public RowConverter<Object> create(Class<?> tClass) {
        return new RowConverterImpl(tClass);
    }

    private static class RowConverterImpl implements RowConverter<Object> {
        private final Class<?> tClass;
        private ResultSetMetaData metaData;

        public RowConverterImpl(Class<?> tClass) {
            this.tClass = tClass;
        }

        @Override
        public Object convert(ResultSet rs) throws SQLException {
            if (metaData == null) {
                metaData = rs.getMetaData();
            }

            Map<String, Object> map = new LinkedHashMap<>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                String name = metaData.getColumnLabel(i);
                Object value = rs.getObject(i);
                map.put(name, value);
            }

            if (Map.class == tClass) {
                return map;
            } else {
                return ONode.ofBean(map).toBean(tClass);
            }
        }
    }
}
