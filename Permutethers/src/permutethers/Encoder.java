package permutethers;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.*;

public class Encoder implements ActionListener {
	int numPos;
	int numPol;
	int p;
	boolean even;	
	boolean symmetry;
	boolean star;
	JTextField[] posCode;
	JTextField[] polCode;
	JTextField tf;
	JTextField tf1;

	public Encoder(int numPos, int numPol, boolean symmetry, boolean star) {
		p = numPos;
		this.symmetry = symmetry;
		this.numPol = numPol;
		this.star = star;
		if (symmetry) {
			if (numPos % 2 == 0) {
				this.numPos = numPos/2;
				this.even = true;
			} else {
				this.numPos = (numPos+1)/2;
				this.even = false;
			}
		} else {
			this.numPos = numPos;
		}
		
		
		this.encode();
		
		
	}
	
	public void encode() {
		JFrame f = new JFrame("Encoding");
		
		int num;
		if (numPos >= numPol) {
			num = numPos;
		} else {
			num = numPol;
		}
	
		int h = (40 * (num+3)) + 100;
		f.setSize(510, h);
		
		JLabel l1 = new JLabel("1 base pair codes for position: Y");
		JLabel l2 = new JLabel("2 base pair codes for composition: XX");
		JSeparator s = new JSeparator();
		
		l1.setBounds(10, 10, 200, 30);
		l2.setBounds(250, 10, 250, 30);
		s.setBounds(5, 42, 500, 10);
		s.setBackground(new Color(0));
		
		JLabel l = new JLabel("Output file name: ");
		l.setBounds(40, 40*num+50, 120, 30);
		
		tf = new JTextField();
		tf.setBounds(150, 40*num+50, 300, 30);
		
		JLabel l3 = new JLabel("Staple Concentration: ");
		l3.setBounds(20, 40*num+100, 150, 30);
		JLabel l4 = new JLabel("(no excess)");
		l4.setBounds(40, 40*num+120, 120, 30);
		
		tf1 = new JTextField();
		tf1.setBounds(180, 40*num+100, 270, 30);
		
		
		JButton b = new JButton("Gooo");
		b.setBounds(250, (40*num+150), 200, 30);
		b.addActionListener(this);
		
		JLabel[] pol = new JLabel[numPol];
		polCode = new JTextField[numPol];
		String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		for (int i = 1; i <= numPol; i++) {
			pol[i-1] = new JLabel("Polymer " + abc.charAt(i-1) + ": ");
			pol[i-1].setBounds(280, (40*i)+10, 80, 30);
			f.add(pol[i-1]);
			polCode[i-1] = new JTextField();
			polCode[i-1].setBounds(345, (40*i)+10, 40, 30);
			f.add(polCode[i-1]);
		}
			
		f.add(l1);
		f.add(l2);
		f.add(s);
		f.add(tf);
		f.add(tf1);
		f.add(l);
		f.add(l3);
		f.add(l4);
		f.add(b);
		
		if (even) {
			this.encode2(f, num);
		} else {
			this.encode3(f, num);
		}
		
	}
	
	
	
	public void encode2(JFrame f, int num) {
		JLabel[] pos = new JLabel[numPos];
		posCode = new JTextField[numPos];
		for (int i = 1; i <= numPos; i++) {
			pos[i-1] = new JLabel("positions " + i + ": ");
			pos[i-1].setBounds(20, (40*i)+10, 100, 30);
			f.add(pos[i-1]);
			posCode[i-1] = new JTextField();
			posCode[i-1].setBounds(115, (40*i)+10, 40, 30);
			f.add(posCode[i-1]);
		}
			
		f.setLayout(null);
		f.setVisible(true);
		
	}
	
	public void encode3(JFrame f, int num) {
		JLabel[] pos = new JLabel[numPos];
		posCode = new JTextField[numPos];
		for (int i = 1; i <= numPos; i++) {
			pos[i-1] = new JLabel("position " + i + ": ");
			pos[i-1].setBounds(20, (40*i)+10, 100, 30);
			f.add(pos[i-1]);
			posCode[i-1] = new JTextField();
			posCode[i-1].setBounds(115, (40*i)+10, 40, 30);
			f.add(posCode[i-1]);
		}
			
		f.setLayout(null);
		f.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		/*
		 * pos: char array of position codes
		 * idx = pos - 1
		 */
		
		HashMap<Character, String> posNums = new HashMap<Character, String>();
		if (symmetry) {
			if (even) {
				Integer forward = 1;
				Integer backward = numPos*2;
				for (Integer i = 1; i <= numPos; i++) {
					posNums.put(forward.toString().charAt(0), i.toString());
					posNums.put(backward.toString().charAt(0), i.toString());
					forward++;
					backward--;
				}
			} else {
				Integer forward = 1;
				Integer backward = (numPos*2) -1;
				Integer middle = numPos;
				for (Integer i = 1; i <= numPos; i++) {
					if (i == middle) {
						posNums.put(middle.toString().charAt(0), i.toString());
					} else {
						posNums.put(forward.toString().charAt(0), i.toString());
						posNums.put(backward.toString().charAt(0), i.toString());
						forward++;
						backward--;
					}
				}

			}
		}
		
		
		HashMap<Character, String> pos = new HashMap<Character, String>();
		for (Integer i = 1; i <= numPos; i++) {
			pos.put(i.toString().charAt(0), posCode[i-1].getText());
		}
		
		
		HashMap<Character, String> pol = new HashMap<Character, String>();
		String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		for (int i = 0; i < numPol; i++) {
			pol.put(abc.charAt(i), polCode[i].getText());
		}
	
		try {
			if (symmetry) {
				Permuter p = new Permuter(this.p, even, pos, posNums, pol, tf.getText(), tf1.getText(), star);
			} else {
				Permuter p = new Permuter(this.p, pos, pol, tf.getText(), tf1.getText(), star);
			}
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
	}
	
}
