package test.com.ido.utils.download;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * HttpUtil封装，异步连接池模式
 */
public class HttpUtil {
    /**
     * Http连接超时时间
     */
    private static final int minCONNECT_TIMEOUT = 3000;
    /**
     * Http 写入超时时间
     */
    private static final int minWRITE_TIMEOUT = 3000;
    /**
     * Http Read超时时间
     */
    private static final int minREAD_TIMEOUT = 3000;
    /**
     * Http Async Call Timeout
     */
    private static final int minCall_TIMEOUT = 3000;
    /**
     * Http连接池
     */
    private static final int connectionPoolSize = 1000;
//    /**
//     * 静态Http请求池
//     */
//    private static OkHttpClient client=null;
    /**
     * 静态连接池对象
     */
    private static ConnectionPool mConnectionPool = new ConnectionPool(connectionPoolSize, 30, TimeUnit.MINUTES);
    //executor = new ThreadPoolExecutor(0, 2147483647, ...这个是静态的，21亿连接数量
//    private static RealConnectionPool mConnectionPool=new RealConnectionPool(connectionPoolSize, 30, TimeUnit.MINUTES);
    /**
     * ContentType
     */
    private static final String ContentType = "application/json;charset=utf-8";
    /**
     * AcceptType
     */
    private static final String AcceptType = "application/json;charset=utf-8";
    /**
     * Content-Type
     */
    private static final MediaType MediaType_ContentType = MediaType.parse(ContentType);

    /**
     * 获取Http Client对象
     * 降低连接时间
     *
     * @return
     */
    public static OkHttpClient getHttpClient() {
        OkHttpClient client = new OkHttpClient.Builder()
//                .addInterceptor(new RetryIntercepter(3))//重试3次
//                .addInterceptor(new GzipRequestInterceptor())//gzip压缩
                .connectTimeout(minCONNECT_TIMEOUT, TimeUnit.MILLISECONDS) //连接超时
                .readTimeout(minREAD_TIMEOUT, TimeUnit.MILLISECONDS) //读取超时
                .writeTimeout(minWRITE_TIMEOUT, TimeUnit.MILLISECONDS) //写超时
                // okhttp默认使用的RealConnectionPool初始化线程数==2147483647，在服务端会导致大量线程TIMED_WAITING
                //ThreadPoolExecutor(0, 2147483647, 60L, TimeUnit.SECONDS, new SynchronousQueue(), Util.threadFactory("OkHttp ConnectionPool", true));
                .connectionPool(mConnectionPool)
                .build();
        return client;
    }

}
