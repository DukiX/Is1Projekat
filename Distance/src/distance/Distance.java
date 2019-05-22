/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package distance;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
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

public class Distance {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		
		try {
			ServerSocket server=new ServerSocket(50001);
			System.out.println("Server started...");
			while(true){
				Socket client=server.accept();
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

            String destination;

            while (true) {
                destination = (String) ois.readObject();

                int g = dohvati("Belgrade",destination);
                
                oos.writeObject(g);
                oos.flush();

            }
        } catch (Exception e) {
            
        }
    }
    
     public static int dohvati(String source, String destination) {
        try {
            System.out.println("prosao" + destination);
            //String searchText = "distance from "+source+"to"+ destination+" km \"car\"";
            //String searchText = source+"to"+ destination+" distance km";
            //Document google = Jsoup.connect("https://google.com/search?q=" + URLEncoder.encode(searchText, encoding)).userAgent("Mozilla/5.0").get();
            //Document distance = Jsoup.connect("http://www.travelmath.com/drive-distance/from/" + source + "/to/" + destination).userAgent("Mozilla/5.0").get();
            //Elements elems = distance.getAllElements();

            Document time = Jsoup.connect("http://www.travelmath.com/driving-time/from/" + source + "/to/" + destination).userAgent("Mozilla/5.0").get();
            Elements elems2 = time.getAllElements();

            //Check if any results found
            //if (elems.isEmpty()) {
            //System.out.println("No results found");
            //return;
            // }
            if (elems2.isEmpty()) {
                System.out.println("No results found");
                return 0;
            }

            String distanca = "";
            double km = 0;

            Pattern pattern = Pattern.compile("(\\d+\\.\\d+ km)|(\\d+ km)");
            //Pattern pattern = Pattern.compile("\\d+ h \\d+ min");
            //webSitesLinks.forEach( link -> System.out.println(link.text()));
            //for (Element e : elems) {
            //System.out.println(e.text());
            //System.out.println("********************************************");

            //Matcher matcher = pattern.matcher(e.text());
            //if (matcher.find()) {
            //distanca = matcher.group(0);
            //String sub;
            // sub = distanca.substring(0, distanca.length() - 3);
            //km = Double.parseDouble(sub.replaceAll("\\s+", ""));
            /*if(km<30 || km > 3000){
            continue;
            }*/
            // break;
            //}
            //}
            //System.out.println(distanca);
            //System.out.println(km);
            String vreme = "";
            int minuta = 0;

            pattern = Pattern.compile("\\d+ hours?, \\d+ minutes?");
            Pattern pattern2 = Pattern.compile("\\d+ minutes?");
            //Pattern pattern = Pattern.compile("\\d+ h \\d+ min");
            //webSitesLinks.forEach( link -> System.out.println(link.text()));
            for (Element e : elems2) {
                //System.out.println(e.text());
                //System.out.println("********************************************");

                Matcher matcher = pattern.matcher(e.text());
                if (matcher.find()) {
                    vreme = matcher.group(0);
                    Pattern p1 = Pattern.compile("\\d+ hour");
                    Matcher m1 = p1.matcher(vreme);
                    String s1 = "";
                    if (m1.find()) {
                        s1 = m1.group(0);
                    }
                    Pattern p2 = Pattern.compile("\\d+ minute");
                    Matcher m2 = p2.matcher(vreme);
                    String s2 = "";
                    if (m2.find()) {
                        s2 = m2.group(0);
                    }
                    String s1sub = s1.substring(0, s1.length() - 5);
                    String s2sub = s2.substring(0, s2.length() - 7);
                    minuta = Integer.parseInt(s1sub) * 60 + Integer.parseInt(s2sub);
                    break;
                }
                Matcher matcher2 = pattern2.matcher(e.text());
                if (matcher2.find()) {
                    vreme = matcher2.group(0);
                    String sub;
                    sub = vreme.substring(0, vreme.length() - 8);
                    minuta = Integer.parseInt(sub);
                    break;
                }
            }
            //System.out.println(vreme);
            System.out.println(minuta);
            return minuta;
        } catch (IOException ex) {
            Logger.getLogger(Distance.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

}
