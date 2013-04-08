package com.hello2morrow.sonargraph.jenkinsplugin.persistence;

import java.io.BufferedReader;
import java.io.IOException;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileReader;

public class TextFileReader
{
    public String readLargeTextFile(String largeTextFilePath) throws IOException
    {
        BufferedReader bfReader = null;
        StringBuilder completeTextFile = new StringBuilder();
        bfReader = new BufferedReader(new TFileReader(new TFile(largeTextFilePath)));
        String currentLine;
        while ((currentLine = bfReader.readLine()) != null)
        {
            completeTextFile.append(currentLine);
        }
        bfReader.close();
        return completeTextFile.toString();
    }
}