package org.noear.solon.admin.server.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationWebsocketTransfer<T> {

    private String type;

    private T data;

}
