package HMM;

import java.awt.FileDialog;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationVector;
import be.ac.ulg.montefiore.run.jahmm.OpdfMultiGaussianFactory;
import be.ac.ulg.montefiore.run.jahmm.io.FileFormatException;
import be.ac.ulg.montefiore.run.jahmm.io.ObservationSequencesReader;
import be.ac.ulg.montefiore.run.jahmm.io.ObservationVectorReader;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;
import be.ac.ulg.montefiore.run.jahmm.learn.KMeansLearner;

import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

@Path("generic")
public class Operation {
	@SuppressWarnings("unused")
	@Context
	private UriInfo context;

	Hmm<ObservationVector> learntHmmPunch = null;
	Hmm<ObservationVector> learntHmmScrolldown = null;
	Hmm<ObservationVector> learntHmmSend = null;

	Hmm<ObservationVector> learn0 = null;
	Hmm<ObservationVector> learn1 = null;
	Hmm<ObservationVector> learn2 = null;
	Hmm<ObservationVector> learn3 = null;
	Hmm<ObservationVector> learn4 = null;
	Hmm<ObservationVector> learn5 = null;
	Hmm<ObservationVector> learn6 = null;
	Hmm<ObservationVector> learn7 = null;
	Hmm<ObservationVector> learn8 = null;
	Hmm<ObservationVector> learn9 = null;

	/**
	 * Default constructor.
	 */
	public Operation() {
		// TODO Auto-generated constructor stub
	}

	Boolean trigger = false;

