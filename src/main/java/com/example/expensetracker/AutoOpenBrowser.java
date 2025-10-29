package com.example.expensetracker;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.awt.Desktop;
import java.net.URI;

@Component
public class AutoOpenBrowser implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        // Small sleep to give server time to start (tweak if needed)
        Thread.sleep(1500);
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(new URI("http://localhost:8080"));
        }
    }
}

