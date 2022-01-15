package permutethers;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

public class Permuter {
	String linearTetherFH = "CTCCACTCCC";
	String linearStapleFH = "GGGAGTGGAG";

	String starTetherFwd = linearTetherFH;
	String starTetherBkwd = "CCCTCACCTC";
	String starStapleFwd = linearStapleFH;
	String starStapleBkwd = "GAGGTGAGGG";

	HashMap<Character, Character> complements;
	FileOutputStream fout;
	String outfile;
	String[][] seqs;
	ArrayList<Integer> tetherNums;
	ArrayList<String> uniqueSeqs;
	ArrayList<String> uniqueStaplesNoDash;
	HashMap<Character, String> pos;
	int p;
	HashMap<Character, String> pol;
	boolean star;
	double conc;
	String orderOfMag;
	String fileLoc;
	
	
	/*
	public static void main(String[] args) throws IOException {
		char[] pos = {'1','2'};
		char[][] comp = { {'3', '3'}, {'4', '4'} };
		Permuter p = new Permuter(pos, comp, "test");
	}
	 */

	

	public Permuter(int p, HashMap<Character, String> pos, HashMap<Character, String> pol, String outfile, String conc, boolean star) throws IOException {
		/*
		 * OGseq = string of possible tethers to determine possible combinations
		 * and unique staples
		 * e.g. AB --> A1A2 A1B2 B1B2 B1A2
		 */
		
		//fileLoc = "C:\\Users\\Knight Group\\Desktop\\" + outfile;
		fileLoc = "C:\\Users\\730236688\\Desktop\\" + outfile;
		
		this.p =p;
		
		this.conc = Double.parseDouble(conc);
		
		for (int i  = 0; i < conc.length(); i++) {
			if (conc.charAt(i)=='e' || conc.charAt(i) == 'E') {
				this.orderOfMag = conc.substring(i);
			}
		}
		
		
		this.pol = pol;
		this.pos = pos;
		this.star = star;
		this.outfile = outfile;
		complements = new HashMap<Character, Character>();
		complements.put('A','T');
		complements.put('T','A');
		complements.put('C','G');
		complements.put('G','C');

		int numPermutations = (int) Math.pow(pol.size(), pos.size());	

		String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String OGseq = "";
		for (int i = 0; i < pol.size(); i++) {
			OGseq += abc.charAt(i);
		}

		seqs = new String[numPermutations][pos.size()];
		seqs = permuteRecur(0, numPermutations/pol.size(), OGseq);


		fout = new FileOutputStream(fileLoc + ".xlsx");

		uniqueSeqs = new ArrayList<String>();
		for (String[] sarr: seqs) {
			String s = "";
			for (String stri: sarr) {
				s += stri;
			}
			uniqueSeqs.add(s);
		}	
		

		String[][] staples;
		String[][] staplesNoDash;
		if (star) {
			staples = this.starStaple();
			staplesNoDash = this.starStapleNoDash();
		} else {
			staples = new String[numPermutations][pos.size() - 1];
			staplesNoDash = new String[numPermutations][pos.size() - 1];
			for (int s = 0; s < uniqueSeqs.size(); s++) {
				for (int i = 0; i < (uniqueSeqs.get(s).length()/2) - 1; i++) {
					staples[s][i] = uniqueSeqs.get(s).charAt(i) + "-" + uniqueSeqs.get(s).charAt(i+1);
					staplesNoDash[s][i] = uniqueSeqs.get(s).charAt(i) + "" + uniqueSeqs.get(s).charAt(i+1);
				}
			}
		}


		uniqueStaplesNoDash = new ArrayList<String>();
		ArrayList<String> uniqueStaples = new ArrayList<String>();
		for (int two = 0; two < staples[0].length; two++) {
			for (int one = 0; one < staples.length; one++) {
				if (!uniqueStaples.contains(staples[one][two])) {
					uniqueStaples.add(staples[one][two]);
				}
				if (!uniqueStaplesNoDash.contains(staplesNoDash[one][two])) {
					uniqueStaplesNoDash.add(staplesNoDash[one][two]);
				}
			}
		}

		ArrayList<String> uniqueTethers = new ArrayList<String>();
		for (int i = 0; i < pol.size(); i++) {
			for (Integer j = 1; j <= pos.size(); j++) {
				uniqueTethers.add("" + OGseq.charAt(i) + j);
			}
		}
		
		tetherNums = new ArrayList<Integer>();

		for (String s: uniqueStaplesNoDash) {
			for (int i = 0; i < s.length()-2; i+=2) {
				int idx = uniqueTethers.indexOf("" + s.charAt(i) + s.charAt(i+1));
				tetherNums.set(idx, tetherNums.get(idx) + 1);
			}
		}

		HashMap<String, Integer> map = new HashMap<String, Integer>();
		int k = 1;
		for (String s: uniqueStaples) {
			map.put(s, k++);
		}
		for (String s: uniqueTethers) {
			map.put(s, k++);
		}
		this.DNAwriter(uniqueTethers, uniqueStaples, pos.size(), map);
	}
	
