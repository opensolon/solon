package client;

import client.dso.FairyConfigurationImp;
import client.dso.UpstreamFactoryImp;
import org.noear.fairy.Fairy;
import org.noear.fairy.annotation.EnableFairyClients;
import org.noear.fairy.annotation.FairyClient;
import org.noear.fairy.decoder.SnackDecoder;
import org.noear.fairy.encoder.SnackTypeEncoder;
import org.noear.solon.XApp;
import org.noear.solon.annotation.XBean;
import org.noear.solon.annotation.XController;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XBridge;
import server.dso.IComplexModelService;
import server.model.ComplexModel;
import server.model.Person;
import server.model.Point;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EnableFairyClients
@XBean
public class ComplextModelServiceTest5 {

    public static void main(String[] args) throws Exception {
        //配置接口代理
//        IComplexModelService service =  Fairy.builder()
//                .encoder(SnackTypeEncoder.instance)
//                .decoder(SnackDecoder.instance)
//                .upstream(()->{
//            return "http://localhost:8080";
//        }).create(IComplexModelService.class);

        XBridge.upstreamFactorySet(new UpstreamFactoryImp());

        XApp.start(ComplextModelServiceTest5.class, args, app -> app.enableHttp(false));

        ComplextModelServiceTest5 test5 = Aop.get(ComplextModelServiceTest5.class);
        test5.test();
    }

    @FairyClient(value = "test:/ComplexModelService/", configuration = FairyConfigurationImp.class)
    IComplexModelService service;

    public void test() {
        ComplexModel<Point> model = new ComplexModel<Point>();
        model.setId(1);
        Person person = new Person();
        person.setName("Tom");
        person.setAge(86);
        person.setBirthDay(new Date());
        person.setSensitiveInformation("This should be private over the wire");
        model.setPerson(person);

        List<Point> points = new ArrayList<Point>();
        Point point = new Point();
        point.setX(3);
        point.setY(4);
        points.add(point);

        point = new Point();
        point.setX(100);
        point.setY(100);
        points.add(point);

        //远程方法调用
        model.setPoints(points);
        service.save(model);

        model = service.read(model.getId());
        List<Point> points1 = model.getPoints();
        for (Point elem : points1) {
            System.out.println(elem.getX() + "\t" + elem.getY());
        }
    }
}
