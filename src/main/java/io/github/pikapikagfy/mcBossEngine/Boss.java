package io.github.pikapikagfy.mcBossEngine;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.List;

public class Boss {
    private Entity entity;
    private List<Entity> parts;

    public Boss(Entity entity, List<Entity> parts, Plugin plugin) {
        this.entity = entity;
        this.parts = parts;

        // 启动定时任务，每个 tick 更新组件位置
        Bukkit.getScheduler().runTaskTimer(plugin, this::updatePartsPosition, 0L, 1L);
    }

    public Entity getEntity() {
        return entity;
    }

    public List<Entity> getParts() {
        return parts;
    }

    // 更新身体组件的位置
    public void updatePartsPosition() {
        for (int i = 0; i < parts.size(); i++) {
            Entity part = parts.get(i);
            // 这里获取偏移量，可以从配置中读取
            Vector offset = new Vector(0, 0, 0); // 示例偏移量
            part.teleport(entity.getLocation().add(offset));
        }
    }
}