	//SYMMETRICAL PERMUTER

	public Permuter(int p, boolean even, HashMap<Character, String> pos, HashMap<Character, String> posNums, HashMap<Character, String> pol, String outfile, String conc, boolean star) throws IOException {
		/*
		 * OGseq = string of possible tethers to determine possible combinations
		 * and unique staples
		 * e.g. AB --> A1A2 A1B2 B1B2 B1A2
		 */
		
		//fileLoc = "C:\\Users\\Knight Group\\Desktop\\" + outfile;
		
		this.p = p;
		fileLoc = "C:\\Users\\730236688\\Desktop\\" + outfile;
		
		this.conc = Double.parseDouble(conc);
		
		for (int i  = 0; i < conc.length(); i++) {
			if (conc.charAt(i)=='e' || conc.charAt(i) == 'E') {
				this.orderOfMag = conc.substring(i);
			}
		}
		
		this.pol = pol;
		this.pos = pos;
		this.star = star;
		complements = new HashMap<Character, Character>();
		complements.put('A','T');
		complements.put('T','A');
		complements.put('C','G');
		complements.put('G','C');

		int max;
		if (even) {
			max = pos.size()*2;
		} else {
			max = (pos.size()*2) - 1;
		}

		int numPermutations = (int) Math.pow(pol.size(), max);	

		String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String OGseq = "";
		for (int i = 0; i < pol.size(); i++) {
			OGseq += abc.charAt(i);
		}

		seqs = new String[numPermutations][max];
		seqs = permuteRecur(0, posNums, numPermutations/pol.size(), OGseq);

		fout = new FileOutputStream(fileLoc + ".xlsx");


		uniqueSeqs = new ArrayList<String>();
		for (String[] sarr: seqs) {
			String s = "";
			for (String stri: sarr) {
				s += stri;
			}
			uniqueSeqs.add(s);
		}

		ArrayList<String> temp = new ArrayList<String>();
		ArrayList<String> handled = new ArrayList<String>();
		for (String s: uniqueSeqs) {
			temp.add(s);
		}

		for (String s: temp) {
			String rev = flipItAndReverseIt(s);
			if (temp.contains(rev) && !rev.equals(s) 
					&& !(handled.contains(s) || handled.contains(rev))) {
				handled.add(s);
				uniqueSeqs.remove(rev);		
			}
		}
		
		


		String[][] staples;
		String[][] staplesNoDash;
		if (star) {
			staples = this.starStaple();
			staplesNoDash = this.starStapleNoDash();
		} else {
			staples = new String[uniqueSeqs.size()][max - 1];
			staplesNoDash = new String[uniqueSeqs.size()][max-1];
			for (int s = 0; s < uniqueSeqs.size(); s++) {
				int j = 0;
				for (int i = j; i < (uniqueSeqs.get(s).length()/2)-1; i++) {
					staples[s][i] = "" + uniqueSeqs.get(s).charAt(j) + uniqueSeqs.get(s).charAt(j+1) 
							+ "-" + uniqueSeqs.get(s).charAt(j+2) + uniqueSeqs.get(s).charAt(j+3);
					staplesNoDash[s][i] = "" + uniqueSeqs.get(s).charAt(j) + uniqueSeqs.get(s).charAt(j+1) 
							+ uniqueSeqs.get(s).charAt(j+2) + uniqueSeqs.get(s).charAt(j+3);
					j+=2;
				}
			}
		}

		uniqueStaplesNoDash = new ArrayList<String>();
		ArrayList<String> uniqueStaples = new ArrayList<String>();
		for (int two = 0; two < staples[0].length; two++) {
			for (int one = 0; one < staples.length; one++) {
				if (!uniqueStaples.contains(staples[one][two])) {
					uniqueStaples.add(staples[one][two]);
				}
				if (!uniqueStaplesNoDash.contains(staplesNoDash[one][two])) {
					uniqueStaplesNoDash.add(staplesNoDash[one][two]);
				}
			}
		}
		
		for (String s: uniqueStaplesNoDash) {
			temp.add(s);
		}

		for (String s: temp) {
			String rev = flipItAndReverseIt(s);
			if (temp.contains(rev) && !rev.equals(s) 
					&& !(handled.contains(s) || handled.contains(rev))) {
				handled.add(s);
				uniqueStaplesNoDash.remove(rev);		
			}
		}
		
		
		
		

		ArrayList<String> uniqueTethers = new ArrayList<String>();
		for (int i = 0; i < pol.size(); i++) {
			for (Integer j = 1; j <= pos.size(); j++) {
				uniqueTethers.add("" + OGseq.charAt(i) + posNums.get(j.toString().charAt(0)));
			}
		}
		
		tetherNums = new ArrayList<Integer>();
		for (int i = 0; i < uniqueTethers.size(); i++) {
			tetherNums.add(0);
		}
		
		for (String s: uniqueStaplesNoDash) {
			for (int i = 0; i < s.length()-1; i+=2) {
				int idx = uniqueTethers.indexOf("" + s.charAt(i) + s.charAt(i+1));
				tetherNums.set(idx, tetherNums.get(idx) + 1);
			}
		}
		
		

		HashMap<String, Integer> map = new HashMap<String, Integer>();
		int k = 1;
		for (String s: uniqueStaplesNoDash) {
			map.put(s, k++);
		}
		for (String s: uniqueTethers) {
			map.put(s, k++);
		}

		this.DNAwriter(uniqueTethers, uniqueStaples, max, map);

	}

