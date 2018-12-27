package com.rbkmoney.fraudbusters.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

@Slf4j
public class FileUtil {

    public static String getFile(String fileName) {
        String result = "";
        ClassLoader classLoader = FileUtil.class.getClassLoader();
        try {
            result = IOUtils.toString(classLoader.getResourceAsStream(fileName), "UTF8");
        } catch (IOException e) {
            log.error("Error when getFile e: ", e);
        }
        return result;
    }

}
