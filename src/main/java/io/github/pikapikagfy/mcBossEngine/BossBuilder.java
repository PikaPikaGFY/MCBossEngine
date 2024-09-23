package io.github.pikapikagfy.mcBossEngine;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BossBuilder {
    private final BossConfig bossConfig;
    private final Plugin plugin;

    public BossBuilder(BossConfig bossConfig, Plugin plugin) {
        this.bossConfig = bossConfig;
        this.plugin = plugin; // 初始化插件实例
    }

    public LivingEntity buildBoss(String bossName, Location location) {
        // 获取当前世界
        World world = location.getWorld();
        if (world == null) {
            Bukkit.getLogger().warning("世界为空，无法生成 Boss。");
            return null;
        }

        // 获取 Boss 的配置
        Map<String, Object> bossData = bossConfig.getBossData(bossName);
        if (bossData == null) {
            Bukkit.getLogger().warning("无法找到名为 " + bossName + " 的 Boss 配置。");
            return null;
        }

        // 获取 Boss 类型
        EntityType entityType;
        try {
            entityType = EntityType.valueOf(bossData.get("type").toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("无效的 Boss 类型：" + bossData.get("type"));
            return null;
        }

        Bukkit.getLogger().info("生成 Boss: " + bossName + "，类型: " + entityType.name());

        // 生成 Boss 主体实体
        LivingEntity boss = (LivingEntity) world.spawnEntity(location, entityType);
        boss.setCustomName(bossName);
        boss.setCustomNameVisible(true);

        // 设置 Boss 的属性
        double health = (double) bossData.get("health");
        boss.setMaxHealth(health);
        boss.setHealth(health);
        boss.setAI(true); // 默认开启 AI

        // 设置 Boss 的速度
        double speed = (double) bossData.get("speed");
        boss.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);

        // 存储身体组件
        List<Entity> partsList = new ArrayList<>();
        if (bossData.containsKey("parts")) {
            List<Map<String, Object>> parts = (List<Map<String, Object>>) bossData.get("parts");
            for (Map<String, Object> partData : parts) {
                Entity part = createBossPart(world, location, partData);
                partsList.add(part); // 添加到部件列表
            }
        }

        // 创建 Boss 实例并启动位置更新任务
        Boss bossInstance = new Boss(boss, partsList, plugin); // 传递 Plugin 实例
        return boss; // 返回 Boss 实体
    }

    // 生成 Boss 身体组件的方法
    private Entity createBossPart(World world, Location bossLocation, Map<String, Object> partData) {
        // 尝试获取实体类型
        EntityType partType;
        try {
            partType = EntityType.valueOf(partData.get("type").toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().warning("无效的部件类型：" + partData.get("type"));
            return null;
        }

        // 获取偏移量并调整位置
        List<Double> offset = (List<Double>) partData.get("offset");
        Location partLocation = bossLocation.clone().add(new Vector(offset.get(0), offset.get(1), offset.get(2)));

        // 生成部分实体
        LivingEntity part = (LivingEntity) world.spawnEntity(partLocation, partType);
        part.setCustomName(partType.name() + " 部件");
        part.setCustomNameVisible(false);  // 部件可以隐藏名字
        part.setAI(false);  // 身体组件默认无 AI


        return part;
    }
}
