package hainer.mod.potionbar.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ModSettings {

    public enum Position {
        CENTER, LEFT, RIGHT;

        public static Position fromString(String s) {
            if (s == null) return CENTER;
            return switch (s.toLowerCase()) {
                case "left" -> LEFT;
                case "right" -> RIGHT;
                case "center" -> CENTER;
                default -> CENTER;
            };
        }
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("potion-bar.json");

    private static ModSettings INSTANCE;

    // ===== default values =====
    public Position position = Position.CENTER;
    public boolean vanillaEffects = false;

    private ModSettings() {}

    public static ModSettings get() {
        if (INSTANCE == null) INSTANCE = load();
        return INSTANCE;
    }

    private static ModSettings load() {
        if (!Files.exists(FILE)) {
            ModSettings cfg = new ModSettings();
            cfg.save();
            return cfg;
        }

        try {
            String json = Files.readString(FILE, StandardCharsets.UTF_8);
            ModSettings cfg = GSON.fromJson(json, ModSettings.class);
            if (cfg == null) cfg = new ModSettings();
            cfg.save();
            return cfg;
        } catch (Exception e) {
            ModSettings cfg = new ModSettings();
            cfg.save();
            return cfg;
        }
    }

    public void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(this), StandardCharsets.UTF_8);
        } catch (IOException ignored) {}
    }

    public void setPosition(Position pos) {
        this.position = pos == null ? Position.CENTER : pos;
        save();
    }

    public void setVanillaEffects(boolean show) {
        this.vanillaEffects = show;
        save();
    }

    public boolean showVanillaEffects() {
        return vanillaEffects;
    }
}