package com.rbkmoney.fraudbusters.service;

import com.rbkmoney.fraudbusters.exception.ReadDataException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileCardTokenManagementService {

    @Value("${card-token-pool.filePath}")
    private String filepath;

    public void writeToFile(String fileName, List<String> cardTokens) {
        File directory = new File(filepath);
        if (!directory.exists()) {
            final boolean mkdir = directory.mkdir();
            if (!mkdir) {
                log.warn("can't create directory when writeToFile filepath: {}", filepath);
            }
        }
        try (BufferedWriter out = new BufferedWriter(new FileWriter(initFilePath(fileName)))) {
            for (String cardToken : cardTokens) {
                out.write(cardToken);
                out.newLine();
            }
        } catch (IOException e) {
            log.error("Error when writeToFile() fileName: {}, cardTokens size: {} e: ", fileName, cardTokens.size(), e);
        }
    }

    @NotNull
    private String initFilePath(String fileName) {
        return filepath + "/" + fileName;
    }

    public void deleteOldFiles() {
        final File[] files = readAllFiles();
        if (!ArrayUtils.isEmpty(files)) {
            for (File file : files) {
                if (file.delete()) {
                    log.info("deleteFile success: {}", file.getPath());
                } else {
                    log.warn("deleteFile error: {}", file.getPath());
                }
            }
        }
    }

    private File[] readAllFiles() {
        File folder = new File(filepath);
        return folder.listFiles();
    }

    public List<String> readCardTokensFromFile(String currentScheduleTime) {
        final File[] files = readAllFiles();
        if (!ArrayUtils.isEmpty(files)) {
            for (File file : files) {
                if (currentScheduleTime.equals(file.getName())) {
                    try (BufferedReader br = new BufferedReader(new FileReader(file.getPath()))) {
                        return br.lines().collect(Collectors.toList());
                    } catch (IOException e) {
                        log.error("Failed to read tokens from file", e);
                        throw new ReadDataException("error when readCardTokensFromFIle");
                    }
                }
            }
        }
        return List.of();
    }

    public boolean isFileExist(String currentScheduleTime) {
        final File[] files = readAllFiles();
        return !ArrayUtils.isEmpty(files) && Arrays.stream(files)
                .anyMatch(file -> currentScheduleTime.equals(file.getName()));
    }
}
