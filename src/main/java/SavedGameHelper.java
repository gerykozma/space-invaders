import javax.sql.rowset.spi.XmlWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.google.gson.Gson;
import org.apache.log4j.Logger;

public class SavedGameHelper
{
    private final static Logger EventLogger = Logger.getLogger(SavedGameHelper.class);
    private static Gson gson= new Gson();

    protected static final boolean SaveGame(String filePath, GameLevel save)
    {
        if(save==null)
        {
            throw new IllegalArgumentException("Save cannot be null");
        }

        String json = gson.toJson(save);
        String fileName= String.format("SavedGame_%s.save", LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE));
        try {
            Files.write(Paths.get(filePath, fileName), json.getBytes());
        }catch (IOException ex)
        {
            EventLogger.error(String.format("Error while saving: %s",ex));
            return false;
        }
        EventLogger.info(String.format("Game saved to: %s", Paths.get(filePath, fileName)));
        return true;
    }

    protected static final GameLevel LoadGame(String filePath)
    {
        Path path= Paths.get(filePath);
        if (!Files.exists(path))
        {
            return null;
        }

        List<String> jsonLines;
        try
        {
            jsonLines = Files.readAllLines(path);
        }catch (IOException ex)
        {
            EventLogger.error(String.format("Error while loading: %s", ex));
            return null;
        }

        StringBuilder stringBuilder= new StringBuilder();
        for (String line : jsonLines)
        {
            stringBuilder.append(line);
        }

        return gson.fromJson(stringBuilder.toString(), GameLevel.class);
    }
}
