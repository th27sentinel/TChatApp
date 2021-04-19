/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerPackage;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.FindIterable;
import com.mongodb.BasicDBObject;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.MongoClient;

/**
 *
 * @author Tristan
 */
public class Server {

    ////
    public static boolean bCreateAccount = false;
    public static boolean bLogin = false;
    public static boolean bRedo = false;
    public static ObjectId iD = new ObjectId();

    public static boolean bRefresh = false;
    public static boolean bLogout = false;
    ////

    public static ConcurrentHashMap<String, Integer> chm1 = new ConcurrentHashMap<>(); // name (key), index in active list (value)

    public static ConcurrentHashMap<Integer, ClientHandler> chm2 = new ConcurrentHashMap<>(); // index (key) , handler object (value)

    //counter for clients
    static int i = 0;

    public static void main(String[] args) throws IOException {

        //server is listening on port 1234
        ServerSocket ss = new ServerSocket(1234);

        Socket s;

        while (true) {

            s = ss.accept();

            DataInputStream dis = new DataInputStream(s.getInputStream());
            DataOutputStream dos = new DataOutputStream(s.getOutputStream());

            ClientHandler mtch = new ClientHandler(s, "client " + i, dis, dos);

            Thread t = new Thread(mtch);

            // chm2 = index | handler object
            // chm1 = name ("client " + 1) | index in active list (value)
            // collectionM = sender(id) | message | recipient(id)
            // collectionP = username | password | id | name ("client " + 1)
            chm1.put("client " + i, i);
            chm2.put(i, mtch);

            if (i == 0) {
                MongoClient mongo = new MongoClient("localhost", 27017);
                MongoDatabase database = mongo.getDatabase("myDb");


boolean collecExists = false;
                MongoCollection<Document> collectionP = null;
        for (String name : database.listCollectionNames()) {

            if (name.equals("profilesNew")) {
                collecExists = true;
                break;
            }
        }
        if (collecExists == false) {

            database.createCollection("profilesNew");
           
                Document documentP = new Document("Username", "a").append("Password", "a").append("ID", new ObjectId()).append("Designation", null).append("Status", "Offline");
collectionP = database.getCollection("profilesNew");
                collectionP.insertOne(documentP);
 documentP = new Document("Username", "b").append("Password", "b").append("ID", new ObjectId()).append("Designation", null).append("Status", "Offline");

                collectionP.insertOne(documentP);
     
        
        }
        collectionP = database.getCollection("profilesNew");
                FindIterable<Document> cursor = collectionP.find();
                
                for (Document doc : cursor) {

                    collectionP.updateOne(new Document("ID", doc.get("ID", ObjectId.class)),
                            new Document("$set", new Document("Designation", null)));

                    collectionP.updateOne(new Document("ID", doc.get("ID", ObjectId.class)),
                            new Document("$set", new Document("Status", "Offline")));

                }

            }

            t.start();

            i++;

        }

    }

}

class ClientHandler implements Runnable {

    Scanner scanIt = new Scanner(System.in);
    private String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    Socket s;
    boolean isLoggedIn;

    private String username = "";
    private String password = "";

    private ObjectId id = new ObjectId();

    String converseWith = "";
    boolean bConverse = false;

    private String option = "";

    String talkWith;

    public ClientHandler(Socket s, String name, DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isLoggedIn = false;
    }

