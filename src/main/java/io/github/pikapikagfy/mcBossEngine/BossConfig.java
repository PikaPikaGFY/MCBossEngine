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

    public Map<String, Object> getBossData(String bossName) {
        ConfigurationSection bossSection = config.getConfigurationSection("bosses." + bossName);
        if (bossSection == null) return null;

        Map<String, Object> bossData = new HashMap<>();

        // 获取基本属性
        bossData.put("type", bossSection.getString("type"));
        bossData.put("health", bossSection.getDouble("health"));
        bossData.put("speed", bossSection.getDouble("speed"));
        bossData.put("size", bossSection.getDouble("size"));

        // 获取 parts 部分 (作为列表)
        List<Map<String, Object>> partsData = getPartsData(bossSection);
        bossData.put("parts", partsData);

        return bossData;
    }

    private List<Map<String, Object>> getPartsData(ConfigurationSection bossSection) {
        List<Map<String, Object>> parts = new ArrayList<>();

        // 获取 parts 列表
        List<Map<?, ?>> partsList = bossSection.getMapList("parts");
        if (partsList == null || partsList.isEmpty()) return parts;

        // 遍历 parts 列表
        for (Map<?, ?> partMap : partsList) {
            Map<String, Object> partData = new HashMap<>();

            partData.put("type", partMap.get("type"));
            partData.put("size", partMap.get("size"));

            // 获取偏移量
            List<Double> offset = (List<Double>) partMap.get("offset");
            partData.put("offset", offset);

            // 获取碰撞和受伤属性
            partData.put("collision", partMap.get("collision"));
            partData.put("can_take_damage", partMap.get("can_take_damage"));

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
