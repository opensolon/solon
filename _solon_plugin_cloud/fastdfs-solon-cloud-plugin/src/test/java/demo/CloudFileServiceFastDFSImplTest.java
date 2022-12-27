package demo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.model.Media;
import org.noear.solon.core.handle.Result;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * @author liaocp
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(App.class)
public class CloudFileServiceFastDFSImplTest {

    private File appYmlFile;

    private Media media;

    private String groupName;

    private String remoteFileName;

    private int fileAvailable;

    @Before
    public void init() throws IOException {
        String file = Utils.getResource("app.yml").getFile();
        appYmlFile = new File(file);
        media = new Media(Files.newInputStream(appYmlFile.toPath()));
        fileAvailable = Files.newInputStream(appYmlFile.toPath()).available();
    }

    @Test
    @Before
    public void putFileTest() {
        Result result = CloudClient.file().put(appYmlFile.getName(), media);
        Assert.assertEquals(200, result.getCode());
        String[] r = (String[]) result.getData();
        groupName = r[0];
        remoteFileName = r[1];
    }

    @Test
    public void getFileTest() throws IOException {
        Media newMedia = CloudClient.file().get(groupName, remoteFileName);
        Assert.assertEquals(fileAvailable, newMedia.body().available());
    }

    @Test
    public void deleteFileTest() {
        Result result = CloudClient.file().delete(groupName, remoteFileName);
        Assert.assertEquals(200, result.getCode());
    }
}
