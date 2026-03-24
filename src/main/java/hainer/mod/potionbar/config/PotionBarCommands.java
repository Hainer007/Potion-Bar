package hainer.mod.potionbar.config;

import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.network.chat.Component;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

import com.mojang.brigadier.arguments.StringArgumentType;

public final class PotionBarCommands {

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(literal("potionbar")

                    .then(literal("position")
                            .then(argument("pos", StringArgumentType.word())
                                    .suggests((ctx, b) -> {
                                        b.suggest("center");
                                        b.suggest("left");
                                        b.suggest("right");
                                        return b.buildFuture();
                                    })
                                    .executes(ctx -> {
                                        String posStr = StringArgumentType.getString(ctx, "pos");
                                        ModSettings.Position pos =
                                                ModSettings.Position.fromString(posStr);

                                        ModSettings.get().setPosition(pos);

                                        ctx.getSource().sendFeedback(
                                                Component.literal("PotionBar position = " + pos.name().toLowerCase())
                                        );
                                        return 1;
                                    })
                            )
                    )

                    .then(literal("vanilaEffects")
                            .then(argument("mode", StringArgumentType.word())
                                    .suggests((ctx, b) -> {
                                        b.suggest("show");
                                        b.suggest("hide");
                                        return b.buildFuture();
                                    })
                                    .executes(ctx -> {
                                        String mode = StringArgumentType.getString(ctx, "mode").toLowerCase();

                                        boolean show = mode.equals("show");
                                        if (!mode.equals("show") && !mode.equals("hide")) {
                                            ctx.getSource().sendError(
                                                    Component.literal("Use: /potionbar vanilaEffects show|hide")
                                            );
                                            return 0;
                                        }

                                        ModSettings.get().setVanillaEffects(show);

                                        ctx.getSource().sendFeedback(
                                                Component.literal("Vanilla effects = " + (show ? "show" : "hide"))
                                        );
                                        return 1;
                                    })
                            )
                    )
            );
        });
    }
}