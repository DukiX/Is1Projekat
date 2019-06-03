/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package muzika;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author Dusan
 */
public class Muzika {

    @SuppressWarnings("resource")
    public static void main(String[] args) {

        try {
            ServerSocket server = new ServerSocket(50002);
            System.out.println("Server started...");
            while (true) {
                Socket client = server.accept();
                new Worker(client).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

class Worker extends Thread {

    Socket socket;

    public Worker(Socket client) {
        socket = client;
    }

    public void run() {
        try {
            Socket client = this.socket;
            ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(client.getInputStream());

            String imePesme;

            while (true) {
                imePesme = (String) ois.readObject();

                boolean g = pustiPesmu(imePesme);

                oos.writeObject(g);
                oos.flush();

            }
        } catch (Exception e) {

        }
    }

    public boolean pustiPesmu(String imePesme) {
        String encoding = "UTF-8";
        try {
            /*String searchText = "youtube " + imePesme;
            Document google = Jsoup.connect("http://google.com/search?q=" + URLEncoder.encode(searchText, encoding)).userAgent("Mozilla/5.0").get();

            Elements webSitesLinks = google.getElementsByTag("cite");

            //Check if any results found
            if (webSitesLinks.isEmpty()) {
                System.out.println("No results found "+imePesme);
                return false;
            }

            //webSitesLinks.forEach( link -> System.out.println(link.text()));
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(webSitesLinks.get(0).text()));*/

            String searchText = "youtube " + imePesme;
            Document google = Jsoup.connect("http://google.com/search?q=" + URLEncoder.encode(searchText, encoding)).get();

            //Elements webSitesLinks = google.getElementsByTag("cite");
            Elements webSitesLinks = google.getElementsByClass("r H1u2de");
            //Elements webSitesLinks = google.getAllElements();
            //Check if any results found
            if (webSitesLinks.isEmpty()) {
                webSitesLinks = google.getElementsByTag("cite");
                if (webSitesLinks.isEmpty()) {
                    System.out.println("No results");
                    return false;
                } else {
                    System.out.println(webSitesLinks.get(0).text());
                    java.awt.Desktop.getDesktop().browse(java.net.URI.create(webSitesLinks.get(0).text()));
                    return true;
                }
            }

            Pattern pat = Pattern.compile("www\\.youtube\\.com/watch([^\"]+)");
            Matcher mat = pat.matcher(webSitesLinks.toString());
            String link = "";
            if (mat.find()) {
                link = mat.group(0);
            } else {
                System.out.println("No results found");
                return false;
            }
            System.out.println(link);

            //webSitesLinks.forEach( link -> System.out.println(link.toString()));
            //System.out.println(webSitesLinks.get(0).toString());
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(link));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

}
