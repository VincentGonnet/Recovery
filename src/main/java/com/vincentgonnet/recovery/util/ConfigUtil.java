package com.vincentgonnet.recovery.util;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class ConfigUtil {
    private File file;
    private FileConfiguration config;

    public ConfigUtil(Plugin plugin, String path) {
        String finalPath = plugin.getDataFolder().getAbsolutePath() + "/" + path;
        File expectedFile = new File(finalPath);
        if (!expectedFile.exists()) {
            copy(plugin.getResource(path), expectedFile);
        }
        this.file = expectedFile;
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }


    private static void copy(InputStream source, File dest) {
        try {
            try (InputStream input = source; OutputStream output = new FileOutputStream(dest)) {
                byte[] buf = new byte[1024];
                int bytesRead;
                while ((bytesRead = input.read(buf)) > 0) {
                    output.write(buf, 0, bytesRead);
                }
                input.close();
                output.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean save() {
        try {
            this.config.save(this.file);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public File getFile() {
        return this.file;
    }

    public FileConfiguration getConfig() {
        return this.config;
    }

    public void reload(){
        this.config = YamlConfiguration.loadConfiguration(this.file);
    }
}
