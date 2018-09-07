package com.xwf.common.utils.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.concurrent.*;

/**
 * Created by 15600 on 2017/9/6.
 */
public class SshUtil {
    private ChannelExec openChannel;
    private Session session = null;
    private int timeout = 60000;

    private LinkedBlockingQueue queue;
    private ExecutorService service;

    private boolean start = true;


    private static volatile SshUtil uniqueInstance;

    private SshUtil() throws JSchException {
        SshConfiguration conf = new SshConfiguration();
        conf.setHost("66.42.74.204");
        conf.setUserName("root");
        conf.setPassword("{4aXQWxg7A57.9}h");
        conf.setPort(22);

        JSch jSch = new JSch(); //创建JSch对象
        session = jSch.getSession(conf.getUserName(), conf.getHost(), conf.getPort());//根据用户名，主机ip和端口获取一个Session对象
        session.setPassword(conf.getPassword()); //设置密码
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);//为Session对象设置properties
        session.setTimeout(timeout);//设置超时
        session.connect();//设置连接的超时时间


        queue = new LinkedBlockingQueue<String>();

        service = Executors.newCachedThreadPool();
        start = true;

        service.submit(new Runnable() {
            public void run() {

                while (start)

                    try {
                        Thread.sleep(1000);

                        String cmd = (String) queue.poll();
                        if (cmd == null) {
                            Thread.sleep(1000);
                            continue;
                        }


                        StringBuffer sb = new StringBuffer();
                        openChannel = (ChannelExec) session.openChannel("exec");
                        openChannel.setCommand(cmd); //执行命令
                        openChannel.setPty(true);//虚拟终端？？？


                        int exitStatus = openChannel.getExitStatus(); //退出状态为-1，直到通道关闭
                        System.out.println(exitStatus);

                        // 下面是得到输出的内容
                        openChannel.connect();
                        InputStream in = openChannel.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        String buf = null;
                        while ((buf = reader.readLine()) != null) {
                            sb.append(buf + "\n");
                        }

                        System.out.println(sb.toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

            }
        });


    }

    public static synchronized SshUtil getInstance() throws JSchException {
        if (uniqueInstance == null) {
            synchronized (SshUtil.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new SshUtil();
                }
            }
        }
        return uniqueInstance;
    }


    public void exe(String command) throws Exception {

        queue.add(command);




        /*StringBuffer sb = new StringBuffer();
        openChannel = (ChannelExec) session.openChannel("exec");
        openChannel.setCommand(command); //执行命令
        openChannel.setPty(true);//虚拟终端？？？


        int exitStatus = openChannel.getExitStatus(); //退出状态为-1，直到通道关闭
        System.out.println(exitStatus);

        // 下面是得到输出的内容
        openChannel.connect();
        InputStream in = openChannel.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String buf = null;
        while ((buf = reader.readLine()) != null) {
            sb.append(buf + "\n");
        }

        return sb.toString();*/

    }


    public void close() {
        session.disconnect();
        service.shutdown();
        queue.clear();
        start = false;
        uniqueInstance = null;

    }

    public static void main(String[] args) {

        try {

            SshUtil sshUtil = SshUtil.getInstance();

            sshUtil.exe("ls");

            sshUtil.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
