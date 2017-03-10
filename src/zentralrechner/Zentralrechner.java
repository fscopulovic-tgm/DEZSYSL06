package zentralrechner;

import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Zentralrechner {

    private static String user = ActiveMQConnection.DEFAULT_USER;
    private static String password = ActiveMQConnection.DEFAULT_PASSWORD;
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

    private List<String> parkrechnerliste;
    private String xml;

    public Zentralrechner(String[] parks) {
        this.parkrechnerliste = new ArrayList<>();
        this.xml = "";
        try {
            this.parkrechnerliste.add(parks[0]);
            //Wird später wiedereingefügt
            /*this.addParkrechner(parks[0]);
            this.addParkrechner(parks[1]);
            this.getParkrechner();*/
            this.getQueueMessages(parks[0]);
            this.writeXML();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //Wird später wieder eingefügt
    /*public void addParkrechner(String parkrechner) throws Exception {
        PrintWriter out = new PrintWriter("parkconfig.txt");
        for(String park : this.parkrechnerliste)
            out.println(park);
        out.println(parkrechner);
        out.close();
    }

    public void getParkrechner() throws Exception {
        RandomAccessFile config = new RandomAccessFile("parkconfig.txt", "r");
        String next = "";
        while ((next = config.readLine()) != null)
            this.parkrechnerliste.add(next);
        config.close();
    }*/

    public void getQueueMessages(String subject) throws Exception {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
        Connection connection = connectionFactory.createConnection();
        connection.start();

        // Create the session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createTopic(subject);

        // Create the consumer
        MessageConsumer consumer = session.createConsumer(destination);

        // Start receiving
        TextMessage message = (TextMessage) consumer.receiveNoWait();
        System.out.println(message.getText());
        if (message != null) {
            System.out.println(message.getText());
            this.xml += "\n" + message.getText();
            message.acknowledge();
        }
        connection.stop();

        consumer.close();
        session.close();
        connection.close();
    }

    public void notifyAllParkrechner(Boolean done) {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
            Connection connection = connectionFactory.createConnection();
            connection.start();

            // Create the session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createTopic("notification");

            // Create the producer.
            MessageProducer producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

            // Create the message
            TextMessage message;
            if (done) {
                message = session.createTextMessage("Done");
            } else {
                message = session.createTextMessage("Something went wrong");
            }
            producer.send(message);
            System.out.println(message.getText());
            connection.stop();

            producer.close();
            session.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeXML() throws Exception {
        System.out.println("In XML-File schreiben");
        PrintWriter out = new PrintWriter("zentralrechner.xml");
        out.println(this.xml);
        out.close();
    }
}
