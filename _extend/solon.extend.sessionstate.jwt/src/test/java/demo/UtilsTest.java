package demo;

import io.jsonwebtoken.Claims;
import org.junit.Test;
import org.noear.snack.ONode;
import org.noear.solon.extend.sessionstate.jwt.JwtUtils;

/**
 * @author noear 2021/2/14 created
 */
public class UtilsTest {
    @Test
    public void test1() {
        String token;
        Claims claims;

        token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTZXNzaW9uIHN0YXRlIiwicHVpZCI6MiwiYmNmZG9ja19wdWlkIjoyLCJ1c2VyX2lkIjoieGlleXVlamlhIiwidXNlcl9uYW1lIjoi6LCi5pyI55SyIiwiaXNzIjoiU29sb24iLCJleHAiOjE2MTMzMTQyMzEsImlhdCI6MTYxMzMwNzAzMSwianRpIjoiN2FiOTI4ZWQyZmUyNGQyNWE4MGUzN2I1YjI0YzRjMzMiLCJiY2Zkb2NrX1ZhbGlkYXRpb25fU3RyaW5nIjoiNnplMyJ9.pMUsiaeQA81_UV9zaacQUNPdYEtShiuf0NxVXLp78ZM";
        claims = JwtUtils.parseJwt(token);

        System.out.println(claims);


        token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTZXNzaW9uIHN0YXRlIiwid2F0ZXJhZG1pbl9WYWxpZGF0aW9uX1N0cmluZyI6InhkemEiLCJpc3MiOiJTb2xvbiIsImV4cCI6MTYxMzMxNDEyNCwiaWF0IjoxNjEzMzA2OTI0LCJqdGkiOiJhZGFmZjhhZDYxNTQ0NWMwODI2OTQ5YzI1YjQ5YzFlZCJ9.r6sLdO7yyptP672ZzbomdCr0qdQ979dG0qbWQbFtGi0";
        claims = JwtUtils.parseJwt(token);

        System.out.println(claims);
    }

    @Test
    public void test2() {
        String token;
        Claims claims;

        token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTZXNzaW9uIHN0YXRlIiwid2F0ZXJhZG1pbl9WYWxpZGF0aW9uX1N0cmluZyI6InhkemEiLCJpc3MiOiJTb2xvbiIsImV4cCI6MTYxMzMxNDIzMSwiaWF0IjoxNjEzMzA3MDMxLCJqdGkiOiIzZTRiYzU5ZjEzZTc0ZTI4YTc5MzYyYWY0ZjE2N2E2YSJ9.3GMugBwaGAEekLlS3assnshRw43Vm37_WGNqzBUJIiE";
        claims = JwtUtils.parseJwt(token);

        System.out.println(claims);


        token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJTZXNzaW9uIHN0YXRlIiwicHVpZCI6MiwiYmNmZG9ja19wdWlkIjoyLCJ1c2VyX2lkIjoieGlleXVlamlhIiwidXNlcl9uYW1lIjoi6LCi5pyI55SyIiwiaXNzIjoiU29sb24iLCJleHAiOjE2MTMzMTQyMzEsImlhdCI6MTYxMzMwNzAzMSwianRpIjoiZjZmOWI5NGVlYWE3NDVkZDg4M2ZiMWQ2YmJkZDg0MDUiLCJiY2Zkb2NrX1ZhbGlkYXRpb25fU3RyaW5nIjoiNnplMyJ9.uGwodRkIzimUEo4HxR09fpvyusA_knRcX-9JKvkCyLU";
        claims = JwtUtils.parseJwt(token);

        System.out.println(claims);
    }

    @Test
    public void test3() {
        String token;
        Claims claims;

        token = "eyJhbGciOiJIUzI1NiJ9.eyJ3YXRlcmFkbWluX1ZhbGlkYXRpb25fU3RyaW5nIjoiZGM4cSIsImlzcyI6IlNvbG9uIiwiZXhwIjoxNjEzMzE3Mjk3LCJpYXQiOjE2MTMzMTAwOTcsImp0aSI6IjhhNzFjYzhjZTZmODQ3Mjg4ZjgyMjBmMjY2Nzc4ZTU1In0.FUJKxqCP6HZzYLhnf1FdiQBA1HFSEjJiYdXx0s838m4";
        claims = JwtUtils.parseJwt(token);

        System.out.println(ONode.stringify(claims));


        token = "eyJhbGciOiJIUzI1NiJ9.eyJ3YXRlcmFkbWluX1ZhbGlkYXRpb25fU3RyaW5nIjoicnNqaCIsInB1aWQiOjIsImJjZmRvY2tfcHVpZCI6MiwidXNlcl9pZCI6InhpZXl1ZWppYSIsInVzZXJfbmFtZSI6IuiwouaciOeUsiIsImlzcyI6IlNvbG9uIiwiZXhwIjoxNjEzMzE3MzA0LCJpYXQiOjE2MTMzMTAxMDQsImp0aSI6IjMyZWQ5NDZmNTMzNDRmNTg4ZGM0NzUzNGUwMGYxM2Y1IiwiYmNmZG9ja19WYWxpZGF0aW9uX1N0cmluZyI6InVkdjMifQ.mNrGf_qujUa8I_jkIxXvx9UXF9oEv0WEo8sF3-ZQRu8";
        claims = JwtUtils.parseJwt(token);

        System.out.println(ONode.stringify(claims));


        token = "eyJhbGciOiJIUzI1NiJ9.eyJ3YXRlcmFkbWluX1ZhbGlkYXRpb25fU3RyaW5nIjoicnNqaCIsInB1aWQiOjIsImJjZmRvY2tfcHVpZCI6MiwidXNlcl9pZCI6InhpZXl1ZWppYSIsInVzZXJfbmFtZSI6IuiwouaciOeUsiIsImlzcyI6IlNvbG9uIiwiZXhwIjoxNjEzMzE3MzA0LCJpYXQiOjE2MTMzMTAxMDQsImp0aSI6ImExMzYxZWNmZGUzNzRmODhhMmExYzBmODE5Zjk0OGViIiwiYmNmZG9ja19WYWxpZGF0aW9uX1N0cmluZyI6InVkdjMifQ.9l6Kfuotab-5t_05RFGNtB2slO9059V8mcQWhTn5IgI";
        claims = JwtUtils.parseJwt(token);

        System.out.println(ONode.stringify(claims));


        token = "eyJhbGciOiJIUzI1NiJ9.eyJ3YXRlcmFkbWluX1ZhbGlkYXRpb25fU3RyaW5nIjoieWZhdSIsImlzcyI6IlNvbG9uIiwiZXhwIjoxNjEzMzE1OTgwLCJpYXQiOjE2MTMzMDg3ODAsImp0aSI6Ijc1MDFlZTc3NmQyNjQ4YjViMGMyZTU4Yjg4YzMyMjQ5In0.2wSD9ECuLFpoubL4CwryiYZJcENSCC-S2KPhp4vDxH8";
        claims = JwtUtils.parseJwt(token);

        System.out.println(ONode.stringify(claims));
    }
}
