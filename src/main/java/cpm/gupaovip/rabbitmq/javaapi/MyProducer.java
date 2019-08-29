package cpm.gupaovip.rabbitmq.javaapi;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * ClassName:MyProducer
 * Package:cpm.gupaovip.rabbitmq.javaapi
 * description
 * Created by zhangbin on 2019/8/28.
 *
 * @author: zhangbin q243132465@163.com
 * @Version 1.0.0
 * @CreateTime： 2019/8/28 17:22
 */
public class MyProducer {
    private final static String EXCHANGE_NAME="SIMPLE_EXCHANGE";
    private final static String TEST_TTL_QUEUE = "SIMPLE_QUEUE";
    private final static String QUEUE_NAME = "SIMPLE_QUEUE";
    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        // 连接IP
        factory.setHost("192.168.5.179");
        // 连接端口
        factory.setPort(5672);
        // vhost
        factory.setVirtualHost("/");
        // 用户
        factory.setUsername("guest");
        // 密码
        factory.setPassword("guest");
        // 建立连接
        Connection connection = factory.newConnection();
        // 创建消息通道
        Channel channel = connection.createChannel();
        String msg="Hello word ,second rabbitMQ";

        AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                .deliveryMode(2) // 持久化消息
                .contentEncoding("UTF-8")
                .expiration("10000") // TTL，10秒后没有被消费则被发送到DLX
                .build();
        channel.basicPublish("",QUEUE_NAME,properties,msg.getBytes());
        channel.close();
        connection.close();
    }

}
