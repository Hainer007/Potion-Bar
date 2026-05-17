package hainer.mod.potionbar.config;

import com.mojang.brigadier.arguments.StringArgumentType;
import hainer.mod.potionbar.screen.CustomPositionScreen;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.network.chat.Component;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public final class PotionBarCommands {




    public static volatile boolean pendingCustomScreen = false;

    public static void register() {


        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (pendingCustomScreen && mc.screen == null) {
                pendingCustomScreen = false;
                mc.setScreen(new CustomPositionScreen());
            }
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("potionbar")




                    .then(literal("position")
                            .then(argument("pos", StringArgumentType.word())
                                    .suggests((ctx, b) -> {
                                        b.suggest("center");
                                        b.suggest("left");
                                        b.suggest("right");
                                        b.suggest("custom");
                                        return b.buildFuture();
                                    })
                                    .executes(ctx -> {
                                        String posStr = StringArgumentType.getString(ctx, "pos");
                                        ModSettings.Position pos = ModSettings.Position.fromString(posStr);

                                        if (pos == ModSettings.Position.CUSTOM) {
                                            pendingCustomScreen = true;
                                            ctx.getSource().sendFeedback(
                                                    Component.literal("Opening PotionBar position editor…")
                                            );
                                        } else {
                                            ModSettings.get().setPosition(pos);
                                            ctx.getSource().sendFeedback(
                                                    Component.literal("PotionBar position = " + pos.name().toLowerCase())
                                            );
                                        }
                                        return 1;
                                    })
                            )
                    )




                    .then(literal("vanillaEffects")
                            .then(argument("mode", StringArgumentType.word())
                                    .suggests((ctx, b) -> {
                                        b.suggest("show");
                                        b.suggest("hide");
                                        return b.buildFuture();
                                    })
                                    .executes(ctx -> {
                                        String mode = StringArgumentType.getString(ctx, "mode").toLowerCase();
                                        if (!mode.equals("show") && !mode.equals("hide")) {
                                            ctx.getSource().sendError(
                                                    Component.literal("Usage: /potionbar vanillaEffects show|hide")
                                            );
                                            return 0;
                                        }
                                        boolean show = mode.equals("show");
                                        ModSettings.get().setVanillaEffects(show);
                                        ctx.getSource().sendFeedback(
                                                Component.literal("Vanilla effects = " + (show ? "show" : "hide"))
                                        );
                                        return 1;
                                    })
                            )
                    )




                    .then(literal("timer")
                            .then(argument("mode", StringArgumentType.word())
                                    .suggests((ctx, b) -> {
                                        b.suggest("on");
                                        b.suggest("off");
                                        b.suggest("left");
                                        b.suggest("right");
                                        b.suggest("above");
                                        b.suggest("below");
                                        return b.buildFuture();
                                    })
                                    .executes(ctx -> {
                                        String mode = StringArgumentType.getString(ctx, "mode").toLowerCase();
                                        ModSettings cfg = ModSettings.get();

                                        switch (mode) {
                                            case "on" -> {
                                                cfg.setTimerEnabled(true);
                                                ctx.getSource().sendFeedback(Component.literal("PotionBar timer = on"));
                                            }
                                            case "off" -> {
                                                cfg.setTimerEnabled(false);
                                                ctx.getSource().sendFeedback(Component.literal("PotionBar timer = off"));
                                            }
                                            case "left", "right", "above", "below" -> {
                                                ModSettings.TimerPosition tPos =
                                                        ModSettings.TimerPosition.fromString(mode);
                                                cfg.setTimerPosition(tPos);
                                                ctx.getSource().sendFeedback(
                                                        Component.literal("PotionBar timer position = " + mode)
                                                );
                                            }
                                            default -> ctx.getSource().sendError(
                                                    Component.literal("Usage: /potionbar timer on|off|left|right|above|below")
                                            );
                                        }
                                        return 1;
                                    })
                            )
                    )




                    .then(literal("customIcons")
                            .then(argument("mode", StringArgumentType.word())
                                    .suggests((ctx, b) -> {
                                        b.suggest("on");
                                        b.suggest("off");
                                        return b.buildFuture();
                                    })
                                    .executes(ctx -> {
                                        String mode = StringArgumentType.getString(ctx, "mode").toLowerCase();
                                        if (!mode.equals("on") && !mode.equals("off")) {
                                            ctx.getSource().sendError(
                                                    Component.literal("Usage: /potionbar customIcons on|off")
                                            );
                                            return 0;
                                        }
                                        boolean on = mode.equals("on");
                                        ModSettings.get().setCustomIcons(on);
                                        ctx.getSource().sendFeedback(
                                                Component.literal("PotionBar custom icons = " + (on ? "on" : "off"))
                                        );
                                        return 1;
                                    })
                            )
                    )
            );
        });
    }
}