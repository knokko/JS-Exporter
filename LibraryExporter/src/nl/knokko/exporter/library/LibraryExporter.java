package nl.knokko.exporter.library;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class LibraryExporter {
    
    public static void main(String[] args){
        List<String> lines;
        if(args.length > 0)
            lines = new ArrayList<>(Integer.parseInt(args[0]));
        else
            lines = new ArrayList<>(10000);
        String libraryName = new File("").getAbsoluteFile().getName();
        lines.add("'use strict';");
        List<String> files;
        try {
            files = new ArrayList<>();
            try (BufferedReader orderReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("order.txt"))))) {
                String line = orderReader.readLine();
                while(line != null){
                    files.add(line);
                    line = orderReader.readLine();
                }
            }
        } catch(IOException ioex){
            System.out.println("Can't find order.txt: " + ioex.getMessage());
            return;
        }
        try {
            for(String file : files){
                lines.add("");
                lines.add("//library source from " + libraryName + "/" + file);
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file)), "UTF-8"))) {
                    String line = reader.readLine();
                    while(line != null){
                        if(!line.equals("'use strict';") && !line.equals('"' + "use strict" + '"' + ";")){
                            lines.add(line);
                        }
                        line = reader.readLine();
                    }
                }
            }
            
            try (PrintWriter writer = new PrintWriter(new File("").getAbsoluteFile().getParentFile() + "/" + libraryName + ".js", "UTF-8")) {
                for(String line : lines)
                    writer.println(line);
            }
        } catch(IOException ioex){
            throw new RuntimeException(ioex);
        }
    }
}