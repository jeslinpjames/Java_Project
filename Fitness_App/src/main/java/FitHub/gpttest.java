/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package FitHub;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
/**
 *
 * @author jeslin
 */
public class gpttest {
     public static void main(String[] args) {
        System.out.println(chatGPT("hello, how are you?"));
        // Prints out a response to the question.
    }
    public static String chatGPTWithRetry(String message, int maxRetries) {
        int retries = 0;
        while (retries < maxRetries) {
            try {
                String response = chatGPT(message);
                return response;
            } catch (RuntimeException e) {
                System.out.println("Request failed with error: " + e.getMessage());
                System.out.println("Retrying in 50 seconds...");
                try {
                    Thread.sleep(50000); // Wait for 5 seconds before retrying
                } catch (InterruptedException ignored) {
                }
                retries++;
            }
        }
        return "Request failed after " + maxRetries + " retries.";
    }

    public static String chatGPT(String message) {
        String url = "https://api.openai.com/v1/chat/completions";
        String apiKey = readApiKeyFromFile("D:\\git\\Java_Project\\API_KEY.env");
        System.out.println(apiKey);
        String model = "gpt-3.5-turbo"; // current model of chatgpt api

        try {
            // Create the HTTP POST request
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + apiKey);
            con.setRequestProperty("Content-Type", "application/json");

            // Build the request body
            String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + message + "\"}]}";
            con.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write(body);
            writer.flush();
            writer.close();

            // Get the response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // returns the extracted contents of the response.
            return extractContentFromResponse(response.toString());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static String readApiKeyFromFile(String filePath) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                return reader.readLine(); // Read the first line which should be the API key
            } catch (IOException e) {
                e.printStackTrace();
                return null; // Return null if there's an error reading the file
            }
        }

    // This method extracts the response expected from chatgpt and returns it.
    public static String extractContentFromResponse(String response) {
        int startMarker = response.indexOf("content")+11; // Marker for where the content starts.
        int endMarker = response.indexOf("\"", startMarker); // Marker for where the content ends.
        return response.substring(startMarker, endMarker); // Returns the substring containing only the response.
    }
}


