package com.gopyyn.salad.nlp;

import com.gopyyn.salad.core.SaladCommands;
import com.gopyyn.salad.enums.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class SaladBot extends Frame {
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField chatBox;
    private JScrollPane scroll;
    private Border border;
    public static void main(String args[]) {
        new SaladBot ();
    }
    public SaladBot () {
        frame = new JFrame("Chatbot");
        chatArea = new JTextArea (2,540);
        chatBox = new JTextField();
        scroll = new JScrollPane(chatArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        border = BorderFactory.createLineBorder(Color.BLUE, 1);
        chatArea. setSize (540, 400);
        chatArea.setLocation (2,2) ;
        chatBox. setSize (540, 30);
        chatBox. setLocation (2,36);
        chatBox.setBorder (border); frame.setResizable(false);
        frame. setSize (600, 600);
        frame.add(chatBox);
        frame.add (scroll);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE) ;
        initialize();
        List<String> allSaladActions = new ArrayList<>();
        chatBox.addActionListener(arg -> {
            String gtext = chatBox.getText () ;
            chatArea.append("User: " + gtext + "\n");
            chatBox.setText("");
            try {
                if (gtext.equals("QUIT")) {
                    SaladCommands.getDriver().close();
                    bot("Below is the feature file.");
                    String finalString = allSaladActions.stream().reduce((s, s2) -> s + "\n   " + s2).orElse("");
                    finalString = "\nBelow is the feature file\nFeature: some feature text\n  Scenario: some scenario text\n\t" + finalString;
                    bot("\n"+ finalString+"\nThank you!. See you again");
                    bot("\n"+ finalString+"\nThe window will close in 10 seconds");
                    allSaladActions.clear();

                    CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                        SaladCommands.wait(10, TimeUnit.SECONDS);
                        return "success";
                    });

                    future.thenAccept(s -> System.exit(0));
                    return;
                }
                if (gtext.equals("CLEAR")) {
                    chatArea.removeAll();
                    initialize();
                }
                List<String> saladAction = ActionDetector.findSaladAction(gtext);
                saladAction.forEach(s -> bot(s));
                allSaladActions.addAll(saladAction);
            } catch (Exception e) {
                System.out.println("Exception thrown."+ e);
                bot("Unable to detect action. Please try to word differently");
            }
        });
    }

    private void initialize() {
        bot("Hello! I am a salad chatbot to do web testing. Tell me what you to do. \nType \"QUIT\" to end the program. \n\n");
        chatArea.append ("Conversation: \n");
        chatBox.setText("");
    }

    private void bot(String response) {
        chatArea.append("Salad: " + response + "\n");
    }

}