package demo;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class Main {
    public static void main(String[] args) throws Exception{
        Student stu = new Student();
        stu.setAddress("屋子科");
        stu.setName("ymz");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Hessian2Output output = new Hessian2Output(os);
        output.writeObject(stu);
        output.close();

        Student.hobby = "drink";

        ByteArrayInputStream bis = new ByteArrayInputStream(os.toByteArray());
        Hessian2Input input = new Hessian2Input(bis);
        Student student = (Student) input.readObject();
        System.out.println(student.getAddress());
        System.out.println(student.getName());
    }
}
