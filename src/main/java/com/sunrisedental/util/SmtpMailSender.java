package com.sunrisedental.util;

import com.sunrisedental.config.MailConfig;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Lightweight SMTP client (AUTH LOGIN + STARTTLS / SSL) so patient emails can be sent without extra JARs.
 */
public final class SmtpMailSender {

    private static final Logger LOGGER = Logger.getLogger(SmtpMailSender.class.getName());

    private SmtpMailSender() {}

    public static boolean send(String to, String subject, String body) {
        MailConfig cfg = MailConfig.getInstance();
        if (!cfg.isEnabled()) {
            LOGGER.warning("SMTP not configured. Set mail.enabled=true and Gmail username/app password in db.properties.");
            return false;
        }
        if (to == null || to.isBlank()) return false;

        Socket socket = null;
        try {
            if (cfg.isSsl()) {
                SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                socket = factory.createSocket(cfg.getHost(), cfg.getPort());
            } else {
                socket = new Socket(cfg.getHost(), cfg.getPort());
            }
            socket.setSoTimeout(20000);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

            expect(in, 220);
            sendLine(out, "EHLO sunrisedental.lk");
            readEhlo(in);

            if (!cfg.isSsl() && cfg.isStartTls()) {
                sendLine(out, "STARTTLS");
                expect(in, 220);
                socket = wrapTls(socket, cfg.getHost(), cfg.getPort());
                in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                sendLine(out, "EHLO sunrisedental.lk");
                readEhlo(in);
            }

            sendLine(out, "AUTH LOGIN");
            expect(in, 334);
            sendLine(out, Base64.getEncoder().encodeToString(cfg.getUsername().getBytes(StandardCharsets.UTF_8)));
            expect(in, 334);
            sendLine(out, Base64.getEncoder().encodeToString(cfg.getPassword().getBytes(StandardCharsets.UTF_8)));
            expect(in, 235);

            sendLine(out, "MAIL FROM:<" + cfg.getFrom() + ">");
            expect(in, 250);
            sendLine(out, "RCPT TO:<" + to.trim() + ">");
            expect(in, 250);
            sendLine(out, "DATA");
            expect(in, 354);

            StringBuilder data = new StringBuilder();
            data.append("From: ").append(cfg.getFrom()).append("\r\n");
            data.append("To: ").append(to.trim()).append("\r\n");
            data.append("Subject: ").append(encodeSubject(subject)).append("\r\n");
            data.append("MIME-Version: 1.0\r\n");
            data.append("Content-Type: text/plain; charset=UTF-8\r\n");
            data.append("\r\n");
            data.append(body == null ? "" : body.replace("\r\n", "\n").replace("\n", "\r\n"));
            data.append("\r\n.\r\n");
            out.write(data.toString());
            out.flush();
            expect(in, 250);

            sendLine(out, "QUIT");
            LOGGER.info("SMTP sent to " + to + " | " + subject);
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "SMTP send failed to " + to + ": " + e.getMessage(), e);
            return false;
        } finally {
            if (socket != null) {
                try { socket.close(); } catch (Exception ignored) {}
            }
        }
    }

    private static Socket wrapTls(Socket plain, String host, int port) throws Exception {
        SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket ssl = (SSLSocket) factory.createSocket(plain, host, port, true);
        ssl.setUseClientMode(true);
        ssl.startHandshake();
        return ssl;
    }

    private static void sendLine(BufferedWriter out, String line) throws Exception {
        out.write(line);
        out.write("\r\n");
        out.flush();
    }

    private static void expect(BufferedReader in, int code) throws Exception {
        String line = in.readLine();
        if (line == null || !line.startsWith(String.valueOf(code))) {
            throw new IllegalStateException("SMTP expected " + code + " but got: " + line);
        }
        while (line != null && line.length() >= 4 && line.charAt(3) == '-') {
            line = in.readLine();
        }
    }

    private static void readEhlo(BufferedReader in) throws Exception {
        String line;
        do {
            line = in.readLine();
            if (line == null) throw new IllegalStateException("SMTP EHLO closed");
        } while (line.length() >= 4 && line.charAt(3) == '-');
        if (!line.startsWith("250")) {
            throw new IllegalStateException("SMTP EHLO failed: " + line);
        }
    }

    private static String encodeSubject(String subject) {
        if (subject == null) return "";
        return subject.replace("\r", " ").replace("\n", " ");
    }
}
