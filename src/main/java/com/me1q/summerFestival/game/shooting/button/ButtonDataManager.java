package com.me1q.summerFestival.game.shooting.button;

import com.me1q.summerFestival.SummerFestival;
import com.me1q.summerFestival.game.shooting.spawner.SpawnArea;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ButtonDataManager {

    private static final String DATA_FILE_NAME = "shooting_buttons.json";
    private final File dataFile;
    private final List<ButtonConfig> buttonConfigs;

    public ButtonDataManager(SummerFestival plugin) {
        this.dataFile = new File(plugin.getDataFolder(), DATA_FILE_NAME);
        this.buttonConfigs = new ArrayList<>();
        loadData();
    }

    public void addButton(Location buttonLocation, SpawnArea spawnArea) {
        removeButton(buttonLocation);
        buttonConfigs.add(new ButtonConfig(buttonLocation, spawnArea));
        saveData();
    }

    public void removeButton(Location buttonLocation) {
        buttonConfigs.removeIf(config -> config.isButtonAt(buttonLocation));
        saveData();
    }

    public ButtonConfig getButtonConfig(Location buttonLocation) {
        return buttonConfigs.stream()
            .filter(config -> config.isButtonAt(buttonLocation))
            .findFirst()
            .orElse(null);
    }

    public List<ButtonConfig> getAllButtons() {
        return new ArrayList<>(buttonConfigs);
    }

    @SuppressWarnings("unchecked")
    private void saveData() {
        try {
            if (!dataFile.getParentFile().exists()) {
                dataFile.getParentFile().mkdirs();
            }

            JSONArray jsonArray = new JSONArray();
            for (ButtonConfig config : buttonConfigs) {
                JSONObject buttonObj = new JSONObject();
                Location loc = config.getButtonLocation();

                buttonObj.put("world", loc.getWorld().getName());
                buttonObj.put("x", loc.getBlockX());
                buttonObj.put("y", loc.getBlockY());
                buttonObj.put("z", loc.getBlockZ());

                SpawnArea area = config.getSpawnArea();
                JSONObject areaObj = new JSONObject();
                areaObj.put("x1", area.getMinX());
                areaObj.put("y1", area.getMinY());
                areaObj.put("z1", area.getMinZ());
                areaObj.put("x2", area.getMaxX());
                areaObj.put("y2", area.getMaxY());
                areaObj.put("z2", area.getMaxZ());

                buttonObj.put("spawnArea", areaObj);
                jsonArray.add(buttonObj);
            }

            try (FileWriter writer = new FileWriter(dataFile)) {
                writer.write(jsonArray.toJSONString());
            }
        } catch (IOException e) {
            SummerFestival.getInstance().getLogger()
                .severe("Failed to save button data: " + e.getMessage());
        }
    }

    private void loadData() {
        if (!dataFile.exists()) {
            return;
        }

        try (FileReader reader = new FileReader(dataFile)) {
            JSONParser parser = new JSONParser();
            JSONArray jsonArray = (JSONArray) parser.parse(reader);

            buttonConfigs.clear();
            for (Object obj : jsonArray) {
                JSONObject buttonObj = (JSONObject) obj;

                String worldName = (String) buttonObj.get("world");
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    continue;
                }

                int x = ((Long) buttonObj.get("x")).intValue();
                int y = ((Long) buttonObj.get("y")).intValue();
                int z = ((Long) buttonObj.get("z")).intValue();
                Location buttonLocation = new Location(world, x, y, z);

                JSONObject areaObj = (JSONObject) buttonObj.get("spawnArea");
                double x1 = ((Number) areaObj.get("x1")).doubleValue();
                double y1 = ((Number) areaObj.get("y1")).doubleValue();
                double z1 = ((Number) areaObj.get("z1")).doubleValue();
                double x2 = ((Number) areaObj.get("x2")).doubleValue();
                double y2 = ((Number) areaObj.get("y2")).doubleValue();
                double z2 = ((Number) areaObj.get("z2")).doubleValue();

                SpawnArea spawnArea = new SpawnArea(x1, y1, z1, x2, y2, z2);
                buttonConfigs.add(new ButtonConfig(buttonLocation, spawnArea));
            }
        } catch (Exception e) {
            SummerFestival.getInstance().getLogger()
                .severe("Failed to load button data: " + e.getMessage());
        }
    }
}