	public String[][] starStaple() {
		///////////// CHANGE THE POSSIBLE NUMBER OF STAPLES?
		String[][] staples = new String[uniqueSeqs.size()][1];
		for (int s = 0; s < uniqueSeqs.size(); s++) {
			staples[s][0] = "" + uniqueSeqs.get(s).charAt(0) + uniqueSeqs.get(s).charAt(1);
			for (int c = 2; c+1 < uniqueSeqs.get(s).length(); c+=2) {
				staples[s][0] += "-" + uniqueSeqs.get(s).charAt(c) + uniqueSeqs.get(s).charAt(c+1);
			}

		}
		return staples;
	}

	public String[][] starStapleNoDash() {
		///////////// CHANGE THE POSSIBLE NUMBER OF STAPLES?
		String[][] staples = new String[uniqueSeqs.size()][1];
		for (int s = 0; s < uniqueSeqs.size(); s++) {
			staples[s][0] = "" + uniqueSeqs.get(s).charAt(0) + uniqueSeqs.get(s).charAt(1);
			for (int c = 2; c+1 < uniqueSeqs.get(s).length(); c+=2) {
				staples[s][0] += "" + uniqueSeqs.get(s).charAt(c) + uniqueSeqs.get(s).charAt(c+1);
			}

		}
		return staples;
	}

