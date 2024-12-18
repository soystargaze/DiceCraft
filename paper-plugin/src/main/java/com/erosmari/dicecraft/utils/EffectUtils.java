package com.erosmari.dicecraft.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;

import java.util.Objects;

public class EffectUtils {

    public static void playEffect(LivingEntity entity, FileConfiguration config, boolean isPlayer, String effectType) {
        // Determinamos las claves para partículas y sonidos
        String particleKey = (isPlayer ? "player" : "mob") + effectType + "Particles";
        String soundKey = (isPlayer ? "player" : "mob") + effectType + "Sound";

        // Obtenemos los nombres configurados
        String particleName = config.getString("effects." + particleKey, "block_marker").toLowerCase();
        String soundName = config.getString("effects." + soundKey, "block.note_block.pling").toLowerCase();

        // Manejo de partículas
        Particle particle = Objects.requireNonNullElse(
                Registry.PARTICLE_TYPE.get(NamespacedKey.minecraft(particleName)),
                Particle.BLOCK_MARKER
        );
        entity.getWorld().spawnParticle(particle, entity.getLocation(), 30);

        // Manejo de sonidos
        Sound sound = Objects.requireNonNullElse(
                org.bukkit.Registry.SOUNDS.get(NamespacedKey.minecraft(soundName)),
                Sound.BLOCK_NOTE_BLOCK_PLING
        );
        entity.getWorld().playSound(entity.getLocation(), sound, 1.0f, 1.0f);
    }
}
