package com.cts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CsvXmlReader {
	
	static String inputPath= "c://input";
	static String outputPath= "c://output";
	static String outputFileName= "output.txt";
	
	public void readFiles() {
		FileWriter fw = null;
		File outputPathFileObj = new File(outputPath);
		if(!outputPathFileObj.exists())
			outputPathFileObj.mkdir();
		File outputfile = new File(outputPath, outputFileName);
		if (outputfile.exists()) {
			// delete if exists
			outputfile.delete();
		}

		try {
			fw = new FileWriter(outputfile, true);
			XmlParsingUsingSax xsax = new XmlParsingUsingSax();
			File folder = new File(inputPath);
			File[] listOfFiles = folder.listFiles();
			if(listOfFiles == null) {
				System.out.println(inputPath +" diesn't exists");
				return;
			}
			String extension = null;
			String line = "";
			String cvsSplitBy = ",";
			String path = null;
			String[] input = null;

			for (File file : listOfFiles) {

				if (file.isFile()) {

					extension = getFileExtension(file);

					if (extension.equals("csv")) {
						path = file.getAbsolutePath();

						getDataFromCsv(path, fw);
						System.out.println(path);
					}

					if (extension.equals("xml")) {
						path = file.getAbsolutePath();

						getDataFromXml(path,xsax, fw);

						System.out.println(path);
					}

				}
			}

		} catch (IOException e) {

			e.printStackTrace();
		} finally {

			try {

				if (fw != null)
					fw.flush();
				fw.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}
	}
	
	public static void main(String args[]) {

		CsvXmlReader csvXmlReader =  new CsvXmlReader();
		csvXmlReader.readFiles();
		
	}

	private static String getFileExtension(File file) {
		String fileName = file.getName();
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		else
			return "";
	}

	private static void getDataFromCsv(String path, FileWriter fw) throws IOException {
		String line = "";
		String cvsSplitBy = ",";

		String[] input = null;
		Set<String> set = new HashSet<String>();
		try {
			FileReader fr = new FileReader(path);
			try (BufferedReader br = new BufferedReader(fr)) {
				br.readLine();
				while ((line = br.readLine()) != null) {

					input = line.split(cvsSplitBy);

					if (set.add(input[0]) == false) {
						fw.write(String.format("%20s %20s %30s \r\n", "DuplicateReferenceNumber", "Account Number",
								"Description"));
						fw.write(String.format("%10s %35s %35s", input[0], input[1], input[2]));
						fw.write("\r\n");

						//System.out.println("duplicate element is=" + input[0]);
					}

					float startbalance = Float.parseFloat(input[3]);
					float mutation = Float.parseFloat(input[4]);
					float endbalance = Float.parseFloat(input[5]);
					float endbalancesum = startbalance + mutation;
					float finalendbalancesum = (float) (Math.round(endbalancesum * 100.0) / 100.0);

					if (finalendbalancesum != endbalance) {
						//System.out.println("PROBLEM element is=" + input[3]);
						fw.write(String.format("%20s %15s %30s \r\n", "EndBalanceMismatchReferenceNo", "Account Number",
								"Description"));
						fw.write(String.format("%10s %35s %35s", input[5], input[1], input[0]));

						fw.write("\r\n");
					}

				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void getDataFromXml(String path, XmlParsingUsingSax xsax, FileWriter fw) throws IOException {
		
		Set<String> set = new HashSet<String>();
		List<Account> emp = xsax.getElement(path);

		for (Account e : emp) {
			//System.out.println("Reference: " + e.attributes.get("reference"));
			//System.out.println("  Accountnumber: " + e.accountNumber);
			//System.out.println("  Description: " + e.description);
			//System.out.println("  StartBalance: " + e.startBalance);
			//System.out.println("  Mutation: " + e.mutation);
			//System.out.println("  EndBalance: " + e.endBalance);

			if (set.add((String) e.attributes.get("reference")) == false) {
				fw.write(String.format("%20s %20s %30s \r\n", "DuplicateReferenceNumber", "Account Number",
						"Description"));
				fw.write(
						String.format("%10s %35s %35s", e.attributes.get("reference"), e.accountNumber, e.description));
				//System.out.println("duplicate element isisis=" + e.attributes.get("reference"));

				fw.write("\r\n");
			}
			float startbalance = Float.parseFloat(e.startBalance);
			float mutation = Float.parseFloat(e.mutation);
			float endbalance = Float.parseFloat(e.endBalance);
			float endbalancesum = startbalance + mutation;
			float finalendbalancesum = (float) (Math.round(endbalancesum * 100.0) / 100.0);

			if (finalendbalancesum != endbalance) {
				//System.out.println("PROBLEM element is=" + e.startBalance);
				fw.write(String.format("%20s %15s %30s \r\n", "EndBalanceMismatchReferenceNo", "Account Number",
						"Description"));
				fw.write(
						String.format("%10s %35s %35s", e.attributes.get("reference"), e.accountNumber, e.description));

				fw.write("\r\n");
			}
		}

	}

}