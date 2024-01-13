package client;

import Jeu.Forme;
import Jeu.Jeux.JeuLocal;
import Jeu.Jeux.JeuEnLigne;
import Jeu.Jeux.JeuEnsolo;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Vector;

public class AttenteFenetre extends JFrame{
    private JTable table;
    private JTextField champText;


    private ClientTetris client;
    private DefaultTableModel dm;

    /**
     * Creation de l'interface utilisateur pour la salle d'attente
     */
    public AttenteFenetre(ClientTetris client) throws IOException{
        this.client = client;

        // GUI stuff
        getContentPane().setLayout(null);
        getContentPane().setBackground(Color.BLACK);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setSize(650, 700);
        setResizable(false);
        setLocationRelativeTo(null);
        setTitle(client.pseudo + " EN ATTENTE");

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(24, 21, 592, 250);

        getContentPane().add(scrollPane).setBackground(Color.BLACK);

        addWindowListener(new WindowListener(){
            public void windowClosed(WindowEvent e){
            }

            public void windowOpened(WindowEvent e){
            }

            public void windowClosing(WindowEvent e){
                client.fermer();
            }

            public void windowIconified(WindowEvent e){
            }

            public void windowDeiconified(WindowEvent e){
            }

            public void windowActivated(WindowEvent e){
            }

            public void windowDeactivated(WindowEvent e){
            }
        });

        table = new JTable();

        dm = new DefaultTableModel(0, 0);
        String header[] = new String[]{"Nom du jeu", "Type de jeu", "Joeurs", ""};
        dm.setColumnIdentifiers(header);
        table.setModel(dm);

        table.setRowHeight(30);
        DefaultTableCellRenderer renduDroit = new DefaultTableCellRenderer();
        renduDroit.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(renduDroit);
        table.getColumnModel().getColumn(1).setCellRenderer(renduDroit);
        table.getColumnModel().getColumn(2).setCellRenderer(renduDroit);

        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel());
        table.setRowSorter(sorter);

        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);

        CompCellEditorRenderer compCellEditorRenderer = new CompCellEditorRenderer();
        table.setDefaultRenderer(Object.class, compCellEditorRenderer);
        table.setDefaultEditor(Object.class, compCellEditorRenderer);

        table.setBounds(24, 21, 592, 250);
        scrollPane.setViewportView(table);
        table.setBorder(new LineBorder(new Color(0, 0, 0)));

        /*JLabel lblMessages = new JLabel("MESSAGES");
        lblMessages.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblMessages.setBounds(24, 294, 210, 26);
        getContentPane().add(lblMessages);*/

        JLabel lblCreateNew = new JLabel("CREER UNE NOUVELLE PARTIE");
        lblCreateNew.setForeground(Color.WHITE);
        lblCreateNew.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblCreateNew.setBounds(50, 294, 227, 26);
        getContentPane().add(lblCreateNew);

        JLabel lblGameName = new JLabel("Nouveau jeu:");
        lblGameName.setForeground(Color.WHITE);
        lblGameName.setBounds(50, 348, 80, 14);
        getContentPane().add(lblGameName);

        champText = new JTextField();
        champText.setBounds(150, 345, 150, 25);
        getContentPane().add(champText);
        champText.setColumns(10);

        JRadioButton rdbtnNewRadioButton = new JRadioButton("En solo");
        rdbtnNewRadioButton.setBounds(50, 400, 109, 23);
        rdbtnNewRadioButton.setForeground(Color.BLACK);
        getContentPane().add(rdbtnNewRadioButton);



        /*JRadioButton rdbtnBattle = new JRadioButton("En locale");
        rdbtnBattle.setBounds(50, 440, 109, 23);
        rdbtnBattle.setForeground(Color.BLACK);
        getContentPane().add(rdbtnBattle);*/

        JRadioButton rdbtnOLBattle = new JRadioButton("Enligne");
        rdbtnOLBattle.setForeground(Color.BLACK);
        rdbtnOLBattle.setBounds(50, 480, 109, 23);
        getContentPane().add(rdbtnOLBattle);

        JLabel lblWarning = new JLabel();
        lblWarning.setBounds(100, 376, 195, 14);
        lblWarning.setForeground(Color.RED);

        lblWarning.setHorizontalAlignment(SwingConstants.RIGHT);
        getContentPane().add(lblWarning);

        ButtonGroup group = new ButtonGroup();
        group.add(rdbtnNewRadioButton);/*
        group.add(rdbtnWha);
        group.add(rdbtnHumanVs);*/
        group.add(rdbtnOLBattle);

        group.setSelected(rdbtnNewRadioButton.getModel(), true);

        JButton btnNewButton = new JButton("Creer une salle");
        btnNewButton.setBounds(50, 605, 118, 34);

        getContentPane().add(btnNewButton);
        btnNewButton.addActionListener(e -> {
            if (champText.getText().isEmpty()){
                lblWarning.setText("*Veillez entrer le nom de la partie svp");
            } else {
                for (int i = 0; i < dm.getRowCount(); i++){
                    if (dm.getValueAt(i, 0).equals(champText.getText())){
                        lblWarning.setText("*Stp ce nom est deja pris");
                        return;
                    }
                }
                lblWarning.setText("");

                client.output.println("**createRoom\n" + champText.getText());      // send messages to server
                if (rdbtnNewRadioButton.isSelected()){
                    client.output.println("Solo");

              /*  } else if (rdbtnBattle.isSelected()){
                    client.output.println("Battle");*/
                } else if (rdbtnOLBattle.isSelected()){
                    client.output.println("Online Battle");
                }
                client.output.flush();
            }
        });


        new Thread(this :: getUpdate).start();
    }

    /**
     * recevoir les mis a jour du serveur et les adapter
     */
    public void getUpdate(){
        client.outputOpen = true;
        String userInput, userInput1, userInput2, userInput3, userInput4;
        while (client.outputOpen){
            try {
                if (client.input.ready()){
                    userInput = client.input.readLine();
                    switch (userInput){
                        /*case "**message":
                            userInput1 = client.input.readLine();  // nickname
                            userInput2 = client.input.readLine();  // icon
                            userInput3 = client.input.readLine();  // timeStamp
                            userInput4 = client.input.readLine();  // message
                            appendFirstLineToPane(userInput2, userInput1, userInput3);
                            appendToPane(userInput4, Color.BLACK);
                            break;*/

                        case "**gameRoom":
                            boolean identified = false;

                            userInput1 = client.input.readLine();  // gameroom name
                            userInput2 = client.input.readLine();  // game type
                            userInput3 = client.input.readLine();  // player count
                            userInput4 = client.input.readLine();  // max count

                            for (int i = 0; i < table.getRowCount(); i++){
                                if (userInput1.equals(table.getValueAt(i, 0))){
                                    identified = true;
                                    if (userInput2 != table.getValueAt(i, 1)){
                                        table.setValueAt(userInput2, i, 1);
                                    }
                                    if ((userInput3 + "/" + userInput4) != table.getValueAt(i, 2)){
                                        table.setValueAt((userInput3 + "/" + userInput4), i, 2);
                                        if (Integer.valueOf(userInput3) >= Integer.valueOf(userInput4)){
                                            table.setValueAt("Play unclickable", i, 3);
                                        } else {
                                            table.setValueAt("Play", i, 3);
                                        }
                                    }
                                }
                            }

                            if (!identified){
                                Vector<Object> data = new Vector<Object>();
                                data.add(userInput1);
                                data.add(userInput2);
                                data.add(userInput3 + "/" + userInput4);
                                if (Integer.valueOf(userInput3) >= Integer.valueOf(userInput4)){
                                    data.add("Play unclickable");
                                } else {
                                    data.add("Play");
                                }
                                dm.addRow(data);
                            }
                            break;

                        case "**permission to play":
                            userInput1 = client.input.readLine();  // game name
                            for (int i = 0; i < dm.getRowCount(); i++){
                                if (userInput1.equals(dm.getValueAt(i, 0))){
                                    switch (dm.getValueAt(i, 1).toString()){
                                        case "Solo":
                                            client.jeu = new JeuEnsolo(client.output);
                                            break;


                                        case "Battle":
                                            client.jeu = new JeuLocal(client.pseudo, client.CheminIcon, client.output);
                                            break;
                                        case "Online Battle":
                                            client.jeu = new JeuEnLigne(client.pseudo, client.CheminIcon, client.output);
                                            break;
                                    }
                                    client.jeu.setGameName(userInput1);
                                    new Thread(() -> client.jeu.run()).start();
                                }
                            }
                            break;

                        case "**permission to view":
                            break;

                        case "**onlineGame":
                            userInput3 = client.input.readLine();   // roomName
                            userInput1 = client.input.readLine();   // name of command
                            userInput2 = client.input.readLine();   // command data

                            if (userInput1.equals("enqueueTetromino")){
                                client.jeu.enqueueTetromino(new Forme(userInput2.charAt(0)));
                            } else {
                                ((JeuEnLigne)client.jeu).handleAdversaire(userInput1, userInput2);
                            }
                            break;

                        case "**viewGameP1":
                            // TODO
                            break;

                        case "**viewGameP2":
                            // TODO
                            break;
                    }
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Combinaison des cellules de l'editeur...
     */
    class CompCellEditorRenderer extends AbstractCellEditor implements TableCellRenderer, TableCellEditor{
        private String lastSelected = null;

        /**
         * rendu d'une cellule


         */
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){
            return getComponent(value, row);
        }


        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column){
            if (isSelected && value != null){
                lastSelected = value.toString();
            }
            return getComponent(value, row);
        }

        /**
         * Generation  des composants
         */
        private Component getComponent(Object value, int row){
            if (value != null && (value.toString().equals("Play"))){
                JPanel p = new JPanel();
                p.setLayout(null);

                JButton jButton = new JButton("JOUER!");
                jButton.setBounds(3, 3, 140, 24);
                jButton.addActionListener(e -> {
                    client.output.println("**enterRoomAndPlay\n" + table.getValueAt(row, 0));
                    client.output.flush();
                    lastSelected = "Play";
                });

                p.add(jButton);
                return p;

            } else if (value != null && value.toString().equals("Play unclickable")){
                JPanel p = new JPanel();
                p.setLayout(null);

                JButton jButton = new JButton("play");
                jButton.setBounds(3, 3, 140, 24);
                jButton.setEnabled(false);

                lastSelected = "Play unclickable";

                p.add(jButton);
                return p;
            }
            lastSelected = value.toString();
            return new JLabel(lastSelected);
        }

        /**
         *Obtenir les valeurs des cellules
         *
         * return la valeur selectionnee a retourner
         */
        @Override
        public Object getCellEditorValue(){
            return lastSelected;
        }

       //si la cellule peut etre cliquee

        @Override
        public boolean isCellEditable(EventObject anEvent){
            return ((JTable)anEvent.getSource()).getSelectedColumn() >= 3;
        }
    }



}