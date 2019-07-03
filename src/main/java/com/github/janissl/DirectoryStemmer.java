package com.github.janissl;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.bg.BulgarianAnalyzer;
import org.apache.lucene.analysis.bn.BengaliAnalyzer;
import org.apache.lucene.analysis.ca.CatalanAnalyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.ckb.SoraniAnalyzer;
import org.apache.lucene.analysis.cz.CzechAnalyzer;
import org.apache.lucene.analysis.da.DanishAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.el.GreekAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.eu.BasqueAnalyzer;
import org.apache.lucene.analysis.fa.PersianAnalyzer;
import org.apache.lucene.analysis.fi.FinnishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.ga.IrishAnalyzer;
import org.apache.lucene.analysis.gl.GalicianAnalyzer;
import org.apache.lucene.analysis.hi.HindiAnalyzer;
import org.apache.lucene.analysis.hu.HungarianAnalyzer;
import org.apache.lucene.analysis.hy.ArmenianAnalyzer;
import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.lt.LithuanianAnalyzer;
import org.apache.lucene.analysis.lv.LatvianAnalyzer;
import org.apache.lucene.analysis.nl.DutchAnalyzer;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.analysis.pt.PortugueseAnalyzer;
import org.apache.lucene.analysis.ro.RomanianAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.sv.SwedishAnalyzer;
import org.apache.lucene.analysis.th.ThaiAnalyzer;
import org.apache.lucene.analysis.uk.UkrainianMorfologikAnalyzer;
import org.apache.lucene.analysis.morfologik.MorfologikAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tr.TurkishAnalyzer;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DirectoryStemmer {
    private File sourceDirectory;
    private File targetDirectory;

    private DirectoryStemmer(File srcDir, File trgDir) {
        sourceDirectory = srcDir;
        targetDirectory = trgDir;
    }

    private String stemWord(String term, String lang) throws IOException {
        String stem = null;
        Analyzer analyzer;
        TokenStream stream;

        switch (lang) {
            case "ar":
                analyzer = new ArabicAnalyzer();
                break;
            case "bg":
                analyzer = new BulgarianAnalyzer();
                break;
            case "bn":
                analyzer = new BengaliAnalyzer();
                break;
            case "ca":
                analyzer = new CatalanAnalyzer();
                break;
            case "cs":
                analyzer = new CzechAnalyzer();
                break;
            case "da":
                analyzer = new DanishAnalyzer();
                break;
            case "de":
                analyzer = new GermanAnalyzer();
                break;
            case "el":
                analyzer = new GreekAnalyzer();
                break;
            case "en":
                analyzer = new EnglishAnalyzer();
                break;
            case "es":
                analyzer = new SpanishAnalyzer();
                break;
            case "eu":
                analyzer = new BasqueAnalyzer();
                break;
            case "fa":
                analyzer = new PersianAnalyzer();
                break;
            case "fi":
                analyzer = new FinnishAnalyzer();
                break;
            case "fr":
                analyzer = new FrenchAnalyzer();
                break;
            case "ga":
                analyzer = new IrishAnalyzer();
                break;
            case "gl":
                analyzer = new GalicianAnalyzer();
                break;
            case "hi":
                analyzer = new HindiAnalyzer();
                break;
            case "hu":
                analyzer = new HungarianAnalyzer();
                break;
            case "hy":
                analyzer = new ArmenianAnalyzer();
                break;
            case "id":
                analyzer = new IndonesianAnalyzer();
                break;
            case "it":
                analyzer = new ItalianAnalyzer();
                break;
            case "ja":
            case "ko":
            case "zh":
                analyzer = new CJKAnalyzer();
                break;
            case "ku":
                analyzer = new SoraniAnalyzer();
                break;
            case "lt":
                analyzer = new LithuanianAnalyzer();
                break;
            case "lv":
                analyzer = new LatvianAnalyzer();
                break;
            case "nl":
                analyzer = new DutchAnalyzer();
                break;
            case "no":
                analyzer = new NorwegianAnalyzer();
                break;
            case "pl":
                analyzer = new MorfologikAnalyzer();
                break;
            case "pt":
                analyzer = new PortugueseAnalyzer();
                break;
            case "ro":
                analyzer = new RomanianAnalyzer();
                break;
            case "ru":
                analyzer = new RussianAnalyzer();
                break;
            case "sv":
                analyzer = new SwedishAnalyzer();
                break;
            case "th":
                analyzer = new ThaiAnalyzer();
                break;
            case "tr":
                analyzer = new TurkishAnalyzer();
                break;
            case "uk":
                analyzer = new UkrainianMorfologikAnalyzer();
                break;
            default:
                analyzer = new StandardAnalyzer();
        }

        stream = analyzer.tokenStream(null, term);
        stream.reset();
        while (stream.incrementToken()) {
            stem = stream.getAttribute(CharTermAttribute.class).toString();
        }

        stream.end();
        stream.close();

        return stem;
    }

    private String stemSentence(String sentence, String lang) throws IOException {
        String[] words = sentence.split("[\\p{Punct}\\s]+");
        List<String> stemmedWords = new ArrayList<>();
        String stemmedWord;

        for (String word : words) {
            stemmedWord = stemWord(word, lang);
            if (stemmedWord != null) {
                stemmedWords.add(stemmedWord);
            } else {
                stemmedWords.add(word);
            }
        }

        return String.join(" ", stemmedWords);
    }

    private List<String> stemFile(File inputFile) throws IOException {
        Charset charset = Charset.forName("UTF-8");
        List<String> stemmedLines = new ArrayList<>();
        String lang = getFileLanguage(inputFile.getName());

        try (BufferedReader reader = Files.newBufferedReader(inputFile.toPath(), charset)) {
            String line;
            while ((line = reader.readLine()) != null) {
                stemmedLines.add(stemSentence(line.trim(), lang));
            }
        }

        return stemmedLines;
    }

    private void stemDirectory() {
        File[] sourceFiles = sourceDirectory.listFiles();

        if (sourceFiles != null) {
            try {
                for (File sourceFile : sourceFiles) {
                    if (sourceFile.getName().endsWith(".snt")) {
                        File stemmedFile = targetDirectory.toPath().resolve(sourceFile.getName()).toFile();
                        List<String> stemmedLines = stemFile(sourceFile);
                        writeStemmedFile(stemmedFile, stemmedLines);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void writeStemmedFile(File outputFile, List<String> lines) throws IOException {
        Path fileLocation = Paths.get(outputFile.getParent());
        if (Files.notExists(fileLocation)) {
            Files.createDirectory(fileLocation);
        }

        Charset charset = Charset.forName("UTF-8");

        try (BufferedWriter writer = Files.newBufferedWriter(outputFile.toPath(), charset)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    private static String getFileLanguage(String fileName) {
        return fileName.substring(fileName.lastIndexOf('_') + 1, fileName.lastIndexOf('.'));
    }

    public static void main(String[] args) {
        File sourceDirectory = new File(args[0]);
        File destinationDirectory = new File(args[1]);

        if ((Files.exists(sourceDirectory.toPath()) && Files.exists(destinationDirectory.toPath()))) {
            DirectoryStemmer ds = new DirectoryStemmer(sourceDirectory, destinationDirectory);
            ds.stemDirectory();
        } else {
            System.err.println("The source directory and the destination directory must exist!");
        }
    }
}
