package webapp.dso.service;

import org.noear.solon.annotation.Inject;
import org.noear.solon.extend.aspect.annotation.Service;
import org.noear.solon.data.annotation.Tran;
import org.noear.solon.data.tran.TranPolicy;
import webapp.dso.mapper.AppxMapper;

@Service
public class AppService {
    @Inject
    AppxMapper sqlMapper1;

    public Object getApp(int app_id) throws Exception {
        return sqlMapper1.appx_get2(app_id);
    }

    public Object getApp(String app_id) throws Exception {
        return sqlMapper1.appx_get2(Integer.parseInt(app_id));
    }

    public void addApp(){
        sqlMapper1.appx_add();
    }


    @Tran
    public void addApp2(){
        sqlMapper1.appx_add();
    }

    @Tran(policy = TranPolicy.nested)
    public void addApp3(){
        sqlMapper1.appx_add();
    }

    @Tran(policy = TranPolicy.requires_new)
    public void addApp4(){
        sqlMapper1.appx_add();
    }

    @Tran(policy = TranPolicy.never)
    public void addApp5(){
        sqlMapper1.appx_add();
    }

    @Tran(policy = TranPolicy.mandatory)
    public void addApp6(){
        sqlMapper1.appx_add();
    }
}