	public void DNAwriter(ArrayList<String> uniqueTethers, ArrayList<String> uniqueStaples,
			int max, HashMap<String, Integer> map) throws IOException {

		/*
		for (int i = 0; i < uniqueStaplesNoDash.size(); i++) {
			//if (isPalindrome(uniqueStaplesNoDash.get(i))) {
			if (uniqueStaplesNoDash.contains(flipItAndReverseIt(uniqueStaplesNoDash.get(i))) && !flipItAndReverseIt(uniqueStaplesNoDash.get(i)).equals(uniqueStaplesNoDash.get(i))) {
				uniqueStaplesNoDash.remove(i);
				uniqueStaples.remove(i);

			}
			//}
		}
		 */

		ArrayList<String> tetherDNA = new ArrayList<String>();
		/*
		if (star) {
			for (int i = 0; i < uniqueTethers.size(); i++) {

				tetherDNA.add("5'-C");
				tetherDNA.add(complements.get(pos.get(uniqueTethers.get(i).charAt(1)).charAt(0)).toString());
				tetherDNA.add(complements.get(pol.get(uniqueTethers.get(i).charAt(0)).charAt(1)).toString() + 
						complements.get(pol.get(uniqueTethers.get(i).charAt(0)).charAt(0)).toString());
				tetherDNA.add(starTetherFwd + "-3");
					
				} else {
					tetherDNA.add("5'-" + starTetherBkwd);
					tetherDNA.add(complements.get(pol.get(uniqueTethers.get(i).charAt(0)).charAt(0)).toString() + 
							complements.get(pol.get(uniqueTethers.get(i).charAt(0)).charAt(1)).toString());
					tetherDNA.add(complements.get(pos.get(uniqueTethers.get(i).charAt(1)).charAt(0)).toString());
					tetherDNA.add("C-3'");
				}
				 
			}
		} else {
		*/
			for (int i = 0; i < uniqueTethers.size(); i++) {
				tetherDNA.add("G");
				tetherDNA.add(complements.get(pos.get(uniqueTethers.get(i).charAt(1)).charAt(0)).toString());
				tetherDNA.add(complements.get(pol.get(uniqueTethers.get(i).charAt(0)).charAt(1)).toString() + 
						complements.get(pol.get(uniqueTethers.get(i).charAt(0)).charAt(0)).toString());
				tetherDNA.add(linearTetherFH);
			}
	//	}

		ArrayList<String> stapleDNA = new ArrayList<String>();
		if (star) {
			for (int i = 0; i < uniqueStaples.size(); i++) {
				stapleDNA.add(starStapleFwd);
				stapleDNA.add(pol.get(uniqueStaplesNoDash.get(i).charAt(0)));
				stapleDNA.add(pos.get(uniqueStaplesNoDash.get(i).charAt(1)));
				stapleDNA.add("G");
				stapleDNA.add("AAA");
				/*
				stapleDNA.add("G");
				stapleDNA.add(pos.get(uniqueStaples.get(i).charAt(4)));
				stapleDNA.add("" + pol.get(uniqueStaples.get(i).charAt(3)).charAt(1) + 
						pol.get(uniqueStaples.get(i).charAt(3)).charAt(0));
				stapleDNA.add(starStapleBkwd);
				stapleDNA.add("AA");
				 */
				stapleDNA.add(starStapleFwd);
				stapleDNA.add(pol.get(uniqueStaplesNoDash.get(i).charAt(2)));
				stapleDNA.add(pos.get(uniqueStaplesNoDash.get(i).charAt(3)));
				stapleDNA.add("G");
				stapleDNA.add("AAA");
				stapleDNA.add(starStapleFwd);
				stapleDNA.add(pol.get(uniqueStaplesNoDash.get(i).charAt(4)));
				stapleDNA.add(pos.get(uniqueStaplesNoDash.get(i).charAt(5)));
				stapleDNA.add("G");
				stapleDNA.add("AAA");
				stapleDNA.add(starStapleFwd);
				stapleDNA.add(pol.get(uniqueStaplesNoDash.get(i).charAt(6)));
				stapleDNA.add(pos.get(uniqueStaplesNoDash.get(i).charAt(7)));
				stapleDNA.add("G");
				/*
				stapleDNA.add("AAA");
				stapleDNA.add(pos.get(uniqueStaples.get(i).charAt(10)));
				stapleDNA.add("" + pol.get(uniqueStaples.get(i).charAt(9)).charAt(1) + 
						pol.get(uniqueStaples.get(i).charAt(9)).charAt(0));
				stapleDNA.add(starStapleBkwd + "-3'");
				 */
			}			
		} else {
			for (int i = 0; i < uniqueStaplesNoDash.size(); i++) {
				stapleDNA.add(linearStapleFH);
				stapleDNA.add(pol.get(uniqueStaplesNoDash.get(i).charAt(0)));
				stapleDNA.add(pos.get(uniqueStaplesNoDash.get(i).charAt(1)));
				stapleDNA.add("C");
				stapleDNA.add("AAA");
				stapleDNA.add("" + linearStapleFH);
				stapleDNA.add(pol.get(uniqueStaplesNoDash.get(i).charAt(2)));
				stapleDNA.add(pos.get(uniqueStaplesNoDash.get(i).charAt(3)));
				stapleDNA.add("C");
			}
		}

		
		FileWriter fin = new FileWriter(fileLoc + ".in");
		int num = uniqueStaplesNoDash.size() + uniqueTethers.size();
		fin.write(num + "\n");
			
		FileWriter fcon = new FileWriter(fileLoc + ".con");
		
		FileWriter fseqs = new FileWriter(fileLoc + "CorrectSeqs.txt");

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		Row row;
		Cell cell;

		XSSFFont posFont = (XSSFFont) workbook.createFont();
		posFont.setColor(IndexedColors.VIOLET.getIndex());
		posFont.setBold(true);

		XSSFFont compFont = (XSSFFont) workbook.createFont();
		compFont.setColor(IndexedColors.GOLD.getIndex());
		compFont.setBold(true);

		XSSFFont AFont = (XSSFFont) workbook.createFont();
		AFont.setColor(IndexedColors.BRIGHT_GREEN1.getIndex());
		AFont.setBold(true);


		int r = 0;
		int c;
		row = sheet.createRow(r++); c = 0;
		cell = row.createCell(c++); 
		cell.setCellValue("Staples:\t\n");
		c+=2;
		cell = row.createCell(c);	
		cell.setCellValue("Concentration: ");
		
		int count = 0;
		int sequence = 1;
		for (int i = 0; i < uniqueStaplesNoDash.size(); i++) {
			row = sheet.createRow(r++); c = 0;
			cell = row.createCell(c++);
			cell.setCellValue("Sequence " + (sequence++) + ":");
			cell = row.createCell(c++);
			cell.setCellValue(uniqueStaplesNoDash.get(i));

			XSSFRichTextString st = new XSSFRichTextString(stapleDNA.get(count++));

			if(star) {
				st.append(stapleDNA.get(count++), compFont);
				st.append(stapleDNA.get(count++), posFont);
				st.append(stapleDNA.get(count++));
				st.append(stapleDNA.get(count++), AFont);
				st.append(stapleDNA.get(count++));
				st.append(stapleDNA.get(count++), compFont);
				st.append(stapleDNA.get(count++), posFont);
				st.append(stapleDNA.get(count++));
				st.append(stapleDNA.get(count++), AFont);
				st.append(stapleDNA.get(count++));
				st.append(stapleDNA.get(count++), compFont);
				st.append(stapleDNA.get(count++), posFont);
				st.append(stapleDNA.get(count++));
				st.append(stapleDNA.get(count++), AFont);
				st.append(stapleDNA.get(count++));
				st.append(stapleDNA.get(count++), compFont);
				st.append(stapleDNA.get(count++), posFont);
				st.append(stapleDNA.get(count++));

				/*
				st.append(stapleDNA.get(count++), posFont);
				st.append(stapleDNA.get(count++), compFont);
				st.append(stapleDNA.get(count++));
				st.append(stapleDNA.get(count++), AFont);
				st.append(stapleDNA.get(count++));
				st.append(stapleDNA.get(count++), compFont);
				st.append(stapleDNA.get(count++), posFont);
				st.append(stapleDNA.get(count++));
				st.append(stapleDNA.get(count++), AFont);
				st.append(stapleDNA.get(count++));
				st.append(stapleDNA.get(count++), posFont);
				st.append(stapleDNA.get(count++), compFont);
				st.append(stapleDNA.get(count++));
				 */
			} else {
				st.append(stapleDNA.get(count++), compFont);
				st.append(stapleDNA.get(count++), posFont);
				st.append(stapleDNA.get(count++));
				st.append(stapleDNA.get(count++), AFont);
				st.append(stapleDNA.get(count++));
				st.append(stapleDNA.get(count++), compFont);
				st.append(stapleDNA.get(count++), posFont);
				st.append(stapleDNA.get(count++));
			}


			cell = row.createCell(c++); 
			cell.setCellValue(st);
			
			fin.write(st + "\n");
			
			
			cell = row.createCell(c++);
			String pointOne = "0.1" + orderOfMag;
			double ptOne = Double.parseDouble(pointOne);
			double concentration = conc + ptOne;
			cell.setCellValue(concentration);
			fcon.write(concentration + "\n");
	
			
		}
		
		
		row = sheet.createRow(r++); c = 0;

		row = sheet.createRow(r++); c = 0; 
		cell = row.createCell(c++); 
		cell.setCellValue("Tethers:\t\n");
		c = c+2;
		cell = row.createCell(c++);
		cell.setCellValue("Count");
		cell = row.createCell(c++);
		cell.setCellValue("Concentration");
		count = 0;
		for (int i = 0; i < uniqueTethers.size(); i++) {
			row = sheet.createRow(r++); c = 0;
			cell = row.createCell(c++);
			cell.setCellValue("Sequence " + (sequence++) + ":");
			cell = row.createCell(c++); 
			cell.setCellValue(uniqueTethers.get(i));
			XSSFRichTextString sq = new XSSFRichTextString(tetherDNA.get(count++));
			/*
			if (star) {
				if (i%2 == 0) {
					sq.append(tetherDNA.get(count++), posFont);
					sq.append(tetherDNA.get(count++), compFont);
					sq.append(tetherDNA.get(count++));
				} else {
					sq.append(tetherDNA.get(count++), compFont);
					sq.append(tetherDNA.get(count++), posFont);
					sq.append(tetherDNA.get(count++));
				}
			} else {
			*/
				sq.append(tetherDNA.get(count++), posFont);
				sq.append(tetherDNA.get(count++), compFont);
				sq.append(tetherDNA.get(count++));
		//	}
				
			cell = row.createCell(c++); 
			cell.setCellValue(sq);
			
			fin.write(sq + "\n");
			
			cell = row.createCell(c++); 
			cell.setCellValue(tetherNums.get(i));
			
			cell = row.createCell(c++); 
			double concentration = tetherNums.get(i) * conc;
			cell.setCellValue(""+concentration);
			
			fcon.write(concentration + "\n");
			
			

		}

		row = sheet.createRow(r++); c = 0;

		
		if (!star) {
			row = sheet.createRow(r++); c = 0;
			cell = row.createCell(c++); 
			cell.setCellValue("Correct Sequence Combos:\t\n");
			for (int i = 0; i < uniqueSeqs.size(); i++) {
				row = sheet.createRow(r+i); c = 0;
				cell = row.createCell(c+1); 
				System.out.println(uniqueSeqs.get(i));
				cell.setCellValue(uniqueSeqs.get(i));
			}


			int[][] histogram = new int[uniqueSeqs.size()][num];
			
			for (int sequ = 0; sequ < uniqueSeqs.size(); sequ++) {
				int  charac;

				String combo = "";

				int numTethers = (uniqueSeqs.get(sequ).length()/2);
				int numStaples = numTethers-1;
				String[] staple = new String[numStaples];
				String[] tether = new String[numTethers];

				charac = 0;
				for (int teth = 0; teth<numTethers; teth++) {
					tether[teth] = "" + uniqueSeqs.get(sequ).charAt(charac)+uniqueSeqs.get(sequ).charAt(charac+1);
					charac += 2;
				}

				charac = 0;
				for (int stape = 0; stape<numStaples; stape++) {
					//staple[stape] = tether[stape] + "-" + tether[stape+1];
					staple[stape] = "" + tether[stape] + tether[stape+1];
					System.out.println(staple[stape]);
				}
				
				
				for (int t = 0; t<numStaples; t++) {
					histogram[sequ][map.get(tether[t])-1] += 1;
					combo += map.get(tether[t]);
					combo += " - ";
					histogram[sequ][map.get(staple[t])-1] += 1;
					combo += map.get(staple[t]);
					combo += " - ";
				}
				histogram[sequ][map.get(tether[tether.length-1])-1] += 1;
				combo+= map.get(tether[tether.length-1]);
				System.out.println(combo);
				for(int i = 0; i<num; i++) {
					System.out.print(histogram[sequ][i] + "\t");
					fseqs.write(histogram[sequ][i] + "\t");
				}
				fseqs.write("\n");
				
				
				
				cell = sheet.getRow(r+sequ).createCell(c); 
				cell.setCellValue(combo);
			}
		}
		
		
		int complex = (p * 2)-1;
		fin.write("3\n");

		
		workbook.write(fout);
		fin.close();
		fcon.close();
		fseqs.close();
		fout.close();
		//workbook.close();
		java.lang.System.exit(0);
	}


