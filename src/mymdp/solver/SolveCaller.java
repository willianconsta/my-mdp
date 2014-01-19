package mymdp.solver;

import static com.google.common.base.Preconditions.checkState;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mymdp.solver.ProbLinearSolver.SolutionType;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

public class SolveCaller {
	private final String amplLocation;
	private final Map<String, Double> currentValuesProb;
	private String fileContents;
	private Set<String> variablesName;
	private Double value;
	private String log;

	private final char[] lineArray = new char[32767];

	private final Process pros;
	private final BufferedReader process_out;
	private final PrintWriter process_in;

	public SolveCaller(final String amplFile) throws IOException {
		this.amplLocation = amplFile;
		currentValuesProb = new LinkedHashMap<>();
		variablesName = new HashSet<>();

		// Open files for reading and writing
		// BufferedReader fis_reader = new BufferedReader(_rReader);
		pros = new ProcessBuilder(amplLocation + "ampl").start();
		process_out = new BufferedReader(new InputStreamReader(pros.getInputStream()), 32767);
		process_in = new PrintWriter(new BufferedOutputStream(pros.getOutputStream(), 32767), true);
	}

	/**
	 * Saves the file containing the problem definition for solving.
	 * 
	 * @param objectiveFunctionMembers
	 * @param allProbabilityVariables
	 *            the name of all variables that represent a probability (their
	 *            values belong to [0,1])
	 * @param allRealVariables
	 *            the name of all variables defined within the real set.
	 * @param constraints
	 * @param solutionType
	 */
	public void saveAMPLFile(final List<String> objectiveFunctionMembers,
			final Set<String> allProbabilityVariables,
			final Set<String> allRealVariables,
			final Collection<String> constraints,
			final SolutionType solutionType) {
		String fileData = "";
		try {
			final StringWriter output = new StringWriter();

			checkState(Collections.disjoint(allRealVariables, allProbabilityVariables));

			setVariablesName(ImmutableSet.copyOf(Iterables.concat(allProbabilityVariables, allRealVariables)));

			for (final String s : allProbabilityVariables) {
				output.write("var " + s + ">=0, <=1;\n");
			}
			for (final String s : allRealVariables) {
				output.write("var " + s + ";\n");
			}

			if (solutionType != SolutionType.ANY_FEASIBLE) {
				fileData = objectiveFunctionMembers.get(0);
				for (int i = 1; i < objectiveFunctionMembers.size(); i++) {
					fileData += " + " + objectiveFunctionMembers.get(i);
				}
			}

			switch (solutionType) {
				case MAXIMIZE:
					output.write("maximize Profit: " + fileData + ";\n");
					break;
				case MINIMIZE:
					output.write("minimize Profit: " + fileData + ";\n");
					break;
				case ANY_FEASIBLE:
					output.write("minimize Profit: 1;\n");
					break;
				default:
					throw new IllegalStateException();
			}

			int i = 0;
			for (final String s : constraints) {
				output.write("subject to Constraint" + i++ + ": " + s + ";\n");
			}

			output.close();
			fileContents = output.getBuffer().toString();
		} catch (final IOException e) {
			throw Throwables.propagate(e);
		}

	}

	public void callSolver() {

		log = "";
		try {
			process_in.println("reset;");
			process_in.println(fileContents);
			process_in.println("option solver '" + amplLocation + "gurobi';");
			process_in.println("solve;");
			final String aux = Joiner.on(',').skipNulls().join(variablesName);

			process_in.println("display " + aux + ";");
			process_in.println("display 'end';");
			process_in.flush();

			while (true) {
				final int n = process_out.read(lineArray, 0, 32767);
				if (n == -1) {
					break;
				}
				log += new String(lineArray, 0, n);
				if (log.contains("end")) {
					break;
				}
			}

			for (final String line : log.split("\n")) {
				if (line != null) {
					if (line.contains("end")) {
						return;
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

				final int pos = line.indexOf("objective");
				if (pos >= 0) {
					final int profit = line.indexOf("Profit");
					if (profit >= 0) {
						try {
							value = Double.valueOf(line.substring(profit + 8, line.length() - 1));
							// pos+characters of objective +1
						} catch (final RuntimeException e) {
							throw e;
						}
					} else {
						try {
							value = Double.valueOf(line.substring(pos + 10));
							// pos+characters of objective +1
						} catch (final RuntimeException e) {
							throw e;
						}
					}
				}
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

	private void setVariablesName(final Set<String> variablesName) {
		this.variablesName = ImmutableSet.copyOf(variablesName);
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
