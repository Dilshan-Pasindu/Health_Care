package com.group.medical;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@SpringBootApplication
public class MedicalAppointmentSystemApplication {
    public static void main(String[] args) {
        // Set headless property to false to allow Desktop API usage
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(MedicalAppointmentSystemApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void openBrowserAfterStartup() {
        String url = "http://localhost:8080/billing/generate";
        try {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            } else {
                // Fallback for Mac
                Runtime.getRuntime().exec("open " + url);
            }
        } catch (IOException | URISyntaxException e) {
            System.err.println("Could not automatically open browser: " + e.getMessage());
        }
    }
}