    @Override
    public void run() {
        

        String received;

        MongoClient mongo = new MongoClient("localhost", 27017);
        MongoDatabase database = mongo.getDatabase("myDb");

        System.out.println("Collection created successfully");
MongoCollection<Document> collectionM = null;
        boolean collecExists = false;
               // MongoCollection<Document> collectionP = null;
        for (String name : database.listCollectionNames()) {

            if (name.equals("messagesNew")) {
                collecExists = true;
                break;
            }
        }
        if (collecExists == false) {

            database.createCollection("messagesNew");
        }
        
     collectionM = database.getCollection("messagesNew");
        boolean collecPExists = false;
                MongoCollection<Document> collectionP = null;
        for (String name : database.listCollectionNames()) {

            if (name.equals("profilesNew")) {
                collecPExists = true;
                break;
            }
        }
        if (collecPExists == false) {

          database.createCollection("profilesNew");
          collectionP = database.getCollection("profilesNew");
           
                Document documentP = new Document("Username", "a").append("Password", "a").append("ID", new ObjectId()).append("Designation", null).append("Status", "Offline");

                collectionP.insertOne(documentP);
 documentP = new Document("Username", "b").append("Password", "b").append("ID", new ObjectId()).append("Designation", null).append("Status", "Offline");

                collectionP.insertOne(documentP);
        
        }
        collectionP = database.getCollection("profilesNew");

        BasicDBObject usernameQuery = new BasicDBObject();
        BasicDBObject recipientQuery = new BasicDBObject();

        try {

            StringTokenizer st = new StringTokenizer(dis.readUTF(), "#");
            if (st.hasMoreTokens()) {
                username = st.nextToken();
            }
            if (st.hasMoreTokens()) {
                password = st.nextToken();
            }
            if (st.hasMoreTokens()) {
                option = st.nextToken();
            }

            if (option.equals("create account")) {
                Document documentP = new Document("Username", username).append("Password", password).append("ID", new ObjectId()).append("Designation", name).append("Status", "Online");

                collectionP.insertOne(documentP);

                option = "login";

            } else {
                option = "login";
            }

            if (option.equals("login")) {

                long foundUsernames = collectionP.countDocuments(new Document("Username", username));
                long foundPasswords = collectionP.countDocuments(new Document("Password", password));

                if ((foundUsernames == 0) || (foundPasswords == 0)) {
                    dos.writeUTF("Invalid input. Please try again...\n");

                    run();
                }
            }

            usernameQuery.put("Username", username);

            FindIterable<Document> cursor = collectionP.find(usernameQuery);

            Iterator it = cursor.iterator();
            for (Document doc : cursor) {
                if (password.equals(doc.get("Password", String.class))) {

                    id = doc.get("ID", ObjectId.class);

                    collectionP.updateOne(new Document("ID", doc.get("ID", ObjectId.class)),
                            new Document("$set", new Document("Designation", name)));

                    collectionP.updateOne(new Document("ID", doc.get("ID", ObjectId.class)),
                            new Document("$set", new Document("Status", "Online")));

                    this.isLoggedIn = true;
                    Server.chm2.get(Server.chm1.get(name)).isLoggedIn = true;

                    MongoCollection<Document> collection = database.getCollection("profilesNew");

                    FindIterable<Document> cursorProfiles = collection.find();

                    Iterator itProfiles = cursorProfiles.iterator();

                }

                dos.writeUTF("true");

                toetsiets();

            }

        } catch (IOException i) {
            //i.printStackTrace();
        } catch (NoSuchElementException n) {
            // n.printStackTrace();
        } catch (NullPointerException nu) {

        }

        while (isLoggedIn == true) {
            try {

                received = dis.readUTF();

                if (received.equals("logout")) {

                    this.isLoggedIn = false;
                    this.s.close();

                    FindIterable<Document> cursorProfiles = collectionP.find();

                    // changes everyones' status field to offline
                    for (Document doc : cursorProfiles) {

                        collectionP.updateOne(new Document("ID", doc.get("ID", ObjectId.class)),
                                new Document("$set", new Document("Designation", null)));

                        collectionP.updateOne(new Document("ID", doc.get("ID", ObjectId.class)),
                                new Document("$set", new Document("Status", "Offline")));

                        break;
                    }

                } else {

                    StringTokenizer st = new StringTokenizer(received, "#");
                    if (st.hasMoreTokens()) {
                        talkWith = st.nextToken().trim();
                    }

                    if (talkWith.length() == 0) {
                        continue;
                    }

                    ObjectId recipientID = new ObjectId();

                    String message = "";

                    BasicDBObject conversationRecipient = new BasicDBObject();

                    conversationRecipient.put("Username", talkWith);

                    FindIterable<Document> cursorRecipient = collectionP.find(conversationRecipient);

                    for (Document doc : cursorRecipient) {

                        recipientID = doc.get("ID", ObjectId.class);
                        break;
                    }

                    BasicDBObject senderReceiver = new BasicDBObject();
                    BasicDBObject receiverSender = new BasicDBObject();

                    senderReceiver.put("$or", new BasicDBObject[]{new BasicDBObject("Sender", id), new BasicDBObject("Recipient", id),
                        new BasicDBObject("Sender", recipientID), new BasicDBObject("Recipient", recipientID)});

                    FindIterable<Document> cursorSR = collectionM.find(senderReceiver);

                    Iterator itOr = cursorSR.iterator();

                    for (Document doc : cursorSR) {

                        if (id.equals(doc.get("Sender", ObjectId.class)) && recipientID.equals(doc.get("Recipient", ObjectId.class))) {

                            dos.writeUTF(username + " : " + doc.get("Message", String.class));
                        } else if (recipientID.equals(doc.get("Sender", ObjectId.class)) && id.equals(doc.get("Recipient", ObjectId.class))) {

                            dos.writeUTF(talkWith + " : " + doc.get("Message", String.class));

                        }

                    }

                }

                String msgToSend = "";
                String recipient = "";
                String recipientUsername = "";

                StringTokenizer st = new StringTokenizer(received, "#");
                if (st.hasMoreTokens()) {
                    recipientUsername = st.nextToken().trim();
                }
                if (st.hasMoreTokens()) {
                    msgToSend = st.nextToken();
                }

                ObjectId sender = new ObjectId();

                FindIterable<Document> cursorSender = collectionP.find(usernameQuery);

                for (Document doc : cursorSender) {
                    sender = doc.get("ID", ObjectId.class);
                }

                ObjectId receiver = new ObjectId();
                String status = "";

                recipientQuery.put("Username", recipientUsername);

                FindIterable<Document> cursorRecipient = collectionP.find(recipientQuery);

                for (Document doc : cursorRecipient) {
                    receiver = doc.get("ID", ObjectId.class);
                    recipient = doc.get("Designation", String.class);
                    status = doc.get("Status", String.class);
                }
                if (msgToSend.trim().length() == 0) {
                    continue;
                }
                Document documentM = new Document("Sender", sender).append("Message", msgToSend).append("Recipient", receiver);

                collectionM.insertOne(documentM);

                (Server.chm2.get((Server.chm1.get(name)))).dos.writeUTF(username + " : " + msgToSend);

                if ((status.equals("Online")) && (Server.chm1.containsKey(recipient)) && ((Server.chm2.get((Server.chm1.get(recipient)))).isLoggedIn == true)) {

                    (Server.chm2.get((Server.chm1.get(recipient)))).dos.writeUTF(username + " : " + msgToSend);

                }

             
            } catch (IOException i) {
                //i.printStackTrace();
                break;
            } catch (NoSuchElementException n) {
                //n.printStackTrace();
            }
        }

    }

