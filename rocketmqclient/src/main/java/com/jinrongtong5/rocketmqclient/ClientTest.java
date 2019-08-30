package com.jinrongtong5.rocketmqclient;

public class ClientTest {
    public static JepsenClient jepsenClient = new JepsenClient();

    public static void main(String[] args) {

        jepsenClient.startup();

        //send and receive
        for (int i = 0; i < 50; i++) {
            System.out.println("dequeue : "+i+": content = " + jepsenClient.dequeue());
            int sendCode = jepsenClient.enqueue(Integer.toString(i));
            System.out.println("enqueue : "+ i + ": sendCode = " + sendCode);

        }

        System.out.println("drain start. ");

        String message = jepsenClient.dequeue();
        //drain
        while (message!=null){
            System.out.println("drain, content = " + message);
            message = jepsenClient.dequeue();
        }

        jepsenClient.shutdown();
    }
}
