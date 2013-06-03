package mymdp.solver;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Throwables;

public class SolveCaller {
    private static int numberOfSolverCalls;
    private final String amplLocation;
    private final Map<String, Double> currentValuesProb;
    private String fileContents;
    private List<String> variablesName;
    private Double value;
    private String log;

    private final Process pros;
    private final BufferedReader process_out;
    private final PrintWriter process_in;

    public SolveCaller(final String amplFile) throws IOException {
	this.amplLocation = amplFile;
	currentValuesProb = new LinkedHashMap<>();
	variablesName = new ArrayList<>();

	// Open files for reading and writing
	// BufferedReader fis_reader = new BufferedReader(_rReader);
	pros = new ProcessBuilder(amplLocation + "ampl").start();
	process_out = new BufferedReader(new InputStreamReader(pros.getInputStream()), 32767);
	process_in = new PrintWriter(new BufferedOutputStream(pros.getOutputStream(), 32767), true);
    }

    public static void initializeCount() {
	numberOfSolverCalls = 0;
    }

    public static int getNumberOfSolverCalls() {
	return numberOfSolverCalls;
    }

    public void saveAMPLFile(final List<String> listaLegal, final List<String> listVariables, final List<String> listConstraint,
	    final boolean isMaximize) {
	String fileData = "";
	try {
	    final StringWriter output = new StringWriter();

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
	    fileContents = output.getBuffer().toString();
	} catch (final IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    public void callSolver() {

	log = "";
	try {
	    numberOfSolverCalls++;
	    process_in.println("reset;");
	    process_in.println(fileContents);
	    process_in.println("option solver '" + amplLocation + "gurobi';");
	    process_in.println("solve;");

	    String aux = variablesName.get(0);
	    for (int i = 1; i < variablesName.size(); i++) {
		aux += "," + variablesName.get(i);
	    }

	    process_in.println("display " + aux + ";");
	    process_in.println("display 'end';");
	    process_in.flush();

	    // Provide input to process (could come from any stream)
	    String line = null;

	    // Get output from process (can also be used by BufferedReader to
	    // get line-by-line... see how fis_reader is constructed).
	    boolean foundObj = false;
	    while (!foundObj) {
		line = process_out.readLine();
		if (line != null) {
		    if (line.contains("end")) {
			throw new IllegalStateException();
		    }
		    if (line.contains("Sorry")) {
			// throws away the error message
			while (process_out.readLine() != null) {
			    ;
			}
			throw new UnsupportedOperationException("Problem too big for student version of the solver.");
		    }
		}
		if (line == null) {
		    break;
		}
		log += line + "\n";

		final int pos = line.indexOf("objective");
		if (pos >= 0) {
		    final int profit = line.indexOf("Profit");
		    if (profit >= 0) {
			try {
			    value = Double.valueOf(line.substring(profit + 8, line.length() - 1));
			    // pos+characters of objective +1
			} catch (final RuntimeException e) {
			    while ((line = process_out.readLine()) != null) {
				log += line;
			    }
			    throw e;
			}
		    } else {
			try {
			    value = Double.valueOf(line.substring(pos + 10));
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

	    while (true) {
		line = process_out.readLine();
		if (line == null || line.contains("end")) {
		    break;
		}

		log += line + "\n";
		if (line.indexOf("=") > 0) {
		    final String key = line.substring(0, line.indexOf("=") - 1).trim();
		    final String value = line.substring(line.indexOf("=") + 1, line.length()).trim();
		    if (!key.equals("") && !value.equals("")) {
			try {
			    currentValuesProb.put(key, Double.valueOf(value));
			} catch (final NumberFormatException e) {
			    System.out.println(value);
			    throw e;
			}
		    }
		}
	    }
	} catch (final IOException ioe) {
	    throw Throwables.propagate(ioe);
	}
    }

    public String getLog() {
	return log;
    }

    public Map<String, Double> getCurrentValuesProb() {
	return currentValuesProb;
    }

    private void setVariablesName(final List<String> variablesName) {
	this.variablesName = variablesName;
    }

    public Double getCurrentValue() {
	return value;
    }

    public String getFileContents() {
	return fileContents;
    }

    @Override
    protected void finalize() throws Throwable {
	try {
	    process_in.close();
	} catch (final Exception e) {
	}
	try {
	    process_out.close();
	} catch (final Exception e) {
	}
	try {
	    pros.waitFor();
	} catch (final Exception e) {
	}
	try {
	    pros.destroy();
	} catch (final Exception e) {
	}
    }
}
