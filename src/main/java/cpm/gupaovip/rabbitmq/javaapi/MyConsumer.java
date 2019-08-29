package cpm.gupaovip.rabbitmq.javaapi;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * ClassName:MyConsumer
 * Package:cpm.gupaovip.rabbitmq.javaapi
 * description
 * Created by zhangbin on 2019/8/28.
 *
 * @author: zhangbin q243132465@163.com
 * @Version 1.0.0
 * @CreateTime： 2019/8/28 19:53
 */
public class MyConsumer {
    private final static String DLX_EXCHANGE = "DEAD_EXCHANGE";
    private final static String QUEUE_NAME = "SIMPLE_QUEUE";
    private final static String TEST_DLX_QUEUE = "TEST_DLX_QUEUE";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.5.179");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setUsername("guest");
        factory.setPassword("guest");
        // 设置交换机,队列
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        // 指定队列的死信交换机
        Map<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", "DLX_EXCHANGE");
        // arguments.put("x-expires","9000"); // 设置队列的TTL
        // 声明死信交换机
        //String exchange (交换机), String type (类型 ), boolean durable (持久化), boolean autoDelete (自动删除),Map<String, Object> arguments
        channel.exchangeDeclare(DLX_EXCHANGE, "topic", false, false, null);
        // String  type：交换机的类型，direct (指定),  topic(),  fanout  中的一种。
        //   声明队列
        //String queue (队列名), boolean durable (持久化), boolean exclusive (单个连接的独占队列), boolean autoDelete (自动删除), Map<String, Object> arguments (队列的其他属性（构造参数）)
        channel.queueDeclare(QUEUE_NAME, false, false, false, arguments);

        // 声明死信队列
        channel.queueDeclare(TEST_DLX_QUEUE,false,false,false,null);
        System.out.println("Waiting for message....");



        // 绑定交换机
        channel.queueBind(TEST_DLX_QUEUE, DLX_EXCHANGE, "#");

        DefaultConsumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String msg = new String(body, "UTF-8");
                System.out.println("receive msg is : " + msg);
                System.out.println("ConsumerTag : " + consumerTag);
                System.out.println("deliveryTag : " + envelope.getDeliveryTag());
            }
        };

        // 开始获取消息
        channel.basicConsume(TEST_DLX_QUEUE, true, consumer);
    }
}
