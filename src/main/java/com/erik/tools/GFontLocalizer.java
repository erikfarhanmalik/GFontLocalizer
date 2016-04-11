package com.erik.tools;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import org.apache.log4j.Logger;

public class GFontLocalizer {

    private String baseUrl = "https://fonts.googleapis.com/css?family=";
    private String fontName = "Lato";
    private String ttfUrl;
    private String path = "/home/erik/workspace/GFont/";
    private final Logger log = Logger.getLogger(GFontLocalizer.class);

    public static void main(String[] args) throws IOException {
        GFontLocalizer engine = new GFontLocalizer();
        engine.extractFontUrl();
        engine.saveFont();
        engine.generateCSS();
    }

    public void extractFontUrl() {
        log.info("Start extracting font url");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(baseUrl + fontName).openStream(), "UTF-8"))) {
            for (String responseLines; (responseLines = reader.readLine()) != null;) {
                if (responseLines.contains("src")) {
                    String firstSplited[] = responseLines.split("\\(");
                    for (String firstHolder : firstSplited) {
                        if (firstHolder.contains("https")) {
                            String[] secondSplited = firstHolder.split("\\)");
                            for (String secondHolder : secondSplited) {
                                if (secondHolder.contains("https")) {
                                    this.ttfUrl = secondHolder;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        log.info("End extracting font url");
    }

    public void generateCSS() {
        log.info("Start Generating CSS file");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(baseUrl + fontName).openStream(), "UTF-8"))) {
            try (PrintWriter out = new PrintWriter(this.path + "/" + fontName + ".css")) {
                String fontType = this.getFontType();
                for (String responseLines; (responseLines = reader.readLine()) != null;) {
                    responseLines = responseLines.replace(ttfUrl, fontName + "." + fontType);
                    out.append(responseLines + "\n");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        log.info("End extracting font url");
    }

    public String getFontType() throws IOException {
        log.info("Start getting font type");
        URL url = new URL(ttfUrl);
        URLConnection uc = url.openConnection();
        String[] types = uc.getContentType().split("/");
        log.info("End getting font type");
        return types[types.length - 1];
    }

    public void saveFont() throws IOException {
        log.info("Start saving font");
        URL url = new URL(ttfUrl);
        InputStream is = url.openStream();
        OutputStream os = new FileOutputStream(this.path + "/" + fontName + "." + this.getFontType());

        byte[] b = new byte[2048];
        int length;

        while ((length = is.read(b)) != -1) {
            os.write(b, 0, length);
        }

        is.close();
        os.close();
        log.info("End saving font");
    }
}
