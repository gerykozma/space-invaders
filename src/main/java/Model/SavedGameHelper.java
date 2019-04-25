package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.google.gson.Gson;
import org.apache.log4j.Logger;

/**
 * Helper class for handling save (serialization) and load (deserialization) game level operation.
 */
public class SavedGameHelper {
    private static final Logger EVENT_LOGGER = Logger.getLogger(SavedGameHelper.class);
    private static Gson gson = new Gson();

    /**
     * Saves (serializes to json) a given game level to the given path.
     *
     * @param filePath The path to be used for serialization.
     * @param save     Game level to be serialized.
     * @return True if the level was successfully saved.
     */
    public static final boolean saveGame(final String filePath, final GameLevel save) {
        if (save == null) {
            throw new IllegalArgumentException("Save cannot be null");
        }

        String json = gson.toJson(save);
        String fileName = String.format("SavedGame_%s.save", LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE));
        try {
            Files.write(Paths.get(filePath, fileName), json.getBytes());
        } catch (IOException ex) {
            EVENT_LOGGER.error(String.format("Error while saving: %s", ex));
            return false;
        }
        EVENT_LOGGER.info(String.format("Game saved to: %s", Paths.get(filePath, fileName)));
        return true;
    }

    /**
     * Loads (deserializes from json) a given game level from file.
     *
     * @param filePath File for the game level to be loaded from.
     * @return The loaded (deserialized) game level.
     */
    public static final GameLevel loadGame(final String filePath) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            return null;
        }

        List<String> jsonLines;
        try {
            jsonLines = Files.readAllLines(path);
        } catch (IOException ex) {
            EVENT_LOGGER.error(String.format("Error while loading: %s", ex));
            return null;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (String line : jsonLines) {
            stringBuilder.append(line);
        }

        return gson.fromJson(stringBuilder.toString(), GameLevel.class);
    }
}
