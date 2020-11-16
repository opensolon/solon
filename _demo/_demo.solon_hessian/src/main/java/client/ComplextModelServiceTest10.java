package client;

import client.dso.FairyConfigurationImp;
import org.noear.fairy.FairyConfigurationDefault;
import org.noear.fairy.annotation.EnableFairyClient;
import org.noear.fairy.annotation.FairyClient;
import org.noear.solon.SolonApp;
import org.noear.solon.annotation.Component;
import org.noear.solon.core.Aop;
import server.dso.IComplexModelService;
import server.model.ComplexModel;
import server.model.Person;
import server.model.Point;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@EnableFairyClient
@Component
public class ComplextModelServiceTest10 {

    public static void main(String[] args) throws Exception {
        FairyConfigurationDefault.proxy = new FairyConfigurationImp();

        SolonApp.start(ComplextModelServiceTest10.class, args, app -> {
            app.enableHttp(false);
            app.enableWebSocket(false);
            app.enableSocket(false);
        });

        ComplextModelServiceTest10 test5 = Aop.get(ComplextModelServiceTest10.class);
        test5.test();
    }

    @FairyClient
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
