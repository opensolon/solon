package labs;


import org.noear.solon.web.vertx.ResponseOutputStream;

/**
 * @author noear 2024/8/23 created
 */
public class OutputStreamTest {
    public static void main(String[] args) throws Exception{
        ResponseOutputStream out = new ResponseOutputStream(null, 2);

        for(int i=0; i<20; i++){
            out.write(i);
        }
    }
}
