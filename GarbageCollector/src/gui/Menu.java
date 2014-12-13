package gui;

import elements.trucks.Truck;
import map.Map;

import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;


public class Menu extends JFrame {

    public Menu() {

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        TitledBorder title = BorderFactory.createTitledBorder("Truck Details");

        JComponent content = (JComponent)this.getContentPane();
        content.setBorder(title);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;

        this.add(new JLabel("     "));
        this.add(new JLabel("List Of Trucks"), c);
        this.add(new JLabel("Current Destination"), c);
        this.add(new JLabel("Current Garbage Amount"), c);

        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 3;
        ArrayList<Truck> trucks = Map.INSTANCE.trucks;

        for (int i = 0; i < trucks.size(); i++) {
            c.gridy++;
            System.out.println("AAAA" + trucks.get(i).getAgentName());
            this.add(trucks.get(i).getComponent(), c);
            //this.add(new TruckDetailsComponent(trucks.get(i)), c);
        }

        this.setSize(400, 300);
        setVisible(true);
    }
}