    private void toetsiets() {

        boolean collecExists = false;

        MongoClient mongo = new MongoClient("localhost", 27017);

        MongoDatabase database = mongo.getDatabase("myDb");

        MongoCollection<Document> collection = null;
        for (String name : database.listCollectionNames()) {

            if (name.equals("profilesNew")) {
                collecExists = true;
                break;
            }
        }
        if (collecExists == false) {

            database.createCollection("profilesNew");

        }
        collection = database.getCollection("profilesNew");

        FindIterable<Document> cursor = collection.find();

        String str = "";
        String clients = "";
        long count = 0;

        Iterator it = cursor.iterator();
        for (Document doc : cursor) {
            str += (doc.get("Username", String.class) + " ( " + doc.get("Status", String.class) + " )" + "#");

        }

        StringTokenizer st = new StringTokenizer(str, "#");
        String recipient = "";
        String status = "";

        long foundUsernames = collection.countDocuments(new Document());

        try {

            FindIterable<Document> cursorRecipient = collection.find();

            for (Document doc : cursorRecipient) {

                recipient = doc.get("Designation", String.class);
                status = doc.get("Status", String.class);

                if (status.equals("Offline")) {
                    continue;
                }

                if ((Server.chm1.get(recipient)) != null && ((Server.chm2.get((Server.chm1.get(recipient)))) != null)
                        && ((Server.chm2.get((Server.chm1.get(recipient)))).isLoggedIn == true)) {

                    (Server.chm2.get((Server.chm1.get(recipient)))).dos.writeUTF("refresh#" + str);

                }
            }

        } catch (IOException i) {
            //i.printStackTrace();
        } catch (NullPointerException nu) {
            //nu.printStackTrace();

        }

    }

}
