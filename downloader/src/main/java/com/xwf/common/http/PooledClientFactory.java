package com.xwf.common.http;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

import java.util.logging.Level;

public class PooledClientFactory {
    private final static PooledClientFactory instance = new PooledClientFactory();
    //另外一种方案或许更为合适——对象池化技术。
    //基于Apache的commons-pool池
    private final GenericObjectPool clientPool = new GenericObjectPool();

    public static PooledClientFactory getInstance() {
        return instance;
    }

    public PooledClientFactory() {

        //实现对象池的对象创建工厂接口
        clientPool.setFactory(new PoolableObjectFactory() {
            // 创建对象实例，用于填充对象池。同时可以分配这个对象适用的资源。  

            public Object makeObject() throws Exception {
                log.info("为线程 [ " + Thread.currentThread().getName() +
                        " ] 创建新的WebClient实例!");

                //构造一个webClient 模拟Chrome 浏览器
                WebClient webClient = new WebClient(BrowserVersion.CHROME);
                //屏蔽日志信息
                LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
                        "org.apache.commons.logging.impl.NoOpLog");

                java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit")
                        .setLevel(Level.OFF);

                java.util.logging.Logger.getLogger("org.apache.commons.httpclient")
                        .setLevel(Level.OFF);
                //支持JavaScript
                webClient.getOptions().setJavaScriptEnabled(false);
                webClient.getOptions().setCssEnabled(false);
                webClient.getOptions().setActiveXNative(false);
                webClient.getOptions().setCssEnabled(false);
                webClient.getOptions().setThrowExceptionOnScriptError(false);
                webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
                webClient.getOptions().setTimeout(10000);


                //设置一个运行JavaScript的时间
//                webClient.waitForBackgroundJavaScript(5000);
                webClient.getOptions().setJavaScriptEnabled(true);

                return webClient;
            }

            // 销毁对象,销毁对象池时被调用,连接池调用invalidateObject(obj)时被调用 

            public void destroyObject(Object arg0) throws Exception {
                log.info("销毁对象：" + arg0);
                WebClient client = (WebClient) arg0;
                client.close();
                client = null;
            }

            //  查询对象有效性,需要对象池设置setTestOnBorrow(true),无效对象将被destroy

            public boolean validateObject(Object arg0) {
                log.info("检查对象有效性：" + arg0);
                return true;
            }

            // 激活一个对象,从对象池获取对象时被调用  


            public void activateObject(Object arg0) throws Exception {
                log.info("激活对象：" + arg0);
            }

            // 挂起(钝化)一个对象,将对象还给对象池时被调用

            public void passivateObject(Object arg0) throws Exception {

                log.info("挂起对象：" + arg0);
            }

        });

        clientPool.setTestOnBorrow(true);
        //借出对象达到最大值的最大等待时间,5s等待时间过后抛出异常
        //clientPool.setMaxWait(5000);
        //设置最大可借出数量,默认为8
        clientPool.setMaxActive(10);
    }


    public WebClient getClient() {
        try {
            return (WebClient) this.clientPool.borrowObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public void returnClient(WebClient client) {
        try {
            this.clientPool.returnObject(client);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //测试对象池
    public static void main(String[] args) {
        try {
            //CursorableLinkedList
            //得到池中空闲的对象数量,如果不可用返回负数
            log.info(PooledClientFactory.getInstance().clientPool.getNumIdle());

            //取出对象1
            Object obj1 = PooledClientFactory.getInstance().getClient();
            //取出对象2
            Object obj2 = PooledClientFactory.getInstance().getClient();
            //取出对象3
            Object obj3 = PooledClientFactory.getInstance().getClient();
            //如果对象借出达到最大数量MaxActive,程序会一直等待有可用的对象(归还的),也可以通过DEFAULT_MAX_WAIT设置等待时间，默认为-1一直等待
            PooledClientFactory.getInstance().getClient();
            PooledClientFactory.getInstance().getClient();
            PooledClientFactory.getInstance().getClient();
            PooledClientFactory.getInstance().getClient();
            PooledClientFactory.getInstance().getClient();
            PooledClientFactory.getInstance().getClient();
            //归还对象1


            PooledClientFactory.getInstance().returnClient((WebClient) obj1);

            //得到池中空闲的对象数量
            log.info(PooledClientFactory.getInstance().clientPool.getNumIdle());
            //    返回从池中借出的对象数量
            log.info(PooledClientFactory.getInstance().clientPool.getNumActive());

            //最大可借出数量
            log.info(PooledClientFactory.getInstance().clientPool.getMaxActive());
            //最大空闲数量
            log.info(PooledClientFactory.getInstance().clientPool.getMaxIdle());
            //最小空闲数量
            log.info(PooledClientFactory.getInstance().clientPool.getMinIdle());


            PooledClientFactory.getInstance().clientPool.clear();
            PooledClientFactory.getInstance().clientPool.close();

            //使用工厂创建一个对象
            PooledClientFactory.getInstance().clientPool.getMaxActive();
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

   static class log {
       static void info(Object info) {
            System.out.println(info.toString());
        }
    }
}