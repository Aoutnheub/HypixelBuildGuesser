package dev.aoutnheub;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class ModClient implements ClientModInitializer {
    private static KeyBinding openKey;

    @Override @SuppressWarnings({"unchecked"})
    public void onInitializeClient() {
        Path themePath = FabricLoader.getInstance().getConfigDir().resolve("hbg_themes.json");
        File themesFile = themePath.toFile();
        ArrayList<String> themes = new ArrayList<String>();
        if(themesFile.exists()) {
            FileInputStream fis;
            try {
                fis = new FileInputStream(themesFile);
            } catch(Exception e) {
                Mod.log.error("Failed to open themes file: ", e.getMessage());
                return;
            }
            String themesTxt;
            try {
                themesTxt = new String(fis.readAllBytes());
                fis.close();
            } catch(Exception e) {
                Mod.log.error("Failed to read themes: ", e.getMessage());
                return;
            }
            if(themesTxt.length() > 0) {
                Gson gson = new Gson();
                try {
                    themes = gson.fromJson(themesTxt, themes.getClass());
                } catch(JsonSyntaxException e) {
                    Mod.log.error("Failed to parse themes: ", e.getMessage());
                    return;
                }
            }
        } else {
            try {
                FileOutputStream fos = new FileOutputStream(themesFile);
                fos.write("[]".getBytes());
                fos.close();
            } catch(IOException e) {
                Mod.log.error("Failed to create themes file: ", e.getMessage());
                return;
            }
        }

        openKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.hbg.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "category.hbg.idk"
        ));
        var mainGui = new MainGui(themes);
        var mainScreen = new MainScreen(mainGui);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(openKey.wasPressed()) {
                mainGui.updateHint();
                MinecraftClient.getInstance().setScreen(mainScreen);
            }
        });
    }
}
