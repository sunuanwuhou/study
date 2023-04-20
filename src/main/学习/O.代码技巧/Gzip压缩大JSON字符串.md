```java
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipUtils {
    
    /**
     * 压缩字符串为Gzip格式
     * 
     * @param str 待压缩字符串
     * @return 压缩后的字符串
     * @throws IOException
     */
    public static String compress(String str) throws IOException {
        if (str == null || str.length() == 0) {
            return str;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(str.getBytes("UTF-8"));
        gzip.close();
        return out.toString("ISO-8859-1");
    }

    /**
     * 解压Gzip格式字符串
     * 
     * @param compressedStr 压缩后的字符串
     * @return 解压后的字符串
     * @throws IOException
     */
    public static String decompress(String compressedStr) throws IOException {
        if (compressedStr == null || compressedStr.length() == 0) {
            return compressedStr;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(compressedStr.getBytes("ISO-8859-1"));
        GZIPInputStream gzip = new GZIPInputStream(in);
        byte[] buffer = new byte[1024];
        int n;
        while ((n = gzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        return out.toString("UTF-8");
    }
}

```