	@GET
	@Produces("application/x-javascript")
	@Path("TrainFileOperation/{TrainRawFile:.+}/{TrainSeqFile:.+}")
	public String TrainFileOperation(@QueryParam("callback") String callback,
			@PathParam("TrainRawFile") String TrainRawFile,
			@PathParam("TrainSeqFile") String TrainSeqFile) {

		String line = "";
		double x1 = 0, y1 = 0, z1 = 0, d = 0.0f, norm;

		// Boolean trigger=false;
		List<String> filewriteList = new ArrayList<String>();
		List<String> tempList = new ArrayList<String>();

		String tempdir = "";

		tempdir = "/" + TrainSeqFile.split("-")[1] + "/"
				+ TrainSeqFile.split("-")[2];

		if (TrainRawFile.contains("-") && !TrainRawFile.contains("http")) {

			BufferedReader br = null;

			try {

				String sCurrentLine;

				TrainRawFile = TrainRawFile.replace("-", "/");

				TrainSeqFile = TrainSeqFile.replace("-", "/");

				br = new BufferedReader(new FileReader(TrainRawFile));
				double x = 0, y = 0, z = 0;

				while ((sCurrentLine = br.readLine()) != null) {

					x = Double.parseDouble(sCurrentLine.split("\t")[0]);
					y = Double.parseDouble(sCurrentLine.split("\t")[1]);
					z = Double.parseDouble(sCurrentLine.split("\t")[2]);

					/*
					 * norm= x*x + y*y +z*z;
					 * 
					 * line=line+"\n"+"norm:"+norm;
					 * 
					 * if(norm>1 && !trigger){ trigger=true; }else if(norm<0.3
					 * && trigger){ trigger=false; }
					 */

					d = Math.sqrt(Math.pow((x - x1), 2) + Math.pow((y - y1), 2)
							+ Math.pow((z - z1), 2));

					// line=line+"\n"+"d:"+d + " trigger:"+trigger;
					if (d >= 0.3 && !trigger) {
						// filewriteList.add("start");
						// line=line+"\n"+"start";
						trigger = true;
					} else if (d <= 0.1 && trigger) {
						if (tempList.size() > 6) {
							filewriteList.add("start");
							for (int t = 0; t < tempList.size(); t++) {
								filewriteList.add(tempList.get(t));
							}
							filewriteList.add("end");
							tempList.clear();
						} else {
							tempList.clear();
						}
						// line=line+"\n"+"end";
						trigger = false;
					}

					if (trigger) {
						// filewriteList.add(x+","+y+","+z);
						tempList.add(x + "," + y + "," + z);
					}

					x1 = x;
					y1 = y;
					z1 = z;

				}

			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null)
						br.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}

			for (int i = 0; i < filewriteList.size(); i++) {
				line = line + "\n" + filewriteList.get(i);
			}

			File file = new File(tempdir + "/tempFile.txt");

			// if file doesnt exists, then create it
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			FileWriter fw;
			try {
				fw = new FileWriter(file.getAbsoluteFile());

				BufferedWriter bw = new BufferedWriter(fw);
				for (int i = 0; i < filewriteList.size(); i++) {
					if (!filewriteList.get(i).equals(""))
						bw.write(filewriteList.get(i) + "\n");
				}

				bw.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// ////////// Convert "Start-End" to Sequence File
			// ////////////////////
			Boolean gesture = false;
			;
			StringBuffer buffer = new StringBuffer();
			for (int i = 1; i <= 1; i++) {

				BufferedReader reader;
				try {
					reader = new BufferedReader(new FileReader(file));

					while (reader.ready()) {
						String s = reader.readLine().trim();

						if (s.equals("start"))
							gesture = true;
						if (s.equals("end")) {
							gesture = false;
							buffer.append(System.getProperty("line.separator"));
							writeGestureToSeq(buffer, TrainSeqFile);

							buffer.delete(0, buffer.length());
						}
						if (!s.isEmpty() && gesture && !s.equals("start")
								&& !s.equals("end")) {
							s = s.replace(",", " ");
							buffer.append("[ " + s + " ] ; ");
						}
					}
					buffer.append(System.getProperty("line.separator"));
					reader.close();

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		} else if (TrainRawFile.contains("http")
				&& TrainRawFile.contains("slash")) {

			// ! try {

			String sCurrentLine;

			TrainRawFile = TrainRawFile.replace("slash", "/");
			line = line + "\n" + TrainRawFile;

			TrainSeqFile = TrainSeqFile.replace("-", "/");

			URL url;
			try {
				url = new URL(TrainRawFile);

				Scanner s = new Scanner(url.openStream());

				while (s.hasNextLine()) {

					sCurrentLine = s.next();
					line = line + "\n" + sCurrentLine;

				}

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				line = line + "\n" + e.toString();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				line = line + "\n" + e.toString();
			}

			/*
			 * ! double x=0,y=0,z=0;
			 * 
			 * while (s.hasNextLine()) {
			 * 
			 * sCurrentLine = s.next(); line = line + "\n" + sCurrentLine;
			 * 
			 * x = Double.parseDouble(sCurrentLine.split("\t")[0]); y =
			 * Double.parseDouble(sCurrentLine.split("\t")[1]); z =
			 * Double.parseDouble(sCurrentLine.split("\t")[2]);
			 * 
			 * 
			 * 
			 * d= Math.sqrt( Math.pow((x-x1),2 ) + Math.pow((y-y1),2 ) +
			 * Math.pow((z-z1),2 ));
			 * 
			 * 
			 * // line=line+"\n"+"d:"+d + " trigger:"+trigger; if(d>=0.3 &&
			 * !trigger){ // filewriteList.add("start"); //
			 * line=line+"\n"+"start"; trigger=true; }else if(d<=0.1 &&
			 * trigger){ if(tempList.size()>6){ filewriteList.add("start");
			 * for(int t=0;t<tempList.size();t++) {
			 * filewriteList.add(tempList.get(t)); } filewriteList.add("end");
			 * tempList.clear(); } else { tempList.clear(); } //
			 * line=line+"\n"+"end"; trigger=false; }
			 * 
			 * if(trigger){ // filewriteList.add(x+","+y+","+z);
			 * tempList.add(x+","+y+","+z); }
			 * 
			 * x1=x;y1=y;z1=z;
			 * 
			 * 
			 * 
			 * }
			 * 
			 * } catch (IOException e) { e.printStackTrace(); line = line+"\n" +
			 * e.toString(); }
			 * 
			 * for(int i=0;i<filewriteList.size();i++) { line =
			 * line+"\n"+filewriteList.get(i); }
			 * 
			 * 
			 * File file = new File(tempdir+"/tempFile.txt");
			 * 
			 * // if file doesnt exists, then create it if (!file.exists()) {
			 * try { file.createNewFile(); } catch (IOException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); line = line+"\n"
			 * + e.toString(); } }
			 * 
			 * FileWriter fw; try { fw = new FileWriter(file.getAbsoluteFile());
			 * 
			 * BufferedWriter bw = new BufferedWriter(fw); for(int i
			 * =0;i<filewriteList.size();i++){
			 * if(!filewriteList.get(i).equals(""))
			 * bw.write(filewriteList.get(i)+"\n"); }
			 * 
			 * bw.close(); } catch (IOException e1) { // TODO Auto-generated
			 * catch block e1.printStackTrace(); line = line+"\n" +
			 * e1.toString(); }
			 * 
			 * 
			 * 
			 * //////////// Convert "Start-End" to Sequence File
			 * //////////////////// Boolean gesture= false;; StringBuffer buffer
			 * = new StringBuffer(); for (int i = 1; i <=1; i++) {
			 * 
			 * BufferedReader reader; try { reader = new BufferedReader(new
			 * FileReader(file));
			 * 
			 * while (reader.ready()) { String s = reader.readLine().trim();
			 * 
			 * 
			 * if(s.equals("start")) gesture=true; if(s.equals("end")){
			 * gesture=false;
			 * buffer.append(System.getProperty("line.separator"));
			 * writeGestureToSeq(buffer,TrainSeqFile);
			 * 
			 * buffer.delete(0, buffer.length()); } if (!s.isEmpty() && gesture
			 * && !s.equals("start") && !s.equals("end")) {
			 * s=s.replace(","," "); buffer.append("[ " + s + " ] ; "); } }
			 * buffer.append(System.getProperty("line.separator"));
			 * reader.close();
			 * 
			 * } catch (FileNotFoundException e) { // TODO Auto-generated catch
			 * block e.printStackTrace(); }catch (IOException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); } }!
			 */

		}

		/*
		 * FileWriter writer; try { writer = new FileWriter(TrainSeqFile);
		 * 
		 * writer.write(buffer.toString()); writer.close();
		 * 
		 * } catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		return line;

	}

	@GET
	@Produces("application/x-javascript")
	@Path("TestFileOperation/{TestRawFile:.+}/{TestSeqFile:.+}")
	public String TestFileOperation(@QueryParam("callback") String callback,
			@PathParam("TestRawFile") String TestRawFile,
			@PathParam("TestSeqFile") String TestSeqFile) {
		String line = "";
		double x1 = 0, y1 = 0, z1 = 0, d = 0.0f, norm;

		// Boolean trigger=false;
		List<String> filewriteList = new ArrayList<String>();
		List<String> tempList = new ArrayList<String>();

		String tempdir = "/" + TestRawFile.split("-")[1] + "/"
				+ TestRawFile.split("-")[2];

		BufferedReader br = null;

		try {

			String sCurrentLine;

			TestRawFile = TestRawFile.replace("-", "/");
			TestSeqFile = TestSeqFile.replace("-", "/");

			br = new BufferedReader(new FileReader(TestRawFile));
			double x = 0, y = 0, z = 0;

			while ((sCurrentLine = br.readLine()) != null) {

				x = Double.parseDouble(sCurrentLine.split("\t")[0]);
				y = Double.parseDouble(sCurrentLine.split("\t")[1]);
				z = Double.parseDouble(sCurrentLine.split("\t")[2]);

				/*
				 * norm= x*x + y*y +z*z;
				 * 
				 * line=line+"\n"+"norm:"+norm;
				 * 
				 * if(norm>1 && !trigger){ trigger=true; }else if(norm<0.3 &&
				 * trigger){ trigger=false; }
				 */

				d = Math.sqrt(Math.pow((x - x1), 2) + Math.pow((y - y1), 2)
						+ Math.pow((z - z1), 2));

				// line=line+"\n"+"d:"+d + " trigger:"+trigger;
				if (d >= 0.3 && !trigger) {
					// filewriteList.add("start");
					// line=line+"\n"+"start";
					trigger = true;
				} else if (d <= 0.1 && trigger) {
					if (tempList.size() > 6) {
						filewriteList.add("start");
						for (int t = 0; t < tempList.size(); t++) {
							filewriteList.add(tempList.get(t));
						}
						filewriteList.add("end");
						tempList.clear();
					} else {
						tempList.clear();
					}
					// line=line+"\n"+"end";
					trigger = false;
				}

				if (trigger) {
					// filewriteList.add(x+","+y+","+z);
					tempList.add(x + "," + y + "," + z);
				}

				x1 = x;
				y1 = y;
				z1 = z;

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		for (int i = 0; i < filewriteList.size(); i++) {
			line = line + "\n" + filewriteList.get(i);
		}

		File file = new File(tempdir + "/tempFile2.txt");

		// if file doesnt exists, then create it
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		FileWriter fw;
		try {
			fw = new FileWriter(file.getAbsoluteFile());

			BufferedWriter bw = new BufferedWriter(fw);
			for (int i = 0; i < filewriteList.size(); i++) {
				if (!filewriteList.get(i).equals(""))
					bw.write(filewriteList.get(i) + "\n");
			}

			bw.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// ////////// Convert "Start-End" to Sequence File ////////////////////
		Boolean gesture = false;
		;
		StringBuffer buffer = new StringBuffer();
		for (int i = 1; i <= 1; i++) {

			BufferedReader reader;
			try {
				reader = new BufferedReader(new FileReader(file));

				while (reader.ready()) {
					String s = reader.readLine().trim();

					if (s.equals("start"))
						gesture = true;
					if (s.equals("end")) {
						gesture = false;
						buffer.append(System.getProperty("line.separator"));
						writeGestureToSeq(buffer, TestSeqFile);

						buffer.delete(0, buffer.length());
					}
					if (!s.isEmpty() && gesture && !s.equals("start")
							&& !s.equals("end")) {
						s = s.replace(",", " ");
						buffer.append("[ " + s + " ] ; ");
					}
				}
				buffer.append(System.getProperty("line.separator"));
				reader.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return line;

	}

	/**
	 * Retrieves representation of an instance of Operation
	 * 
	 * @return an instance of String
	 */
	@GET
	@Produces("application/x-javascript")
	@Path("HMMTrainingTestThree/{Trainfilename1:.+}/{Trainfilename2:.+}/{Trainfilename3:.+}/{TestFile:.+}")
	public String HMMTrainingTestThree(@QueryParam("callback") String callback,
			@PathParam("Trainfilename1") String Trainfilename1,
			@PathParam("Trainfilename2") String Trainfilename2,
			@PathParam("Trainfilename3") String Trainfilename3,
			@PathParam("TestFile") String TestFile) {
		String line = "";

		String Trainfilename_1 = Trainfilename1.replace("-", "/");
		String Trainfilename_2 = Trainfilename2.replace("-", "/");
		String Trainfilename_3 = Trainfilename3.replace("-", "/");
		String Test_File = TestFile.replace("-", "/");

		// Create HMM for punch gesture
		Boolean exception = false;
		int x = 10;
		while (!exception) {

			OpdfMultiGaussianFactory initFactoryPunch = new OpdfMultiGaussianFactory(
					3);

			Reader learnReaderPunch;
			try {
				learnReaderPunch = new FileReader(Trainfilename_1);

				List<List<ObservationVector>> learnSequencesPunch = ObservationSequencesReader
						.readSequences(new ObservationVectorReader(),
								learnReaderPunch);
				learnReaderPunch.close();

				KMeansLearner<ObservationVector> kMeansLearnerPunch = new KMeansLearner<ObservationVector>(
						x, initFactoryPunch, learnSequencesPunch);

				// Create an estimation of the HMM (initHmm) using one iteration
				// of the
				// k-Means algorithm
				Hmm<ObservationVector> initHmmPunch = kMeansLearnerPunch
						.iterate();

				// Use BaumWelchLearner to create the HMM (learntHmm) from
				// initHmm
				BaumWelchLearner baumWelchLearnerPunch = new BaumWelchLearner();
				learntHmmPunch = baumWelchLearnerPunch.learn(initHmmPunch,
						learnSequencesPunch);
				exception = true;
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				line = e.toString();
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				line = e.toString();
				e.printStackTrace();
			} catch (FileFormatException e) {
				// TODO Auto-generated catch block
				line = e.toString();
				e.printStackTrace();
			} catch (Exception e) {
				x--;

			}
		}

		// Create HMM for scroll-down gesture
		Boolean exception1 = false;
		int x1 = 10;
		while (!exception1) {

			try {
				OpdfMultiGaussianFactory initFactoryScrolldown = new OpdfMultiGaussianFactory(
						3);

				Reader learnReaderScrolldown;

				learnReaderScrolldown = new FileReader(Trainfilename_2);

				List<List<ObservationVector>> learnSequencesScrolldown = ObservationSequencesReader
						.readSequences(new ObservationVectorReader(),
								learnReaderScrolldown);
				learnReaderScrolldown.close();

				KMeansLearner<ObservationVector> kMeansLearnerScrolldown = new KMeansLearner<ObservationVector>(
						x1, initFactoryScrolldown, learnSequencesScrolldown);
				// Create an estimation of the HMM (initHmm) using one iteration
				// of the
				// k-Means algorithm
				Hmm<ObservationVector> initHmmScrolldown = kMeansLearnerScrolldown
						.iterate();

				// Use BaumWelchLearner to create the HMM (learntHmm) from
				// initHmm
				BaumWelchLearner baumWelchLearnerScrolldown = new BaumWelchLearner();
				learntHmmScrolldown = baumWelchLearnerScrolldown.learn(
						initHmmScrolldown, learnSequencesScrolldown);
				exception1 = true;

			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				line = e1.toString();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				line = e.toString();
			} catch (FileFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				line = e.toString();
			} catch (Exception e) {
				x1--;

			}
		}

		// Create HMM for send gesture
		Boolean exception2 = false;
		int x2 = 10;
		while (!exception2) {
			try {
				OpdfMultiGaussianFactory initFactorySend = new OpdfMultiGaussianFactory(
						3);

				Reader learnReaderSend = new FileReader(Trainfilename_3);

				List<List<ObservationVector>> learnSequencesSend;

				learnSequencesSend = ObservationSequencesReader.readSequences(
						new ObservationVectorReader(), learnReaderSend);

				learnReaderSend.close();

				KMeansLearner<ObservationVector> kMeansLearnerSend = new KMeansLearner<ObservationVector>(
						x2, initFactorySend, learnSequencesSend);
				// Create an estimation of the HMM (initHmm) using one iteration
				// of the
				// k-Means algorithm
				Hmm<ObservationVector> initHmmSend = kMeansLearnerSend
						.iterate();

				// Use BaumWelchLearner to create the HMM (learntHmm) from
				// initHmm
				BaumWelchLearner baumWelchLearnerSend = new BaumWelchLearner();
				learntHmmSend = baumWelchLearnerSend.learn(initHmmSend,
						learnSequencesSend);
				exception2 = true;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				line = e1.toString();
			} catch (FileFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				line = e1.toString();
			} catch (Exception e) {
				x2--;
				// System.out.println(x2);

			}
		}

		line = "training success for:" + Trainfilename_1 + ","
				+ Trainfilename_2 + "," + Trainfilename_3 + "\n";
		line = line + "Start Test....\n";

		try {
			Reader testReader = new FileReader(Test_File);
			List<List<ObservationVector>> testSequences = ObservationSequencesReader
					.readSequences(new ObservationVectorReader(), testReader);
			testReader.close();

			short gesture; // punch = 1, scrolldown = 2, send = 3
			double punchProbability, scrolldownProbability, sendProbability;
			for (int i = 0; i < testSequences.size(); i++) {

				punchProbability = learntHmmPunch.probability(testSequences
						.get(i));
				gesture = 1;
				scrolldownProbability = learntHmmScrolldown
						.probability(testSequences.get(i));
				if (scrolldownProbability > punchProbability) {
					gesture = 2;
				}
				sendProbability = learntHmmSend.probability(testSequences
						.get(i));
				line = line + punchProbability + "," + scrolldownProbability
						+ "," + sendProbability + "\n";
				if ((gesture == 1 && sendProbability > punchProbability)
						|| (gesture == 2 && sendProbability > scrolldownProbability)) {
					gesture = 3;
				}
				if (gesture == 1) {
					line = line + "This is a punch gesture\n";
				} else if (gesture == 2) {
					line = line + "This is a right-left gesture\n";
				} else if (gesture == 3) {
					line = line + "This is a left to right gesture\n";
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			line = e.toString();
			e.printStackTrace();
		} catch (FileFormatException e) {
			// TODO Auto-generated catch block
			line = e.toString();
			e.printStackTrace();
		}

		return line;
	}

	/**
	 * Retrieves representation of an instance of Operation
	 * 
	 * @return an instance of String
	 */
	@GET
	@Produces("application/x-javascript")
	@Path("HMMTrainingTest/{Trainfilename:.+}/{Actionname:.+}/{TestFile:.+}")
	public String HMMTraining(@QueryParam("callback") String callback,
			@PathParam("Trainfilename") String Trainfilename,
			@PathParam("Actionname") String Actionname,
			@PathParam("TestFile") String TestFile) {
		String line = "";

		Trainfilename = Trainfilename.replace("-", "/");
		TestFile = TestFile.replace("-", "/");

		String TrainfileArr[] = Trainfilename.split(":");
		String ActionnameArr[] = Actionname.split(":");

		Map<Hmm<ObservationVector>, String> HMMMap = new HashMap<Hmm<ObservationVector>, String>();

		Boolean exception = false;

		for (int i = 0; i < TrainfileArr.length; i++) {
			exception = false;

			int x = 10;

			while (!exception) {

				OpdfMultiGaussianFactory initFactory = new OpdfMultiGaussianFactory(
						3);

				Reader learnReader;
				try {
					learnReader = new FileReader(TrainfileArr[i]);

					List<List<ObservationVector>> learnSequences = ObservationSequencesReader
							.readSequences(new ObservationVectorReader(),
									learnReader);
					learnReader.close();

					KMeansLearner<ObservationVector> kMeansLearner = new KMeansLearner<ObservationVector>(
							x, initFactory, learnSequences);

					// Create an estimation of the HMM (initHmm) using one
					// iteration of the
					// k-Means algorithm
					Hmm<ObservationVector> initHmm = kMeansLearner.iterate();

					// Use BaumWelchLearner to create the HMM (learntHmm) from
					// initHmm
					switch (i) {
					case 0:
						BaumWelchLearner baumWelchLearner0 = new BaumWelchLearner();
						learn0 = baumWelchLearner0.learn(initHmm,
								learnSequences);
						exception = true;
						HMMMap.put(learn0, ActionnameArr[0]);
						break;
					case 1:
						BaumWelchLearner baumWelchLearner1 = new BaumWelchLearner();
						learn1 = baumWelchLearner1.learn(initHmm,
								learnSequences);
						exception = true;
						HMMMap.put(learn1, ActionnameArr[1]);
						break;

					case 2:
						BaumWelchLearner baumWelchLearner2 = new BaumWelchLearner();
						learn2 = baumWelchLearner2.learn(initHmm,
								learnSequences);
						exception = true;
						HMMMap.put(learn2, ActionnameArr[2]);
						break;

					case 3:
						BaumWelchLearner baumWelchLearner3 = new BaumWelchLearner();
						learn3 = baumWelchLearner3.learn(initHmm,
								learnSequences);
						exception = true;
						HMMMap.put(learn3, ActionnameArr[3]);
						break;

					case 4:
						BaumWelchLearner baumWelchLearner4 = new BaumWelchLearner();
						learn4 = baumWelchLearner4.learn(initHmm,
								learnSequences);
						exception = true;
						HMMMap.put(learn4, ActionnameArr[4]);
						break;
					case 5:
						BaumWelchLearner baumWelchLearner5 = new BaumWelchLearner();
						learn5 = baumWelchLearner5.learn(initHmm,
								learnSequences);
						exception = true;
						HMMMap.put(learn5, ActionnameArr[5]);
						break;
					case 6:
						BaumWelchLearner baumWelchLearner6 = new BaumWelchLearner();
						learn6 = baumWelchLearner6.learn(initHmm,
								learnSequences);
						exception = true;
						HMMMap.put(learn6, ActionnameArr[6]);
						break;
					case 7:
						BaumWelchLearner baumWelchLearner7 = new BaumWelchLearner();
						learn7 = baumWelchLearner7.learn(initHmm,
								learnSequences);
						exception = true;
						HMMMap.put(learn7, ActionnameArr[7]);
						break;
					case 8:
						BaumWelchLearner baumWelchLearner8 = new BaumWelchLearner();
						learn8 = baumWelchLearner8.learn(initHmm,
								learnSequences);
						exception = true;
						HMMMap.put(learn8, ActionnameArr[8]);
						break;
					case 9:
						BaumWelchLearner baumWelchLearner9 = new BaumWelchLearner();
						learn9 = baumWelchLearner9.learn(initHmm,
								learnSequences);
						exception = true;
						HMMMap.put(learn9, ActionnameArr[9]);
						break;
					}

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					line = e.toString();
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					line = e.toString();
					e.printStackTrace();
				} catch (FileFormatException e) {
					// TODO Auto-generated catch block
					line = e.toString();
					e.printStackTrace();
				} catch (Exception e) {
					x--;

				}
			}
		}

		line = "training success for:" + Actionname + "\n";
		line = line + "Start Test....\n";

		line = line + "TrainfileArr size: " + TrainfileArr.length + "\n";
		line = line + "HMMMap size: " + HMMMap.size();

		line = line + "\n" + HMMMap.get(learn0);
		line = line + "\n" + HMMMap.get(learn1);
		line = line + "\n" + HMMMap.get(learn2);

		// /////////////////////// Start Testing ///////////////////////////

		try {
			Reader testReader = new FileReader(TestFile);
			List<List<ObservationVector>> testSequences = ObservationSequencesReader
					.readSequences(new ObservationVectorReader(), testReader);
			testReader.close();

			short gesture;
			double Probability = 0;
			Map<Double, String> motionmap = new HashMap<Double, String>();

			for (int i = 0; i < testSequences.size(); i++) {

				Iterator<Hmm<ObservationVector>> HMMIte2 = HMMMap.keySet()
						.iterator();
				while (HMMIte2.hasNext()) {

					Hmm<ObservationVector> learnModel = HMMIte2.next();
					String motion = HMMMap.get(learnModel);
					Probability = learnModel.probability(testSequences.get(i));
					motionmap.put(Probability, motion);
				}

				double comp = 0;

				Iterator<Double> motionIte2 = motionmap.keySet().iterator();
				while (motionIte2.hasNext()) {
					Double prob = motionIte2.next();
					if (prob > comp) {
						comp = prob;
					}

				}

				String finaldecision = motionmap.get(comp);

				line = line + "\n" + "highest probability is: " + comp
						+ ", gesture is: " + finaldecision;

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			line = e.toString();
			e.printStackTrace();
		} catch (FileFormatException e) {
			// TODO Auto-generated catch block
			line = e.toString();
			e.printStackTrace();
		}

		/*
		 * if (gesture == 1) { line=line+"This is a punch gesture\n"; } else if
		 * (gesture == 2) { line=line+"This is a right-left gesture\n"; } else
		 * if (gesture == 3) { line=line+"This is a left to right gesture\n"; }
		 */

		return line;
	}

	private static void writeGestureToSeq(StringBuffer buffer, String seqFile)
			throws IOException {
		FileWriter writer = new FileWriter(seqFile, true);
		writer.write(buffer.toString());
		writer.close();

	}

	/**
	 * PUT method for updating or creating an instance of Operation
	 * 
	 * @param content
	 *            representation for the resource
	 * @return an HTTP response with content of the updated or created resource.
	 */
	@PUT
	@Consumes("application/json")
	public void putJson(String content) {
	}

}
