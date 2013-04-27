package mymdp.solver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class SolveCaller {

    private String amplLocation;
    private String fileName;
    private Map<String, Float> currentValuesProb;
    private List<String> variablesName;
    public float value;
    private String log;

    public SolveCaller(final String amplFile) {
	this.amplLocation = amplFile;
	currentValuesProb = new Hashtable<String, Float>();
	variablesName = new ArrayList<String>();
    }

    public void clearValues() {
	variablesName = new ArrayList<String>();
	currentValuesProb = new Hashtable<String, Float>();
    }

    public void salveAMPLFile(final List<String> listaLegal, final List<String> listVariables, final List<String> listConstraint,
	    final boolean isMaximize) {
	String fileData = "";
	try {
	    final Writer output = new BufferedWriter(new FileWriter(amplLocation + fileName));

	    setVariablesName(listVariables);

	    for (final String s : listVariables) {
		output.write("var " + s + ">=0, <=1;\n");
	    }

	    fileData = listaLegal.get(0);
	    for (int i = 1; i < listaLegal.size(); i++) {
		fileData += " + " + listaLegal.get(i);
	    }

	    if (isMaximize) {
		output.write("maximize Profit: " + fileData + ";\n");
	    } else {
		output.write("minimize Profit: " + fileData + ";\n");
	    }

	    int i = 0;
	    for (final String s : listConstraint) {
		output.write("subject to Constraint" + i++ + ": " + s + ";\n");
	    }

	    output.close();
	} catch (final IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    public void callSolver() {

	log = "";
	try {
	    // Open files for reading and writing
	    // BufferedReader fis_reader = new BufferedReader(_rReader);
	    final Process pros = Runtime.getRuntime().exec(amplLocation + "ampl");
	    final BufferedReader process_out = new BufferedReader(new InputStreamReader(pros.getInputStream()));
	    final PrintWriter process_in = new PrintWriter(pros.getOutputStream(), true);

	    process_in.println("model '" + amplLocation + fileName + "';");
	    process_in.println("option solver '" + amplLocation + "minos';");
	    process_in.println("solve;");

	    String aux = variablesName.get(0);
	    for (int i = 1; i < variablesName.size(); i++) {
		aux += "," + variablesName.get(i);
	    }

	    process_in.println("display " + aux + ";");
	    process_in.flush();

	    // Provide input to process (could come from any stream)
	    String line = null;
	    process_in.close(); // Need to close input stream so process
				// exits!!!

	    // Get output from process (can also be used by BufferedReader to
	    // get
	    // line-by-line... see how fis_reader is constructed).
	    boolean foundObj = false;
	    while (!foundObj && (line = process_out.readLine()) != null) {
		log += line;

		final int pos = line.indexOf("objective");
		if (pos >= 0) {
		    final int profit = line.indexOf("Profit");
		    if (profit >= 0) {
			try {
			    value = Float.valueOf(line.substring(profit + 8, line.length() - 1));
			    // pos+characters of objective +1
			} catch (final RuntimeException e) {
			    while ((line = process_out.readLine()) != null) {
				log += line;
			    }
			    throw e;
			}
		    } else {
			try {
			    value = Float.valueOf(line.substring(pos + 10));
			    // pos+characters of objective +1
			} catch (final RuntimeException e) {
			    while ((line = process_out.readLine()) != null) {
				log += line;
			    }
			    throw e;
			}
			foundObj = true;
		    }
		}
	    }

	    while ((line = process_out.readLine()) != null) {
		log += line;
		if (line.indexOf("=") > 0) {
		    final String key = line.substring(0, line.indexOf("=") - 1).trim();
		    final String value = line.substring(line.indexOf("=") + 1, line.length()).trim();
		    if (!key.equals("") && !value.equals("")) {
			try {
			    currentValuesProb.put(key, Float.valueOf(value));
			} catch (final NumberFormatException e) {
			    System.out.println(value);
			    throw e;
			}
		    }
		}
	    }
	    process_out.close();

	    pros.waitFor();

	} catch (final InterruptedException ie) {
	    System.out.println(ie);
	    System.out.println("interrupted");
	} catch (final IOException ioe) {
	    System.out.println(ioe);
	    System.out.println("ioexception");
	}
    }

    public String getLog() {
	return log;
    }

    public String getAmplLocation() {
	return amplLocation;
    }

    public void setAmplLocation(final String amplLocation) {
	this.amplLocation = amplLocation;
    }

    public String getFileName() {
	return fileName;
    }

    public void setFileName(final String fileName) {
	this.fileName = fileName;
    }

    public Map<String, Float> getCurrentValuesProb() {
	return currentValuesProb;
    }

    public void setCurrentValuesProb(final Map<String, Float> currentValuesProb) {
	this.currentValuesProb = currentValuesProb;
    }

    public List<String> getVariablesName() {
	return variablesName;
    }

    public void setVariablesName(final List<String> variablesName) {
	this.variablesName = variablesName;
    }
}
