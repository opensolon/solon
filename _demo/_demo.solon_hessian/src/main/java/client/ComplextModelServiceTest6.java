package client;

import org.noear.fairy.Fairy;
import org.noear.fairy.channel.SocketChannel;
import org.noear.fairy.decoder.ProtobufDecoder;
import org.noear.fairy.encoder.ProtobufEncoder;
import server.dso.IComplexModelService;
import server.model.ComplexModel;
import server.model.Person;
import server.model.Point;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ComplextModelServiceTest6 {
    public static void main(String[] args) throws Exception {
        //配置接口代理
        IComplexModelService service =  Fairy.builder()
                .channel(SocketChannel.instance)
                .encoder(ProtobufEncoder.instance)
                .decoder(ProtobufDecoder.instance)
                .upstream(()->{
            return "s://localhost:28080";
        }).create(IComplexModelService.class);


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
        for(Point elem : points1) {
            System.out.println(elem.getX() + "\t" + elem.getY());
        }

    }
}
