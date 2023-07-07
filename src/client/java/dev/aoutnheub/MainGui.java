package dev.aoutnheub;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.function.BiConsumer;

import org.spongepowered.include.com.google.gson.Gson;

import dev.aoutnheub.mixin.client.IOverlayAccessorMixin;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.impl.client.LibGuiClient;
import io.github.cottonmc.cotton.gui.impl.client.LibGuiConfig;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WListPanel;
import io.github.cottonmc.cotton.gui.widget.WTextField;
import io.github.cottonmc.cotton.gui.widget.data.Insets;

import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class MainGui extends LightweightGuiDescription {
    private ArrayList<String> themes;
    private ArrayList<String> matches = new ArrayList<String>();

    public WTextField searchField;
    public WButton searchButton;
    public WButton addButton;
    public WListPanel<String, ResultWidget> results;
    public BiConsumer<String, ResultWidget> resultsConsumer = (String s, ResultWidget w) -> {
        w.label.setText(Text.literal(s));
        w.onDelete = () -> {
            themes.remove(s);
            matches.remove(s);
            results.layout();
            saveThemes();
        };
    };

    public MainGui(ArrayList<String> themes) {
        LibGuiClient.config = new LibGuiConfig();
        LibGuiClient.config.darkMode = true;

        this.themes = themes;
        WGridPanel root = new WGridPanel(20);
        setRootPanel(root);
        root.setInsets(Insets.ROOT_PANEL);
        root.setGaps(4, 4);

        searchField = new WTextField(Text.literal("Enter hint..."));
        searchField.setMaxLength(32);
        root.add(searchField, 0, 0, 8, 1);

        searchButton = new WButton(Text.literal("Search"));
        searchButton.setOnClick(() -> {
            search();
            results.layout();
        });
        root.add(searchButton, 8, 0, 3, 1);

        addButton = new WButton(Text.literal("+"));
        addButton.setOnClick(() -> {
            this.themes.add(searchField.getText());
            saveThemes();
        });
        root.add(addButton, 11, 0, 1, 1);

        results = new ResultsList<String, ResultWidget>(this.matches, ResultWidget::new, resultsConsumer);
        results.setListItemHeight(23);
        root.add(results, 0, 1, 12, 10);

        root.validate(this);
    }

    @SuppressWarnings("resource")
    public void updateHint() {
        Text hint = ((IOverlayAccessorMixin) MinecraftClient.getInstance().inGameHud).getOverlayMessage();
        if(hint != null) {
            var hintStr = hint.getString();
            var trashLen = "xxThe theme is xx".length();
            if(hintStr.length() > trashLen) {
                searchField.setText(hintStr.substring(trashLen));
            } else {
                searchField.setText(hintStr);
            }
            search();
            results.layout();
        }
    }

    private boolean matchesHint(String hint, String text) {
        if(hint.length() != text.length()) { return false; }
        for(int i = 0; i < hint.length(); i++) {
            if(hint.charAt(i) != '_') {
                if(Character.toLowerCase(hint.charAt(i)) != Character.toLowerCase(text.charAt(i))) {
                    return false;
                }
            } else {
                if(text.charAt(i) == ' ') { return false; }
            }
        }

        return true;
    }

    private void search() {
        matches.clear();
        String hint = searchField.getText();
        for(int i = 0; i < themes.size(); i++) {
            if(matchesHint(hint, themes.get(i))) {
                matches.add(themes.get(i));
            }
        }
    }

    private void saveThemes() {
        Gson gson = new Gson();
        StringBuffer json = new StringBuffer();
        gson.toJson(themes, json);
        Path themePath = FabricLoader.getInstance().getConfigDir().resolve("hbg_themes.json");
        File themesFile = themePath.toFile();
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(themesFile);
            fos.write(json.toString().getBytes());
            fos.close();
        } catch(IOException e) {
            Mod.log.error("Failed to save themes: ", e.getMessage());
        }
    }
}
