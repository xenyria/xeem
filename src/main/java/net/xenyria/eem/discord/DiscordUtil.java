package net.xenyria.eem.discord;

import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DiscordUtil {

    public static final String SDK_VERSION = "3.1.0";
    public static final String SDK_DOWNLOAD_URL = "https://dl-game-sdk.discordapp.net/" + SDK_VERSION + "/discord_game_sdk.zip";

    public static final String SEPARATOR_ZIP = "/";
    public static final String SDK_FILE_NAME = "discord_game_sdk";

    public static final String EXTENSION_LIB_WINDOWS = "dll";
    public static final String EXTENSION_LIB_LINUX = "so";
    public static final String EXTENSION_LIB_MAC = "dylib";

    public static final String LIB_ARCH_X86_64 = "x86_64"; // 64 bit
    public static final String LIB_ARCH_X86 = "x86"; // 32 bit
    public static final String LIB_ARCH_ARM64 = "aarch64"; // 64 bit ARM

    public static final String OS_ARCH_X86_64 = "amd64"; // 64 bit (os.arch)
    public static final String OS_ARCH_X86_64_ALT = "x86_64"; // 64 bit (os.arch) - M1 with x86_64 emulation
    public static final String OS_ARCH_X86 = "x86"; // 32 bit (os.arch)
    public static final String OS_ARCH_ARM = "aarch64"; // 64 bit (os.arch) ARM

    public static String getNativeSDKLibraryFileName() {
        return getNativeSDKLibraryFileName(File.separator);
    }

    public static String getNativeSDKLibraryFileName(String separator) {
        String extension = getNativeLibExtension();
        String architecture = getSystemArchitecture();
        if(SystemUtils.IS_OS_WINDOWS) {
            return architecture + separator + SDK_FILE_NAME + "." + extension;
        } else if(SystemUtils.IS_OS_LINUX) {
            return architecture + separator + SDK_FILE_NAME + "." + extension;
        } else if(SystemUtils.IS_OS_MAC) {
            return architecture + separator + SDK_FILE_NAME + "." + extension;
        }
        throw new RuntimeException("Unsupported operating system: '" + architecture + "'.");
    }

    /**
     * @return Returns the current system architecture
     */
    public static String getSystemArchitecture() {
        String architecture = System.getProperty("os.arch").toLowerCase();
        if(architecture.equalsIgnoreCase(OS_ARCH_X86_64) || architecture.equalsIgnoreCase(OS_ARCH_X86_64_ALT)) {
            return LIB_ARCH_X86_64;
        } else if(architecture.equalsIgnoreCase(OS_ARCH_X86)) {
            return LIB_ARCH_X86;
        } else if(architecture.equalsIgnoreCase(OS_ARCH_ARM)) {
            return LIB_ARCH_ARM64;
        }
        throw new RuntimeException("Unsupported operating system architecture: '" + architecture + "'.");
    }

    /**
     * @return Returns the file extension for the native lib based on the current OS
     */
    public static String getNativeLibExtension() {
        if(SystemUtils.IS_OS_WINDOWS) {
            return EXTENSION_LIB_WINDOWS;
        } else if(SystemUtils.IS_OS_LINUX) {
            return EXTENSION_LIB_LINUX;
        } else if(SystemUtils.IS_OS_MAC) {
            return EXTENSION_LIB_MAC;
        }
        throw new RuntimeException("Unsupported operating system.");
    }

    /**
     * Downloads and extracts the required native library from Discord's Game SDK
     */
    public static void downloadGameSDK(File output) throws IOException {
        // Determine which file we'll need from the zip file
        String targetFileName = "lib/"+getNativeSDKLibraryFileName(SEPARATOR_ZIP);
        // Create a URL and open a connection
        URL downloadURL = new URL(SDK_DOWNLOAD_URL);
        HttpURLConnection connection = (HttpURLConnection) downloadURL.openConnection();

        // Read the downlaaded zip file
        try(ZipInputStream zipInputStream = new ZipInputStream(connection.getInputStream())) {
            ZipEntry currentZipEntry;
            while ((currentZipEntry = zipInputStream.getNextEntry()) != null) {
                // Check if it's the file we are searching for...
                try {
                    if (currentZipEntry.getName().equals(targetFileName)) {
                        if (!output.getParentFile().mkdirs())
                            throw new IOException("Folder could not be created.");
                        // Copy to the system disk
                        Files.copy(zipInputStream, output.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        // Exit from the loop
                        break;
                    }
                } finally {
                    zipInputStream.closeEntry();
                }
            }
        }

    }

}
