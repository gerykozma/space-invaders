package Model;

import org.apache.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Helper class to save player score and determine high scores.
 * */
public final class HighScoreHelper
{
    private static final Logger EventLogger = Logger.getLogger(HighScoreHelper.class);


    /**
     * Saves the given score to the default output file.
     * @param score Score to be saved.
     * @throws IOException If an error occurs in file handling.
     * @return True if the score was high score.
     * */
    public static boolean SaveScore(Integer score) throws IOException
    {
        return SaveScore(score, "PlayerScores.score");
    }

    /**
     * Saves the given score to the given output file.
     * @param score Score to be saved.
     * @param fileName File to store scores.
     * @throws IOException If an error occurs in file handling.
     * @return True if the score was high score.
     * */
    public static boolean SaveScore(Integer score, String fileName) throws IOException
    {
        List<Integer> playerScores;
        try
        {
            playerScores = GetPlayerScoresFromFile(fileName);
        }
        catch (IOException ex)
        {
            EventLogger.error(ex);
            throw ex;
        }

        playerScores.add(score);
        List<Integer> sortedScores = playerScores.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());

        File scoresFile = new File(fileName);

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

        if(sortedScores.get(0).equals(score))
        {
            return true;
        }
        return false;
    }

    private static ArrayList<Integer> GetPlayerScoresFromFile(String fileName) throws IOException
    {
        File highScores= new File(fileName);        if(!highScores.exists())
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

    private static Integer TryParseStringToInteger(String line)
    {
        try
        {
            return Integer.parseInt(line);
        }
        catch (NumberFormatException ex)
        {
            EventLogger.warn(String.format("Unable to parse score. %s",ex));
            return 0;
        }
    }
}