	public String[][] permuteRecur(int posss, HashMap<Character, String> posNums, int counter, String OGseq) {
		int i = 0;
		int ltr = 0;
		int seq = 0;
		while (seq < seqs.length) {
			if (i < counter) {
				if (ltr == OGseq.length()) {
					ltr = 0;
				} 
				Integer posss1 = posss+1;
				seqs[seq][posss] = OGseq.charAt(ltr) + "" + posNums.get(posss1.toString().charAt(0));
				seq++;
				i++;
			} else {
				ltr++;
				i = 0;
			}
		}

		if (counter != 1) {
			return permuteRecur(posss+1, posNums, counter/OGseq.length(), OGseq);
		} else {
			return seqs;
		}
	}

	public String[][] permuteRecur(int posss, int counter, String OGseq) {
		int i = 0;
		int ltr = 0;
		int seq = 0;
		while (seq < seqs.length) {
			if (i < counter) {
				if (ltr == OGseq.length()) {
					ltr = 0;
				} 
				seqs[seq][posss] = OGseq.charAt(ltr) + "" + (posss+1);
				seq++;
				i++;
			} else {
				ltr++;
				i = 0;
			}
		}

		if (counter != 1) {
			return permuteRecur(posss+1, counter/OGseq.length(), OGseq);
		} else {
			return seqs;
		}
	}

