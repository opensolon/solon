package org.noear.solon.admin.client.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.noear.solon.lang.Nullable;

/**
 * 应用程序数据传输 Dto
 *
 * @param <T> 要传输的数据类型
 * @author shaokeyibb
 * @since 2.3
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationWebsocketTransfer<T> {

    @Nullable
    private Application scope;

    private String type;

    private T data;

}
