package labs.annoTest;

/**
 * @author noear 2023/10/19 created
 */
public class AnnoTest {
    public static void main(String[] args){
        //有
        System.out.println(CrudDao.class.getAnnotations());
        //没有（接口，不支持继承过来）
        System.out.println(JpaDao.class.getAnnotations());
    }
}
