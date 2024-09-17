package com.andrew;

import static java.lang.System.out;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fastcgi.FCGIInterface;

class FCGIServer {
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

    @SuppressWarnings("static-access")
    public static void main(String[] args) {
        FCGIInterface fcgiinterface = new FCGIInterface();
        while (fcgiinterface.FCGIaccept() >= 0) {
            String URI = (String) fcgiinterface.request.params.get("REQUEST_URI");

            out.println("Access-Control-Allow-Origin: *");
            out.println("Connection: keep-alive");
            out.println("Content-Type: application/json\n\n");

            try {
                Parameters params = Parameters.fromURI(URI);
                point(params);
            } catch (Exception e) {
                JSONBuilder builder = new JSONBuilder();
                builder.addValue("status", "error");
                builder.addValue("message", e.getMessage());
            }
        }
    }

    public static void point (Parameters params) {
        long startTime = System.nanoTime();

        JSONBuilder builder = new JSONBuilder();
        try {
            double x = Double.parseDouble(params.get("x"));
            double y = Double.parseDouble(params.get("y"));
            double r = Double.parseDouble(params.get("r"));
            if (y <= -3 || y >= 3) {
                builder.addValue("status", "error");
                builder.addValue("message", "y must be in (-3; 3)");
            } else {
                builder.addValue("status", "ok");
                builder.addValue("x", params.get("x"));
                builder.addValue("y", params.get("y"));
                builder.addValue("r", params.get("r"));
                builder.addValue("current_time", dtf.format(LocalDateTime.now()));
                builder.addValue("execution_time", String.valueOf((double)(System.nanoTime() - startTime) / 1000));

                if ((x * x + y * y < r * r && x >= 0 && y <= 0) ||
                (x >= 0 && y >= 0 && y <= -x/2 + r/2) ||
                ((-r/2 <= x && x <= 0) && (-r <= y && y <= 0))) {
                    builder.addValue("result", "hit");
                } else {
                    builder.addValue("result", "miss");
                }
            }
        } catch (Exception e) {
            builder.addValue("status", "error");
            builder.addValue("message", e.toString());
        }
        out.println(builder.build());
    }
}