	public boolean isPalindrome(String s) {
		String fwd;
		String bck;
		for (int i = 0; (i+1 < (s.length())/2); i++) {
			fwd = "" + s.charAt(i) + s.charAt(i+1);
			bck = "" + s.charAt(s.length()-2-i) + s.charAt(s.length()-1-i);
			if (!fwd.equals(bck)) {
				return false;
			} 
		}
		return true;
	}

	public String flipItAndReverseIt(String s) {
		String fwd;
		String bck;
		String[] reversed = new String[s.length()/2];

		int n;
		if (reversed.length % 2 == 0) {
			n = 0;
		} else {
			n = 1;
		}

		int f = 0;
		int b = s.length()-1;
		for (int i = 0; i < (reversed.length-n)/2; i++) {
			fwd = "" + s.charAt(f) + s.charAt(f+1);
			f += 2;
			bck = "" + s.charAt(b-1) + s.charAt(b);
			b -= 2;
			reversed[i] = bck;
			reversed[reversed.length-1-i] = fwd;
		}

		if (n==1) {
			reversed[(reversed.length-n)/2] = "" + s.charAt(f) + s.charAt(f+1);
			//reversed[((reversed.length-n)/2)+1] =  "" + s.charAt(b-1) + s.charAt(b);
		}


		String ans = "";
		for (String s1: reversed) {
			ans += s1;
		}
		return ans;

	}


}


