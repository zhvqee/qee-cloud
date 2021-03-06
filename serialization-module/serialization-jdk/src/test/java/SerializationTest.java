import org.qee.cloud.serialization.api.ObjectInput;
import org.qee.cloud.serialization.api.ObjectOutput;
import org.qee.cloud.serialization.api.Serialization;
import org.qee.cloud.serialization.concretion.jdk.JdkSerialization;
import lombok.Data;
import lombok.ToString;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * @ProjectName: qee-cloud
 * @Package: PACKAGE_NAME
 * @ClassName: SerializationTest
 * @Description:
 * @Date: 2021/11/16 8:02 下午
 * @Version: 1.0
 */
public class SerializationTest {

    @Data
    @ToString
    static class ObjectValue implements Serializable {
        private Long id;

        private String key;

        private Object value;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        Serialization serialization = new JdkSerialization();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutput objectOutput = serialization.serialize(outputStream);
        ObjectValue objectValue = new ObjectValue();
        objectValue.setId(1L);
        objectValue.setKey("test");
        objectValue.setValue("abcd");
        objectOutput.writeObject(objectValue);
        objectOutput.flushBuffer();
        byte[] bytes = outputStream.toByteArray();

        ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
        ObjectInput deserilize = serialization.deserilize(inputStream);
        ObjectValue objectValue1 = deserilize.readObject(ObjectValue.class);
        System.out.println(objectValue1);

    }
}
