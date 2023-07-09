package org.noear.solon.admin.server.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.noear.solon.lang.Nullable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationWebsocketTransfer<T> {

    @Nullable
    private Application scope;

    private String type;

    private T data;

}
