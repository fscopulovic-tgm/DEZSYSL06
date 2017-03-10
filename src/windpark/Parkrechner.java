package windpark;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URL;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

public class Parkrechner {

    private List<HashMap<String, Integer>> auslesen;
    @XStreamImplicit
    private List<Windkraftanlage> windkraftanlagen;

    private List<Integer> ports;
    private static final String host = "localhost";

    private static String user = ActiveMQConnection.DEFAULT_USER;
    private static String password = ActiveMQConnection.DEFAULT_PASSWORD;
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

    private String name;
    private String xml;

    public Parkrechner(ArrayList<Integer> p, String name) {
        this.name = name;
        this.auslesen = new ArrayList<>();
        this.windkraftanlagen = new ArrayList<>();
        this.ports = p;
        this.xml = "";

        try {
            this.startParkrechner();
            this.sendInQueue();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void startParkrechner() throws Exception{
        for(Integer port : this.ports) {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL("http://"+ host + ":" + port.intValue() + "/xmlrpc") );
            XmlRpcClient client = new XmlRpcClient();
            client.setConfig(config);
            Object[] params = new Object[0];
            this.auslesen.add((HashMap<String, Integer>) client.execute("Windkraft.getData", params));
        }
        this.printAnlage();
        this.toXML();
    }

    private void printAnlage() {
        int i = 0;
        System.out.println("Daten zu der/den Windkraftanlage/n");
        for(HashMap<String, Integer> wka : this.auslesen) {
            int as = ((Integer) wka.get("aktStrom")).intValue();
            int bs = ((Integer) wka.get("blindstrom")).intValue();
            int wg = ((Integer) wka.get("windgesch")).intValue();
            int d = ((Integer) wka.get("drehzahl")).intValue();
            int t = ((Integer) wka.get("temp")).intValue();
            int bp = ((Integer) wka.get("blattpos")).intValue();

            System.out.println("Windkraftanlage Nr. " + Integer.toString(i) +
                    "\nAktuelle Stromerzeugung: " + Integer.toString(as) + " Watt" +
                    "\nBlindstrom: " + Integer.toString(bs) + " Watt" +
                    "\nWindgeschwindigkeit: " + Integer.toString(wg) + " Meter pro Sekunde" +
                    "\nDrehzahl: " + Integer.toString(d) + " Runden pro Minute" +
                    "\nTemperatur: " + Integer.toString(t) + " Grad Celsius" +
                    "\nBlattposition: "+ Integer.toString(bp) + " Grad");
            i++;
            this.windkraftanlagen.add(new Windkraftanlage(this.name+"/Windkraftanlage"+Integer.toString(i), as, bs, wg, d, t, bp));
        }
    }

    public void toXML() {
        this.xml = "";
        XStream xstream = new XStream();

        this.xml = xstream.toXML(this.windkraftanlagen);

        System.out.println("Ausgabe in XML-Format");
        System.out.println(this.xml);
        try {
            PrintWriter out = new PrintWriter(this.name+".xml");
            out.println(this.xml);
            out.close();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    // Method that works with the activemq
    public void sendInQueue() throws Exception{
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
        Connection connection = connectionFactory.createConnection();
        connection.start();

        // Create the session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createTopic(this.name);

        // Create the producer.
        MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT );

        // Create the message
        // Fürs debugen
        System.out.println("\nVor dem Senden" + this.xml);
        TextMessage message = session.createTextMessage(this.xml);
        // Fürs debugen
        System.out.println("\nNachdem die Message erstellt wurde:\n" + message.getText());
        producer.send(message);
        // Fürs debugen
        System.out.println("Gesendete Message: " + message.getText());
        connection.stop();

        producer.close();
        session.close();
        connection.close();
    }

    //Wird später gebraucht
    public void getNotification() throws Exception {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, url);
        Connection connection = connectionFactory.createConnection();
        connection.start();

        // Create the session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destination = session.createTopic("notification");

        // Create the consumer
        MessageConsumer consumer = session.createConsumer(destination);

        // Start receiving
        TextMessage message = (TextMessage) consumer.receive();
        if (message != null) {
            System.out.println("Message received: " + message.getText());
            this.xml += "\n" + message.getText();
            message.acknowledge();
        }
        connection.stop();

        consumer.close();
        session.close();
        connection.close();
    }
}

