package io.github.pikapikagfy.mcBossEngine;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigReader {
    private final JavaPlugin plugin;  // 使用 JavaPlugin 而不是 BossEngine

    public ConfigReader(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public BossConfig loadBossConfig() {
        FileConfiguration config = plugin.getConfig();  // 获取配置文件
        return new BossConfig(config);
    }

    public SkillConfig loadSkillConfig() {
        FileConfiguration config = plugin.getConfig();
        return new SkillConfig(config);
    }
}
