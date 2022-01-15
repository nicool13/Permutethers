package permutethers;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.*;


public class numChooser implements ActionListener {
	JTextField t;
	JTextField t1;
	JCheckBox c;
	JCheckBox c1;
	JCheckBox c2;
	JFrame f;
	JButton b;
	public numChooser() {
		f = new JFrame("Permutethers");
		//JPanel p = new JPanel();
		JLabel l= new JLabel("Number of positions: ");
		l.setBounds(10, 15, 200, 30);
		t = new JTextField();
		t.setBounds(200, 15, 40, 30);
		t.setEditable(true);
		
		JLabel l1= new JLabel("Number of polymers: ");
		l1.setBounds(10, 50, 200, 30);
		t1 = new JTextField();
		t1.setBounds(200, 50, 40, 30);
		t1.setEditable(true);
		
		JLabel l2 = new JLabel("Symmetrical?");
		l2.setBounds(10, 125, 100, 30);
		c = new JCheckBox();
		c.setBounds(90, 125, 30, 30);
		
		JLabel l3 = new JLabel("Linear?");
		l3.setBounds(10, 85, 100, 30);
		c1 = new JCheckBox();
		c1.setBounds(60, 85, 30, 30);
		c1.addActionListener(this);
		
		JLabel l4 = new JLabel("Star?");
		l4.setBounds(120, 85, 100, 30);
		c2 = new JCheckBox();
		c2.setBounds(170, 85, 30, 30);
		c2.addActionListener(this);
		
		
		
		b = new JButton("Go");
		b.setBounds(170, 125, 70, 30);
		b.addActionListener(this);
				
		f.add(l3);
		f.add(l4);
		f.add(c1);
		f.add(c2);
		
		
		f.add(l);
		f.add(l1);
		f.add(t);
		f.add(t1);
		f.add(l2);
		f.add(c);
		f.add(b);
		f.setLayout(null);
		f.setSize(280,210);
		f.setVisible(true);
		
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(b)) {
			int numPos = Integer.parseInt(t.getText());
			int numPol = Integer.parseInt(t1.getText());
			boolean symmetry = c.isSelected();
			boolean star = c2.isSelected();
			t.setEditable(false);

			Encoder y = new Encoder(numPos, numPol, symmetry, star);
			return;
		} else if (e.getSource().equals(c2)) {
			if (c2.isSelected()) {
				t.setText("4");
				t.setForeground(Color.RED);
				t.setVisible(true);
				c1.setEnabled(false);
			} else {
				t.setText("");
				t.setForeground(Color.BLACK);
				t.setVisible(true);
				c1.setEnabled(true);
			}
		} else if (e.getSource().equals(c1)) {
			if (c1.isSelected()) {
				c2.setEnabled(false);
			} else {
				c2.setEnabled(true);
			}
		}
		
		
	}




	
}
