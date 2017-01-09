/**
 * Created by Vizz on 1/6/17.
 */

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class UDPClient{

    private String hostname;
    private String username;
    private String msg = "";
    private int port;

    private static List<Neighbour> connections = new ArrayList<Neighbour>();
    //private static String[] media = {"Sanandrias" , "sanandrias", "Visitha"};

    private static List<String> media;
    private static String fileNames[] = {"Adventures of Tintin",
            "Jack and Jill",
            "Glee",
            "The Vampire Diarie",
            "King Arthur",
            "Windows XP",
            "Harry Potter",
            "Kung Fu Panda",
            "Lady Gaga",
            "Twilight",
            "Windows 8",
            "Mission Impossible",
            "Turn Up The Music",
            "Super Mario",
            "American Pickers",
            "Microsoft Office 2010",
            "Happy Feet",
            "Modern Family",
            "American Idol",
            "Hacking for Dummies"} ;

    private static String queries[] = {"Twilight\n" +
            "Jack\n" +
            "American Idol\n" +
            "Happy Feet\n" +
            "Twilight saga\n" +
            "Happy Feet\n" +
            "Happy Feet\n" +
            "Feet\n" +
            "Happy Feet\n" +
            "Twilight\n" +
            "Windows\n" +
            "Happy Feet\n" +
            "Mission Impossible\n" +
            "Twilight\n" +
            "Windows 8\n" +
            "The\n" +
            "Happy\n" +
            "Windows 8\n" +
            "Happy Feet\n" +
            "Super Mario\n" +
            "Jack and Jill\n" +
            "Happy Feet\n" +
            "Impossible\n" +
            "Happy Feet\n" +
            "Turn Up The Music\n" +
            "Adventures of Tintin\n" +
            "Twilight saga\n" +
            "Happy Feet\n" +
            "Super Mario\n" +
            "American Pickers\n" +
            "Microsoft Office 2010\n" +
            "Twilight\n" +
            "Modern Family\n" +
            "Jack and Jill\n" +
            "Jill\n" +
            "Glee\n" +
            "The Vampire Diarie\n" +
            "King Arthur\n" +
            "Jack and Jill\n" +
            "King Arthur\n" +
            "Windows XP\n" +
            "Harry Potter\n" +
            "Feet\n" +
            "Kung Fu Panda\n" +
            "Lady Gaga\n" +
            "Gaga\n" +
            "Happy Feet\n" +
            "Twilight\n" +
            "Hacking\n" +
            "King"};


    public  UDPClient(String hostname, String username, int port){
        this.hostname = hostname;
        this.username = username;
        this.port = port;
        this.media = new ArrayList<String>();

        int fileCount = fileNames.length;
        System.out.println(fileCount);
        //Random random = new Random(System.nanoTime());
        for(int i = 0 ; i<4 ;i++) {
            int rand = (int) (Math.random() * fileCount);
            media.add(fileNames[rand]);
            System.out.print(rand+"-"+fileNames[rand]+", ");
        }

        System.out.println();


    }

//


    public static void main (String args[]) {

        UDPClient client = new UDPClient("localhost", "visitha", 55555);
        Scanner input = new Scanner(System.in);
        try {
            System.out.println("Binding to a local port");
            DatagramSocket socket = new DatagramSocket();
            System.out.println("Bound to local port " + socket.getLocalPort());
            String host = " " + InetAddress.getLoopbackAddress();
            host = host.split("/")[1];
            String port = new String(" " + socket.getLocalPort() + " ");
            client.msg = " " + host + port + client.username;

            /**
             Test harness
             */

            while(true) {
                System.out.println("\n--------------------------------*****************--------------------------------");
                System.out.print("Enter R - register, ");
                System.out.print("U - unregister, ");
                System.out.print("L - leave, ");
                System.out.println("S - search ");
                System.out.println("-------------------------------*****************--------------------------------\n");

                String in = input.nextLine();
                switch (in){
                    case "R" : {
                        client.register( client.msg, client.hostname, socket, client.port );
                        ClientConnector cc = new ClientConnector(socket, connections, media);
                        cc.start();
                        client.joinAndLeaveFromNetwork(host, port, "JOIN");
                        break;
                    }
                    case "U" : {
                        client.joinAndLeaveFromNetwork(host, port, "LEAVE");
                        client.unRegister( client.msg, client.hostname, socket, client.port );
                        break;
                    }
                    case "L" : {
                        client.joinAndLeaveFromNetwork(host, port, "LEAVE");
                        break;
                    }
                    case "S" : {
                        System.out.print("Enter File name   : ");
                        String filename = input.nextLine();
                        client.search(filename, host, port, socket);
                        break;
                    }

                    default: {
                        System.out.println("Wrong input.");
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }


    }

    public void search(String fileName, String host, String port, DatagramSocket listeningSocket ){
        try {
            DatagramSocket socket = new DatagramSocket();

            for(int i = 0; i < connections.size(); i++){
                Neighbour neighbour = connections.get(i);
                String search = " SER" + " " + host + port + fileName + " 0";
                int jLength = search.length() + 5;
                search = getLengthFormat(jLength) + search;
                sendPacket(search, socket, neighbour.getPort(), neighbour.getIp());
                //System.out.println(getResponsFromServer(listeningSocket));
                //socket.close();

            }

        } catch (SocketException e) {
            e.printStackTrace();
        }

    }

    public void joinAndLeaveFromNetwork(String host, String port, String message){

        try {
            DatagramSocket socket = new DatagramSocket();

            for(int i = 0; i < connections.size(); i++){
                Neighbour neighbour = connections.get(i);
                String join = " " + message + " " + host + port;
                int jLength = join.length() + 5;
                join = getLengthFormat(jLength) + join;
                sendPacket(join, socket, neighbour.getPort(), neighbour.getIp());
                System.out.println(getResponsFromServer(socket));
                // socket.close();

            }

        } catch (SocketException e) {
            e.printStackTrace();
        }



    }
    public void register(String msg, String hostname, DatagramSocket socket, int depPort){
        String register = "REG " + msg;
        int length = register.length() + 5;
        register = getLengthFormat(length) + " " + register;
        sendPacket(register, socket, depPort, hostname);

        String resposnse = getResponsFromServer(socket);
        System.out.println(resposnse);
        StringTokenizer st = new StringTokenizer(resposnse);
        String lenght = st.nextToken();
        String command = st.nextToken();
        String count = st.nextToken();
        int num = Integer.parseInt(count);

        if(command.equals("REGOK")) {
            if (num > 0) {
                while (st.hasMoreTokens()) {
                    String ip = st.nextToken();
                    String Nport = st.nextToken();
                    String username = "";
                    connections.add(new Neighbour(ip, Integer.parseInt(Nport), username));
                }
            }
        }

    }

    public static String getResponsFromServer(DatagramSocket socket){
        System.out.println ("Waiting for packet.... ");
        byte[] received = new byte[65536];
        DatagramPacket receivePacket = new DatagramPacket(received, 65536);
        String response = "";

        boolean timeout = false;

        try
        {
            socket.receive(receivePacket);

        } catch (IOException e) {
            timeout = true;
        }

        if(!timeout){
            System.out.println ("packet received!");
            System.out.println ("Details : " + receivePacket.getAddress());

            ByteArrayInputStream bin = new ByteArrayInputStream (receivePacket.getData(), 0, receivePacket.getLength() );
            BufferedReader reader = new BufferedReader (new InputStreamReader ( bin ) );


            while(true){
                String line = null;
                try {
                    line = reader.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (line == null)
                    break; else {
                    response = response + line + " ";
                }

            }

        } else {
            System.out.println("Packet Lost");
        }

        return response;
    }


    public void unRegister(String msg, String hostname, DatagramSocket socket, int depPort){
        String unRegister = "UNREG " + msg;
        int length = unRegister.length() + 5;
        unRegister = getLengthFormat(length) + " " + unRegister;
        sendPacket(unRegister, socket, depPort, hostname);
    }


    public void sendPacket(String msg, DatagramSocket socket, int depPort, String hostname){

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        PrintStream pout = new PrintStream(bout);
        pout.print(msg);

        byte[] barray = bout.toByteArray();
        DatagramPacket packet = new DatagramPacket(barray, barray.length);

        try {
            packet.setAddress(InetAddress.getByName(hostname));
            packet.setPort(depPort);
            socket.send(packet);
            System.out.println("Packet sent!");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getLengthFormat(int x){
        String length = "0000";
        if(x < 10 && x > 0) {
            length = "000" + x;
        }
        if(x >= 10 && x < 100){
            length = "00" + x;

        }
        if(x >= 100 && x < 999){
            length = "0" + x;

        }
        if(x >= 1000){
            length = "" + x;

        }
        return length;
    }
}

class Neighbour{
    private String ip;
    private int port;
    private String username;

    public Neighbour(String ip, int port, String username){
        this.ip = ip;
        this.port = port;
        this.username = username;
    }

    public String getIp(){
        return this.ip;
    }

    public String getUsername(){
        return this.username;
    }

    public int getPort(){
        return this.port;
    }
}

class ClientConnector extends Thread {

    DatagramSocket socket;
    List<Neighbour> connections;
    List<String> media;

    public ClientConnector(DatagramSocket socket, List<Neighbour> connections, List<String> media){
        this.socket = socket;
        this.connections = connections;
        this.media = media;
    }
    @Override
    public void run() {
        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(new byte[65535], 65535);
                System.out.println("Waiting for a Neighbour.....");
                socket.receive(packet);
                System.out.println("Neighbour received!");
                InetAddress remote_addr = packet.getAddress();
                System.out.print("Sent by : " + remote_addr.getHostAddress());
                System.out.println(" Sent from: " + packet.getPort());

                byte[] data = packet.getData();
                String s = new String(data, 0, packet.getLength());
                respond(s, packet);
                System.out.println(s);
            } catch (IOException ioe) {
                System.err.println("Error - " + ioe);
            }
        }
    }

    public void respond(String msg,  DatagramPacket packet) throws UnknownHostException {

        StringTokenizer st = new StringTokenizer(msg);
        String length = st.nextToken();
        String command = st.nextToken();
        String ack = "";
        boolean sendACK = true;

        if(command.equals("JOIN")){
            String host = st.nextToken();
            String port = st.nextToken();
            Neighbour neighbour = new Neighbour(host, Integer.parseInt(port), "");
            connections.add(neighbour);

            ack = "0014 JOINOK 0";

            sendMessage(socket, ack, packet.getAddress(), packet.getPort());
            //TODO do error handleling here
        } else if(command.equals("LEAVE")) {
            String host = st.nextToken();
            String port = st.nextToken();

            for(int i = 0; i < connections.size(); i++){
                if(connections.get(i).getPort() == Integer.parseInt(port)){
                    connections.remove(i);
                    ack = "0014 LEAVEOK 0";
                }
            }
            sendMessage(socket, ack, packet.getAddress(), packet.getPort());
        } else if(command.equals("SER")){

            List<String> matches = new ArrayList<String>();
            String ip = st.nextToken();
            String port = st.nextToken();
            String fileName = st.nextToken();
            String hops = st.nextToken();

            int count = Integer.parseInt(hops);
            if(count <= 2){
                count++;
                for (int i = 0; i < media.size(); i++) {
                    if (media.get(i).contains(fileName)) {
                        matches.add(media.get(i));
                    }
                }

                String newMessage = command + " " + ip + " " + port + " "+ fileName + " " + count;
                newMessage = UDPClient.getLengthFormat(newMessage.length()) + " " + newMessage;

                for(int x = 0; x < connections.size(); x++ ){
                    if(!(connections.get(x).getIp().equals(ip) && connections.get(x).getPort() == Integer.parseInt(port)) )
                        sendMessage(socket,newMessage,InetAddress.getByName(connections.get(x).getIp()), connections.get(x).getPort() );
                }
                if(matches.size() > 0){
                    String ipAddress = " " + InetAddress.getLocalHost();
                    String ips = ipAddress.split("/")[1];
                    String newAck = "SEROK" + " " + matches.size() + " " + ips + " " +  socket.getLocalPort() + " " + count;
                    for (int j = 0; j < matches.size(); j ++){
                        newAck = newAck + " " + matches.get(j);
                    }
                    sendMessage(socket, newAck, InetAddress.getByName(ip), Integer.parseInt(port));
                }
            }
        } else if(command.equals("SEROK")){
            System.out.println(msg);
        }

    }

    public void sendMessage(DatagramSocket socket, String ack, InetAddress host, int port){
        DatagramPacket packetAck = new DatagramPacket(ack.getBytes(), ack.getBytes().length);
        packetAck.setAddress(host);
        packetAck.setPort(port);
        try {
            socket.send(packetAck);
            System.out.println("ACK sent");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
