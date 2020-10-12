package net.hasor.solon.boot;
import com.alibaba.fastjson.JSONObject;
import net.hasor.core.AppContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.XInject;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(WebBootEnableHasor_1.class)
public class WebBootEnableHasor_1_Test extends HttpTestBase {
    @XInject
    private AppContext            appContext;
    //private MockMvc               mockMvc;

//    public MockMvc mockMvc() throws Exception {
//        if (mockMvc != null) {
//            return mockMvc;
//        }
//        RuntimeListener runtimeListener = new RuntimeListener(this.appContext);
//        RuntimeFilter runtimeFilter = new RuntimeFilter(this.appContext);
//        //
//        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)//
//                .addFilter(runtimeFilter, "/*")//
//                .build();
//        //
//        ServletContext servletContext = mockMvc.getDispatcherServlet().getServletContext();
//        runtimeListener.contextInitialized(new ServletContextEvent(servletContext));
//        runtimeFilter.init(new OneConfig("abc", () -> appContext));
//        //
//        return mockMvc;
//    }

    @Test
    public void login() throws Exception {
        String contentAsString = path("/hello").bodyTxt("{}","application/json").post();

//        String contentAsString = mockMvc().perform(MockMvcRequestBuilders//
//                .post("/hello")//
//                .contentType(MediaType.APPLICATION_JSON)//
//                .content("{}"))//
//                .andDo(mvcResult -> {
//                    MockMvcResultHandlers.print();
//                }).andExpect(MockMvcResultMatchers.status().isOk())//
//                .andReturn()//
//                .getResponse()//
//                .getContentAsString();
        //
        JSONObject jsonObject = JSONObject.parseObject(contentAsString);
        assert jsonObject.getBoolean("spring");
        assert jsonObject.getString("message").equals("HelloWord");
    }
}
