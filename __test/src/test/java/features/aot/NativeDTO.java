package features.aot;

import java.io.Serializable;

/**
 * @author songyinyin
 * @since 2023/4/7 18:49
 */
public class NativeDTO {

    private String name;

    static class NativeDTO2 implements Serializable {

        private String subName;
    }
}
