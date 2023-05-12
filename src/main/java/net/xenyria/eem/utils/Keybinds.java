package net.xenyria.eem.utils;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import net.minecraft.text.Text;
import net.xenyria.eem.PlayingSessionInformation;
import org.lwjgl.glfw.GLFW;

import java.time.Instant;

public class Keybinds {
    private static final KeyBinding Achievements = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.xenyria_eem.achievements",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_K,
            "key_category.xenyria_eem.xenyria"
    ));
    private static final KeyBinding Ping = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.xenyria_eem.ping",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_F8,
            "key_category.xenyria_eem.xenyria"
    ));
    private static final KeyBinding MusicSettings = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.xenyria_eem.music_settings",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_F7,
            "key_category.xenyria_eem.xenyria"
    ));
    private static final KeyBinding Settings = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.xenyria_eem.settings",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_F6,
            "key_category.xenyria_eem.xenyria"
    ));
    private static final KeyBinding Stats = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.xenyria_eem.stats",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_F4,
            "key_category.xenyria_eem.xenyria"
    ));
    private static final KeyBinding Lobby = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.xenyria_eem.lobby",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "key_category.xenyria_eem.xenyria"
    ));
    private static final KeyBinding Hub = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.xenyria_eem.hub",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UNKNOWN,
            "key_category.xenyria_eem.xenyria"
    ));

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(client.player == null) return;
            registerCommandKeybind(client, Achievements, "achievements");
            registerCommandKeybind(client, Ping, "ping");
            registerCommandKeybind(client, MusicSettings, "music");
            registerCommandKeybind(client, Settings, "settings");
            registerCommandKeybind(client, Stats, "stats");
            registerCommandKeybind(client, Lobby, "lobby");
            registerCommandKeybind(client, Hub, "hub");
        });
    }

    private static void registerCommandKeybind(MinecraftClient client, KeyBinding keybinding, String command) {
        // Check if the user is on the server since you don't want to trigger certain macros on other servers
        if(!PlayingSessionInformation.isOnNetwork()) { return; }
        while (keybinding.wasPressed()) client.player.networkHandler.sendChatCommand(command);
    }
}
