package hainer.mod.potionbar;


import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.*;

public class PotionBar implements ClientModInitializer {
	public static final String MODID = "potion-bar";
	private static final Identifier BG_TEXTURE = Identifier.of(MODID, "textures/gui/bg/potion_bg.png");


	// Клас для зберігання даних про іконку та бар для ефекту
	public static class EffectBarData {
		public final Identifier icon;
		public final Identifier bar;
		public final int barWidth;
		public final int barHeight;


		public EffectBarData(Identifier icon, Identifier bar, int barWidth, int barHeight) {
			this.icon = icon;
			this.bar = bar;
			this.barWidth = barWidth;
			this.barHeight = barHeight;
		}
	}

	private static final Map<RegistryEntry<StatusEffect>, EffectBarData> BAR_DATA = new HashMap<>();
	static {
		BAR_DATA.put(StatusEffects.ABSORPTION, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/absorption_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/absorption_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.BAD_OMEN, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/bad_omen_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/bad_omen_bar.png"),
				39, 4
		 ));
		BAR_DATA.put(StatusEffects.BLINDNESS, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/blindness_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/blindness_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.CONDUIT_POWER, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/conduit_power_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/conduit_power_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.DARKNESS, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/darkness_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/darkness_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.DOLPHINS_GRACE, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/dolphins_grace_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/dolphins_grace_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.FIRE_RESISTANCE, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/fire_resistance_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/fire_resistance_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.GLOWING, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/glowing_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/glowing_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.HASTE, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/haste_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/haste_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.HEALTH_BOOST, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/health_boost_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/health_boost_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.HERO_OF_THE_VILLAGE, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/hero_of_the_village_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/hero_of_the_village_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.HUNGER, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/hunger_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/hunger_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.INFESTED, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/infested_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/infested_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.INSTANT_DAMAGE, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/instant_damage_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/instant_damage_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.INSTANT_HEALTH, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/instant_health_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/instant_health_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.INVISIBILITY, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/invisibility_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/invisibility_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.JUMP_BOOST, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/jump_boost_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/jump_boost_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.LEVITATION, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/levitation_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/levitation_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.LUCK, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/luck_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/luck_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.MINING_FATIGUE, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/mining_fatigue_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/mining_fatigue_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.NAUSEA, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/nausea_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/nausea_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.NIGHT_VISION, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/night_vision_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/night_vision_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.OOZING, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/oozing_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/oozing_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.POISON, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/poison_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/poison_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.RAID_OMEN, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/raid_omen_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/raid_omen_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.REGENERATION, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/regeneration_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/regeneration_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.RESISTANCE, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/resistance_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/resistance_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.SATURATION, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/saturation_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/saturation_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.SLOW_FALLING, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/slow_falling_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/slow_falling_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.SLOWNESS, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/slowness_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/slowness_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.SPEED, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/speed_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/speed_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.STRENGTH, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/strength_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/strength_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.TRIAL_OMEN, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/trial_omen_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/trial_omen_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.UNLUCK, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/unluck_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/unluck_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.WATER_BREATHING, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/water_breathing_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/water_breathing_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.WEAKNESS, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/weakness_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/weakness_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.WEAVING, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/weaving_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/weaving_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.WIND_CHARGED, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/wind_charged_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/wind_charged_bar.png"),
				39, 4
		));
		BAR_DATA.put(StatusEffects.WITHER, new EffectBarData(
				Identifier.of(MODID, "textures/gui/icon/wither_icon.png"),
				Identifier.of(MODID, "textures/gui/bar/wither_bar.png"),
				39, 4
		));
	}

	private static final int BAR_OFFSET_X = 19;
	private static final int BAR_OFFSET_Y = 14;
	private static final int BAR_SPACING = 15;

	private record EffectKey(RegistryEntry<StatusEffect> effect, int amplifier) {}

	private final Map<EffectKey, Integer> maxDurations = new HashMap<>();

	@Override
	public void onInitializeClient() {
		HudRenderCallback.EVENT.register(this::onHudRender);
	}

	private void onHudRender(DrawContext context, RenderTickCounter renderTickCounter) {
		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc.player == null) return;

		RenderPipeline pipeline = RenderPipelines.GUI_TEXTURED;

		// Збираємо ефекти, для яких задано бар
		List<StatusEffectInstance> displayedEffects = new ArrayList<>();
		for (StatusEffectInstance effect : mc.player.getStatusEffects()) {
			if (BAR_DATA.containsKey(effect.getEffectType())) {
				displayedEffects.add(effect);
			}
		}

		displayedEffects.sort(Comparator.comparing(
				e -> e.getEffectType().getKey().map(k -> k.getValue().toString()).orElse("unknown")
		));

		Set<EffectKey> currentKeys = new HashSet<>();
		for (StatusEffectInstance effect : displayedEffects) {
			EffectKey key = new EffectKey(effect.getEffectType(), effect.getAmplifier());
			currentKeys.add(key);
			maxDurations.putIfAbsent(key, effect.getDuration());
			if (effect.getDuration() > maxDurations.get(key)) {
				maxDurations.put(key, effect.getDuration());
			}
		}
		maxDurations.keySet().removeIf(key -> !currentKeys.contains(key));

		int x = (mc.getWindow().getScaledWidth() - 64) / 2;
		int yStart = 10;

		for (int i = 0; i < displayedEffects.size(); ++i) {
			StatusEffectInstance effect = displayedEffects.get(i);
			EffectKey key = new EffectKey(effect.getEffectType(), effect.getAmplifier());
			int maxDuration = maxDurations.get(key);

			EffectBarData barData = BAR_DATA.get(effect.getEffectType());
			int y = yStart + i * BAR_SPACING;

			context.drawTexture(pipeline, BG_TEXTURE, x, y, 0, 0, 64, 32, 64, 32);
			context.drawTexture(pipeline, barData.icon, x, y, 0, 0, 64, 32, 64, 32);

			float progress = maxDuration > 0 ? Math.max(0f, Math.min(1f, (float)effect.getDuration() / maxDuration)) : 0f;
			int barWidth = (int)(barData.barWidth * progress);

			if (barWidth > 0) {
				context.drawTexture(pipeline, barData.bar, x + BAR_OFFSET_X, y + BAR_OFFSET_Y, 0, 0, barWidth, barData.barHeight, barData.barWidth, barData.barHeight
				);

			}
		}
	}
}