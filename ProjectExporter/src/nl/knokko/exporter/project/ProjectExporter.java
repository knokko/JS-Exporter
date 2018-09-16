package nl.knokko.exporter.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ProjectExporter {

	public static void main(String[] args) throws IOException {
		String fileName = "main.html";
		if(args.length == 1)
			fileName = args[0];
		File file = new File(fileName).getAbsoluteFile();
		List<String> lines = new ArrayList<>(10000);
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		String line = reader.readLine();
		while(line != null){
			if(line.contains("<body")){
				lines.add(line);
				lines.add("<script>");
				lines.add("'use strict';");
			}
			else if(line.contains("</body")){
				lines.add("</script>");
				lines.add(line);
			}
			else if(line.contains("<script src=")){
				int startQuote = line.indexOf('"');
				int endQuote = line.lastIndexOf('"');
				String src = line.substring(startQuote + 1, endQuote);
				File srcFile = file.getParentFile();
				while(src.startsWith("../")){
					srcFile = srcFile.getParentFile();
					src = src.substring(3);
				}
				srcFile = new File(srcFile + "/" + src);
				lines.add("");
				lines.add("//source from file " + src);
				BufferedReader srcReader = new BufferedReader(new InputStreamReader(new FileInputStream(srcFile), "UTF-8"));
				String srcLine = srcReader.readLine();
				while(srcLine != null){
					if(srcLine.contains("'use strict';")){}//I will place the use strict at the start of the script
					else {
						lines.add(srcLine);
					}
					srcLine = srcReader.readLine();
				}
				srcReader.close();
			}
			else {
				lines.add(line);
			}
			line = reader.readLine();
		}
		reader.close();
		PrintWriter writer = new PrintWriter(new File("exported.html"), "UTF-8");
		for(String currentLine : lines)
			writer.println(currentLine);
		writer.close();
	}
}