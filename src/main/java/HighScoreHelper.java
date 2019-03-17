import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class HighScoreHelper
{
    private static final Logger EventLogger = Logger.getLogger(HighScoreHelper.class);

    public static final boolean SaveScore(Integer score) throws IOException
    {
        List<Integer> playerScores;
        try
        {
            playerScores = GetPlayerScoresFromFile();
        }
        catch (IOException ex)
        {
           throw ex;
        }

        playerScores.add(score);
        List<Integer> sortedScores = playerScores.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());

        File scoresFile = new File("PlayerScores.score");

        try(BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(scoresFile)))) {
            for (Integer sortedScore : sortedScores)
            {
                bw.write(sortedScore.toString());
                bw.newLine();
            }
        }
        catch (IOException ex)
        {
            EventLogger.error(ex);
            throw ex;
        }

        if(sortedScores.get(0) == score)
        {
            return true;
        }
        return false;
    }

    private static final ArrayList<Integer> GetPlayerScoresFromFile() throws IOException
    {
        File highScores= new File("PlayerScores.score");
        if(!highScores.exists())
        {
            return new ArrayList<Integer>(0);
        }

        ArrayList<Integer> entries=new ArrayList<Integer>();

        try (Stream<String> lines = Files.lines(highScores.toPath()))
        {
            lines.forEachOrdered(line-> entries.add(TryParseStringToInteger(line)));
        }
        catch (IOException ex)
        {
            EventLogger.error("Error while reading score file: %s", ex);
            throw ex;
        }

        return entries;
    }

    private static final Integer TryParseStringToInteger(String line)
    {
        try
        {
            return Integer.parseInt(line);
        }
        catch (NumberFormatException ex)
        {
            return new Integer(0);
        }
    }
}
