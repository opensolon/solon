package org.noear.solon.admin.client.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationWebsocketTransfer<T> {

    @Nullable
    private Application scope;

    private String type;

    private T data;

}
