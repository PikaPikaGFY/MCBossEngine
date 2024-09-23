package io.github.pikapikagfy.mcBossEngine;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BossConfig {
    private final FileConfiguration config;

    public BossConfig(FileConfiguration config) {
        this.config = config;
    }

    /**
     * 获取指定 Boss 的配置信息
     * @param bossName Boss 的名字
     * @return 返回包含 Boss 配置的 Map
     */
    public Map<String, Object> getBossData(String bossName) {
        ConfigurationSection bossSection = config.getConfigurationSection("bosses." + bossName);
        if (bossSection == null) return null;

        Map<String, Object> bossData = new HashMap<>();

        // 获取基本属性
        bossData.put("type", bossSection.getString("type"));
        bossData.put("health", bossSection.getDouble("health"));
        bossData.put("speed", bossSection.getDouble("speed"));
        bossData.put("size", bossSection.getDouble("size"));

        // 获取 parts 部分
        List<Map<String, Object>> partsData = getPartsData(bossSection.getConfigurationSection("parts"));
        bossData.put("parts", partsData);

        return bossData;
    }

    /**
     * 获取 Boss 身体组件的配置信息
     * @param partsSection 配置文件中的 "parts" 部分
     * @return 返回每个部分的配置信息
     */
    private List<Map<String, Object>> getPartsData(ConfigurationSection partsSection) {
        List<Map<String, Object>> parts = new ArrayList<>();
        if (partsSection == null) return parts;

        for (String partKey : partsSection.getKeys(false)) {
            ConfigurationSection partSection = partsSection.getConfigurationSection(partKey);
            if (partSection == null) continue;

            Map<String, Object> partData = new HashMap<>();
            partData.put("type", partSection.getString("type"));
            partData.put("size", partSection.getDouble("size"));

            // 获取位置偏移量
            List<Double> offset = partSection.getDoubleList("offset");
            partData.put("offset", offset);

            // 获取是否有碰撞箱和是否可受伤的属性
            partData.put("collision", partSection.getBoolean("collision"));
            partData.put("can_take_damage", partSection.getBoolean("can_take_damage"));

            parts.add(partData);
        }

        return parts;
    }

    /**
     * 获取所有 Boss 的名称
     * @return 返回所有 Boss 名字的 List
     */
    public List<String> getAllBossNames() {
        ConfigurationSection bossesSection = config.getConfigurationSection("bosses");
        if (bossesSection == null) return new ArrayList<>();

        return new ArrayList<>(bossesSection.getKeys(false));
    }
}
