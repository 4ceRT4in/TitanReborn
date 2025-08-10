package net.shirojr.titanfabric.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class TitanConfig {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Path configPath = FabricLoader.getInstance().getConfigDir().resolve("titanfabric-config.json");
    private static ConfigData configData;

    public static void loadConfig() {
        if (!Files.exists(configPath)) {
            configData = new ConfigData();
            saveConfig();
            return;
        }

        try (FileReader reader = new FileReader(configPath.toFile())) {
            configData = gson.fromJson(reader, ConfigData.class);
            if (configData == null) {
                configData = new ConfigData();
            }
        } catch (IOException e) {
            configData = new ConfigData();
        }
    }

    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(configPath.toFile())) {
            gson.toJson(configData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getBlockedEnchantments() {
        return configData.blockedEnchantments;
    }

    private static class ConfigData {
        public List<String> blockedEnchantments = new ArrayList<>();
    }
}