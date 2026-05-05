package com.group.medical.util;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

    // ── READ all non-blank pipe-lines (for Invoice parsing) ──────────────────
    public static List<String> readAllLines(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String t = line.trim();
                // Only keep lines that look like data records (contain '|')
                if (!t.isEmpty() && t.contains("|")) {
                    lines.add(t);
                }
            }
        } catch (IOException ignored) {}
        return lines;
    }

    // ── READ every raw line (including formatted blocks) ─────────────────────
    public static List<String> readRawLines(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException ignored) {}
        return lines;
    }

    // ── APPEND a pipe-delimited record line ───────────────────────────────────
    public static void appendToFile(String filename, String data) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            bw.write(data);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── APPEND a multi-line formatted block + blank separator ─────────────────
    public static void appendFormattedBlock(String filename, String block) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename, true))) {
            bw.write(block);
            bw.newLine();
            bw.newLine(); // blank line between records
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── WRITE pipe-delimited lines back (preserves formatted blocks) ──────────
    public static void writeAllLines(String filename, List<String> lines) {
        // Read current file to get the raw content (formatted blocks)
        List<String> rawLines = readRawLines(filename);

        // Replace only the pipe-delimited lines in the raw file
        List<String> result = new ArrayList<>();
        for (String raw : rawLines) {
            if (raw.trim().contains("|")) {
                // Find the matching updated line
                String invoiceId = raw.trim().split("\\|")[0];
                String match = lines.stream()
                        .filter(l -> l.startsWith(invoiceId + "|"))
                        .findFirst()
                        .orElse(null);
                if (match != null) {
                    result.add(match);
                }
                // If match is null, the line was deleted — skip it
            } else {
                result.add(raw);
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (String line : result) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ── WRITE raw lines exactly as-is ─────────────────────────────────────────
    public static void writeRawLines(String filename, List<String> lines) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (String line : lines) {
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}