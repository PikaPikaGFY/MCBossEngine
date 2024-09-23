package io.github.pikapikagfy.mcBossEngine;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;

public class BossBuilder {
    private final BossConfig bossConfig;

    public BossBuilder(BossConfig bossConfig) {
        this.bossConfig = bossConfig;
    }

    // 生成 Boss 实体的方法，传入 Boss 名字和生成位置
    public Entity buildBoss(String bossName, Location location) {
        // 获取当前世界
        World world = location.getWorld();
        if (world == null) return null; // 如果世界为空，返回 null

        // 获取 Boss 的配置
        Map<String, Object> bossData = bossConfig.getBossData(bossName);
        if (bossData == null) {
            Bukkit.getLogger().warning("无法找到名为 " + bossName + " 的 Boss 配置。");
            return null;
        }

        // 获取 Boss 类型
        EntityType entityType = EntityType.valueOf((String) bossData.get("type").toString().toUpperCase());
        if (entityType == null) {
            Bukkit.getLogger().warning("无效的 Boss 类型：" + bossData.get("type"));
            return null;
        }

        // 生成 Boss 主体实体
        LivingEntity boss = (LivingEntity) world.spawnEntity(location, entityType);
        boss.setCustomName(bossName);
        boss.setCustomNameVisible(true);

        // 设置 Boss 的属性
        double health = (double) bossData.get("health");
        boss.setMaxHealth(health);
        boss.setHealth(health);
        boss.setAI(true); // 默认开启 AI

        double speed = (double) bossData.get("speed");
        boss.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);

        // 设置 Boss 大小
        if (bossData.containsKey("size")) {
            double size = (double) bossData.get("size");
            boss.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).setBaseValue(health * size); // 体积影响血量
        }

        // 如果有 parts，生成身体组件
        if (bossData.containsKey("parts")) {
            List<Map<String, Object>> parts = (List<Map<String, Object>>) bossData.get("parts");
            for (Map<String, Object> partData : parts) {
                // 生成每个身体组件
                createBossPart(world, location, partData);
            }
        }

        return boss;
    }

    // 生成 Boss 身体组件的方法
    private void createBossPart(World world, Location bossLocation, Map<String, Object> partData) {
        EntityType partType = EntityType.valueOf(partData.get("type").toString().toUpperCase());
        double size = (double) partData.get("size");

        // 获取 offset 并调整位置
        List<Double> offset = (List<Double>) partData.get("offset");
        Location partLocation = bossLocation.clone().add(new Vector(offset.get(0), offset.get(1), offset.get(2)));

        // 生成部分实体
        LivingEntity part = (LivingEntity) world.spawnEntity(partLocation, partType);
        part.setCustomName(partType.name() + " 部件");
        part.setCustomNameVisible(false);  // 部件可以隐藏名字
        part.setAI(false);  // 身体组件默认无 AI

        // 设置身体组件的碰撞箱和是否可受伤
        boolean collision = (boolean) partData.get("collision");
        part.setCollidable(collision);

        boolean canTakeDamage = (boolean) partData.get("can_take_damage");
        if (!canTakeDamage) {
            part.setInvulnerable(true);  // 设置不可受伤
        }

        // 设置身体组件的体积大小
        part.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).setBaseValue(20 * size);  // 示例：体积影响血量
        part.setHealth(part.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getBaseValue());
    }
}
