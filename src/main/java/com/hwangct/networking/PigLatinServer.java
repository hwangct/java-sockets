//package com.hwangct.networking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A server which accepts strings from clients and converts it to Pig Latin.
 */
public class PigLatinServer {
    public static void main(String[] args) throws IOException {
        System.out.println("Server started");
        int clientNumber = 0;
        ServerSocket listener = new ServerSocket(8090);
        try {
            while (true) {
                new Converter(listener.accept(), clientNumber++).start();
            }
        } finally {
            listener.close();
        }
    }

    /**
     * Convert input requests to Pig Latin.  
     */
    public static class Converter extends Thread {
        private Socket socket;
        private int clientNumber;

        public Converter(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            log("New connection with client# " + clientNumber + " at " + socket);
        }

        public void run() {
            try {
                // Decorate the streams so we can send characters
                // and not just bytes.  Ensure output is flushed after 
                // every newLine.
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                
                out.println("You are client #" + clientNumber + ".");
                out.println("Enter a line with only a period to quit\n");

                // Get messages from the client, line by line, and convert
                while (true) {
                    String input = in.readLine();
                    String output = "";

                    if (input == null || input.equals(".")) {
                        break;
                    }

                    // parses line 
                    String[] words = input.split(" ");
                    for (String word : words) {
                        output += convert((word));
                        output += " ";
                    }
                    out.println(output);
                }
            } catch (IOException e) {
                log("Error handling client# " + clientNumber + ": " + e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    log ("Couldn't close a socket");
                } 
                log("Connection with client# " + clientNumber + " closed");
            }
        }

        /**
         * Logs a simple message.  In this case we just write the
         * message to the server applications standard output.
         */
        public void log(String message) {
            System.out.println(message);
        }

        /**
         * Converts the message to Pig Latin
         */
        public String convert(String message) {
            char[] vowels = {'a', 'e', 'i', 'o', 'u'};
            char first = message.charAt(0);

            for (char c: vowels) {
                if (first == c) {
                    return message + "way";
                }
            }
            
            message = message.substring(1);
            message += first + "ay";
            
            
            return message;
        }
    }
}

