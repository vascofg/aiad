package gui;

import elements.trucks.Truck;
import map.Map;

import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;


public class Menu extends JPanel {
    private GridBagConstraints c = new GridBagConstraints();
    public Menu() {

        this.setLayout(new GridBagLayout());

        TitledBorder title = BorderFactory.createTitledBorder("Truck Details");

        this.setBorder(title);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        c.insets = new Insets(5, 10, 5, 10);
        c.gridx = 0;
        this.add(new JLabel("List Of Trucks"), c);
        c.gridx = 1;
        this.add(new JLabel("Current Destination"), c);
        c.gridx = 2;
        this.add(new JLabel("Current Garbage Amount"), c);

        this.setPreferredSize(new Dimension(400,300));
    }

    public void addTrucks(ArrayList<Truck> trucks) {
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;

        for (int i = 0; i < trucks.size(); i++) {
            c.gridy++;
            this.add(trucks.get(i).getComponent(),c);
        }

    }
}
