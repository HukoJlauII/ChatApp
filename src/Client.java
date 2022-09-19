import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.username = username;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
        } catch (IOException e) {
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }
    public void sendMessage()
    {
        try{
            System.out.println(Thread.currentThread().getId());
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            Scanner scanner=new Scanner(System.in);

            while (socket.isConnected())
            {
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(username+": "+messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }

    public void listenForMessage()
    {
       new Thread(() -> {
           String messageFromGroupChat;
           while (socket.isConnected())
           {
               try {
                   System.out.println(Thread.currentThread().getId());
                   messageFromGroupChat=bufferedReader.readLine();
                   System.out.println(messageFromGroupChat);
               } catch (IOException e) {
                   closeEverything(socket,bufferedReader,bufferedWriter);
               }
           }
       }).start();
    }

    public void closeEverything(Socket socket,BufferedReader bufferedReader,BufferedWriter bufferedWriter)
    {
        try
        {
            if (bufferedReader!=null)
            {
                bufferedReader.close();
            }
            if (bufferedWriter!=null)
            {
                bufferedWriter.close();
            }
            if (socket!=null)
            {
                socket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner=new Scanner(System.in);
        System.out.println("Enter your username for the group chat: ");
        String username= scanner.nextLine();
        Socket socket1=new Socket("localhost",1234);
        Client client=new Client(socket1,username);
        client.listenForMessage();
        client.sendMessage();
    }
}
