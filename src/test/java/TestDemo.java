import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @Author: xiaoxiang.zhang
 * @Description: 测试文件读取
 * @Date: Create in 11:16 AM 2019/10/26
 */
public class TestDemo {
    @Test
    public void testFileLOad() throws IOException {
        Properties properties = new Properties();
        properties.load(TestDemo.class.getClassLoader().getResourceAsStream("./default.properties"));
        String str = properties.get("defaultSuffix").toString();
        System.err.println(str.contains(".jsp"));
    }

    @Test
    public void testGetAbsoultePath(){
        String path = this.getClass().getClassLoader().getResource("./").getPath();
        System.err.println(path);
    }
}
