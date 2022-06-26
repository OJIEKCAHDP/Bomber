package bomber.ojiekcahdp.dev.handler;

public class Listener {

    public void onMessageReceive(String message) {
        if (message.startsWith("bomb")) {
            String[] args = message.split(" ");
            if (args.length < 4) {
                System.out.println("Usage: bomb number count_attack message_text");
                return;
            }
            String number = args[1];
            int count = Integer.parseInt(args[2]);
            String text = "";
            for (int i = 3; i < args.length; i++) {
                text += args[i];
            }
            System.out.println("Number: " + number);
            System.out.println("Count: " + count);
            System.out.println("Text:" + text);
        }
    }

}
