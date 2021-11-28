package org.papz06;

import org.papz06.Request.CinemaServer;
import org.papz06.Request.UserServer;

import java.io.*;
import java.net.Socket;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;
import org.papz06.Function;
import org.papz06.KeyValue;

// The tutorial can be found just here on the SSaurel's Blog :
// https://www.ssaurel.com/blog/create-a-simple-http-web-server-in-java
// Each Client Connection will be managed in a dedicated Thread
public class JavaHTTPServer implements Runnable {

    static final File WEB_ROOT = new File(".");
    static final String DEFAULT_FILE = "index.html";
    static final String FILE_NOT_FOUND = "404.html";
    //    static final String METHOD_NOT_SUPPORTED = "not_supported.html";
    // port to listen connection
    static final int PORT = Function.getEnv("PORT") != null ? Integer.valueOf(Function.getEnv("PORT")) : 8080;

    // verbose mode
//    static final boolean verbose = true;

    // Client Connection via Socket Class
    private final Socket connect;

    public JavaHTTPServer(Socket c) {
        connect = c;
    }

    @Override
    public void run() {
        // we manage our particular client connection
        BufferedReader in = null;
        PrintWriter out = null;
        BufferedOutputStream dataOut = null;
        String fileRequested;

        try {
            // we read characters from the client via input stream on the socket
            in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            // we get character output stream to client (for headers)
            out = new PrintWriter(connect.getOutputStream());
            // get binary output stream to client (for requested data)
            dataOut = new BufferedOutputStream(connect.getOutputStream());

            // get first line of the request from the client
            String input = in.readLine();
            if (input == null)
                throw new IOException("Empty request!");
            System.out.println(input);
            // we parse the request with a string tokenizer
            StringTokenizer parse = new StringTokenizer(input);
            String method = parse.nextToken().toUpperCase(); // we get the HTTP method of the client
            // we get file requested
            fileRequested = parse.nextToken().toLowerCase();
            // Ignore header
            while (input.length() != 0) {
                input = in.readLine();
            }
            // Read body
            String requesBody = "";
            while (in.ready()) {
                requesBody += (char) in.read();
            }
            // prepare url, queryParams, index
            String url;
            Map<String, String> queryParams = null;
            if (fileRequested.indexOf('?') != -1) {
                String[] pairs = fileRequested.split("\\?");
                url = pairs[0];
                queryParams = new Utils().splitQuery(pairs[1]);
            } else url = fileRequested.substring(1);
            String id = null;
            KeyValue<Integer, String> result = null;
            if (url.indexOf('/') != -1) {
                int lx = url.lastIndexOf('/');
                id = url.substring(lx+1);
                url = url.substring(0, lx);
            }
            System.out.println(url);
            System.out.println(id);

            if (method.equals("POST")) {
                /**
                 * Divider cases for PORT: - login
                 * **/
                // Case 1:
                if (url.equals("login")) result= new UserServer().login(requesBody);
                // Case 2:
                if (url.equals("cinema") ) result = new CinemaServer().CinemaCreate(requesBody);
                // Case 3:
                // Case 4:
                // Case 5:
                // Case 6:
                // Template
            } else if (method.equals("GET")) {
                // GET method
                /**
                 * Divider cases for GET: -
                 * **/
                // Case 1:
                if (url.equals("cinema") && (id == null)) result = new CinemaServer().CinemaList();
                else if (url.equals("cinema") && (id != null))
                    result = new CinemaServer().CinemaDetails(Integer.parseInt(id));

            } else if (method.equals("PATCH")){
                /**
                 * Divider cases for PATCH: -
                 * **/
                // Case 1:
                if (url.equals("cinema") && (id != null)) result = new CinemaServer().CinemaUpdate(Integer.parseInt(id), requesBody);

            } else if (method.equals("DELETE")){
                /**
                 * Divider cases for DELETE: -
                 * **/
                // Case 1:
                if (url.equals("cinema") && (id != null)) result = new CinemaServer().CinemaDelete(Integer.parseInt(id));

            } else {
                System.out.println("405 Method Not Allowed : " + method + " method.");

                // we return the not supported file to the client
//                File file = new File(WEB_ROOT, METHOD_NOT_SUPPORTED);
//                int fileLength = (int) file.length();
//                String contentMimeType = "text/html";
//                //read content to return to client
//                byte[] fileData = readFileData(file, fileLength);

                // we send HTTP Headers with data to client
                out.write("HTTP/1.1 405 Method Not Allowed\r\n");
                out.write("Server: Java HTTP Server from SSaurel : 1.0\r\n");
                out.write("Date: " + new Date()+"\r\n");
//                out.println("Content-type: " + contentMimeType);
                out.write("Content-length: 0\r\n");
                out.write("Access-Control-Allow-Origin: *\r\n");
                out.write("\r\n"); // blank line between headers and content, very important !
                out.flush(); // flush character output stream buffer
                dataOut.flush();

            }
            if (result != null){
                out.write("HTTP/1.1 " + result.getKey() + " OK\r\n");
                out.write("Server: Java HTTP Server from SSaurel : 1.0\r\n");
                out.write("Date: " + new Date()+"\r\n");
                out.write("Connection: close\r\n");
                out.write("Content-length: " + result.getValue().getBytes().length+"\r\n");
                out.write("Access-Control-Allow-Origin: *\r\n");
                out.write("\r\n"); // blank line between headers and content, very important !
                // End Template
                out.write(result.getValue());
                out.write("\r\n");
                out.flush();
//                byte[] fileData = readFileData(file, fileLength);
//                dataOut.write(fileData, 0, fileLength);
                dataOut.flush();
            }
            else{
                // Implement!!!
            }


        } catch (IOException ioe) {
            System.err.println("Server error : " + ioe);
            String err = ioe.toString();
            out.write("HTTP/1.1 500 Internal Server Error\r\n");
            out.write("Server: Java HTTP Server from SSaurel : 1.0\r\n");
            out.write("Date: " + new Date()+"\r\n");
            out.write("Connection: close\r\n");
            out.write("Content-length: " +err.getBytes().length+ "\r\n");
            out.write("Access-Control-Allow-Origin: *\r\n");
            out.write("\r\n"); // blank line between headers and content, very important !
            out.write(err);
            out.write("\r\n");
            out.flush();
        } finally {
            try {
                in.close();
                out.close();
                dataOut.close();
                connect.close(); // we close socket connection
            } catch (Exception e) {
                System.err.println("Error closing stream : " + e.getMessage());
            }
            System.out.println("Connection closed.\n");
        }


    }

    private byte[] readFileData(File file, int fileLength) throws IOException {
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];

        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        } finally {
            if (fileIn != null)
                fileIn.close();
        }

        return fileData;
    }
    private String getContentType(String fileRequested) {
        if (fileRequested.endsWith(".htm")  ||  fileRequested.endsWith(".html"))
            return "text/html";
        else
            return "text/plain";
    }
}