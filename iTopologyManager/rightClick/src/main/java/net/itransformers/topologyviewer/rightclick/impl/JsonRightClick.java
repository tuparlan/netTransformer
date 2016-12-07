/*
 * jsonRightClick.java
 *
 * This work is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Copyright (c) 2010-2016 iTransformers Labs. All rights reserved.
 */

package net.itransformers.topologyviewer.rightclick.impl;

import net.itransformers.topologyviewer.rightclick.RightClickHandler;
import net.itransformers.topologyviewer.rightclick.impl.putty.Putty;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 4/17/13
 * Time: 10:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class JsonRightClick implements RightClickHandler {
    static Logger logger = Logger.getLogger(JsonRightClick.class);

    public <G> void handleRightClick(JFrame parent, String v,
                                     Map<String, String> graphMLParams,
                                     Map<String, String> rightClickParams,
                                     File projectPath,
                                     java.io.File s) {
        try {
            Map<String, String> connParams;

            final String url = rightClickParams.get("protocol") + "://" + graphMLParams.get("ipAddress") + ":" + rightClickParams.get("port");


            String operation = rightClickParams.get("operation");

            if (operation.equals("get")) {
                URL obj = null;
                obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", "java");
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoOutput(false);

                int responseCode = con.getResponseCode();
//        System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                if (responseCode != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + responseCode);
                }

            } else if (operation.equals("post")) {
                String request = "{\"singer\":\"Metallica\",\"title\":\"Fade To Black\"}";
                URL obj = null;
                obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", "java");
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(request);
                wr.flush();
                wr.close();

                int responseCode = con.getResponseCode();
//        System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Request: " + request);
                System.out.println("Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                if (responseCode != 201) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + responseCode);
                }

            } else if (operation.equals("put")) {
                String request = "{\"singer\":\"Metallica\",\"title\":\"Fade To Black\"}";
                URL obj = null;
                obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                con.setRequestMethod("PUT");
                con.setRequestProperty("User-Agent", "java");
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(request);
                wr.flush();
                wr.close();

                int responseCode = con.getResponseCode();
//        System.out.println("\nSending 'POST' request to URL : " + url);
                System.out.println("Request: " + request);
                System.out.println("Response Code : " + responseCode);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                if (responseCode != 201) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + responseCode);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    protected void handleConnParams(JFrame parent, Map<String, String> connParams, Map<String, String> rightClickParams) {
        Putty putty = new Putty(rightClickParams);
        putty.openSession(connParams);
    }


}
