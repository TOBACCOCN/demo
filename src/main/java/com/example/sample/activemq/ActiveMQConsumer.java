package com.example.sample.activemq;

import com.example.sample.util.ErrorPrintUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

@Slf4j
public class ActiveMQConsumer {

    // private static Logger logger = LoggerFactory.getLogger(ActiveMQConsumer.class);


    public static void main(String[] args) throws JMSException {
        String url = "tcp://localhost:61616";
        Session session = getSession(url);

        String topicName = "topic_test";
        generateConsumer(session, session.createTopic(topicName));

        String queueName = "queue_test";
        generateConsumer(session, session.createQueue(queueName));
    }

    private static Session getSession(String url) throws JMSException {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.start();
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    private static void generateConsumer(Session session, Destination destination) throws JMSException {
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(message -> {
            TextMessage textMessage = (TextMessage) message;
            try {
                log.info(">>>>> RECEIVING MESSAGE: {}", textMessage.getText());
            } catch (JMSException e) {
                ErrorPrintUtil.printErrorMsg(log, e);
            }
        });
    }
}
