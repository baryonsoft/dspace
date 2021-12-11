package org.dspace.app.canvasdimension;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.dspace.app.canvasdimension.service.IIIFApiQueryService;
import org.dspace.content.Bitstream;
import org.dspace.services.ConfigurationService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class IIIFApiQueryServiceImpl implements IIIFApiQueryService, InitializingBean {

    @Autowired(required = true)
    protected ConfigurationService configurationService;

    String iiifImageServer;

    @Override
    public void afterPropertiesSet() throws Exception {
       iiifImageServer = configurationService.getProperty("iiif.image.server");
    }

    public int[] getImageDimensions(Bitstream bitstream) {
        return getIiifImageDimensions(bitstream);
    }

    /**
     * Retrieves image dimensions from the image server (IIIF Image API v.2.1.1).
     * @param bitstream the bitstream DSO
     * @return image dimensions
     */
    private int[] getIiifImageDimensions(Bitstream bitstream) {
        int[] arr = new int[2];
        String path = iiifImageServer + bitstream.getID() + "/info.json";
        URL url;
        try {
            url = new URL(path);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JsonNode parent = new ObjectMapper().readTree(response.toString());
            arr[0] = parent.get("width").asInt();
            arr[1] = parent.get("height").asInt();
            return arr;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
