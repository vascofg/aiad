package gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import elements.trucks.Truck;

public class Menu extends JPanel {
	private static final long serialVersionUID = 1L;
	private GridBagConstraints c = new GridBagConstraints();
	private JPanel container;

	public Menu() {

		// this.setLayout(new GridBagLayout());

		TitledBorder title = BorderFactory.createTitledBorder("Truck Details");

		this.setBorder(title);

		container = new JPanel(new GridBagLayout());

		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		c.weighty = 1;
		c.weightx = 1;

		c.insets = new Insets(5, 10, 5, 10);
		c.gridx = 0;
		container.add(new JLabel("List Of Trucks"), c);
		c.gridx = 1;
		container.add(new JLabel("Current Destination"), c);
		c.gridx = 2;
		container.add(new JLabel("Current Garbage Amount"), c);
		this.add(container, BorderLayout.NORTH);
	}

	public void addTrucks(ArrayList<Truck> trucks) {
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;

		for (int i = 0; i < trucks.size(); i++) {
			c.gridy++;
			container.add(trucks.get(i).getComponent(), c);
		}
		this.add(container, BorderLayout.NORTH);
	}
}
