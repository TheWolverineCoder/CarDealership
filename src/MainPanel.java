import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class MainPanel extends JFrame implements ChangeListener{
	Connection conn=null;
	int id=-1;
	static int currentTab = 1;
	PreparedStatement state = null;
	JTable brandTable = new JTable();
	JScrollPane scroller = new JScrollPane(brandTable);
	JTabbedPane tab = new JTabbedPane();
	JPanel brandConfig = new JPanel();
	JPanel carConfig = new JPanel();
	JPanel salePanel = new JPanel();
	JPanel searchPanel = new JPanel();
	
	//panels for brandConfig Tab
	//upPanel for TextFields
	JPanel upPanel = new JPanel();
	//midPanel for the Buttons
	JPanel midPanel = new JPanel();
	//downPanel for the results
	JPanel downPanel = new JPanel();
	//----------------------------
	
	//labels for brandConfig Tab
	JLabel carBrand = new JLabel("Въведи марка на автомобила: ");
	JLabel countryOfOrigin = new JLabel("Държава на производство: ");
	
	//TextFields for brandConfig Tab
	JTextField carBrandTF = new JTextField();
	JTextField countryOfOriginTF = new JTextField();
	
	//buttons for brandConfig Tab
	JButton addBtn = new JButton("Добави");
	JButton deleteBtn = new JButton("Изтрий");
	JButton editBtn = new JButton("Промени");
	
	//-----------------------------
	
	//panels for carConfig Tab
	//upPanel for TextFields
	JPanel upPanelCar = new JPanel();
	//midPanel for the Buttons
	JPanel midPanelCar = new JPanel();
	//downPanel for the results
	JPanel downPanelCar = new JPanel();
	
	
	
	
	//method which checks which tab is opened
	public void stateChanged(ChangeEvent e) {
        JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
        int selectedIndex = tabbedPane.getSelectedIndex();
        currentTab = selectedIndex;
    }
	
	
	public MainPanel() {
		
		this.setSize(800,800);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//this.setLayout(new GridLayout(3,1));
		
		//brandConfig Code
		//-------------------------------------------------
		//configuration of upPanel in brandConfig
		brandConfig.setLayout(new GridLayout(3,1));
		upPanel.setLayout(new GridLayout(2,2));
		upPanel.add(carBrand);
		upPanel.add(carBrandTF);
		upPanel.add(countryOfOrigin);
		upPanel.add(countryOfOriginTF);
		brandConfig.add(upPanel);
		
		//Configuration of midPanel in brandConfig
		midPanel.setLayout(new GridLayout(3,1));
		midPanel.add(addBtn);
		midPanel.add(deleteBtn);
		midPanel.add(editBtn);
		brandConfig.add(midPanel);
		
		//buttons action listeners
		addBtn.addActionListener(new AddAction());
		deleteBtn.addActionListener(new DeleteAction());
		editBtn.addActionListener(new EditAction());
		addBtn.setBackground(Color.green);
		deleteBtn.setBackground(new Color(255,138,138));
		editBtn.setBackground(new Color(204,204,255));
		
		
		//Adding tabs to the mainPanel
		tab.add(brandConfig,"Конфигурация на марки");
		tab.add(carConfig,"Конфигурация на автомобили");
		tab.add(salePanel,"Продажби");
		tab.add(searchPanel,"Търсене по критерии");
		this.add(tab);
		
		//setting fontSize to bigger one
		carBrand.setFont(carBrand.getFont().deriveFont(20.0f));
		countryOfOrigin.setFont(countryOfOrigin.getFont().deriveFont(20.0f));
		
		
		//downPanel of brandConfig
		downPanel.add(scroller);
		brandConfig.add(downPanel);
		scroller.setPreferredSize(new Dimension(760,240));
		brandTable.setModel(DBBrandHelper.getAllData());
		brandTable.addMouseListener(new TableListener());
		//-------------------------------------------------
		
		
		
		
		
		
		
		
		tab.addChangeListener(this);
		this.setVisible(true);
	}
	
	
	//clears text fields in first form
	public void clearFirstForm() {
		carBrandTF.setText("");
		countryOfOriginTF.setText("");
		
	}
	
	
	//class with addBtn Action
	class AddAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			conn = DBBrandHelper.getConnection();
			String sql = "insert into BRANDS values(null,?,?)";
			try {
				state = conn.prepareStatement(sql);
				state.setString(1, carBrandTF.getText());
				state.setString(2, countryOfOriginTF.getText());
				
				state.execute();
				brandTable.setModel(DBBrandHelper.getAllData());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally {
				try {
					state.close();
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			clearFirstForm();
		}
		
	}
	
	
	//class with deleteBtn Action
	class DeleteAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			conn = DBBrandHelper.getConnection();
			String sql = "DELETE FROM BRANDS WHERE ID=?";
			try {
				state = conn.prepareStatement(sql);
				state.setInt(1, id);
				state.execute();
				id = -1;
				brandTable.setModel(DBBrandHelper.getAllData());
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
	}
	
	//class with editBtn Action
	class EditAction implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			conn = DBBrandHelper.getConnection();
			String sql = "UPDATE BRANDS SET BRAND = \'" + carBrandTF.getText() + "\', COUNTRY = \'"  + countryOfOriginTF.getText() + "\' WHERE ID=?;";
			try {
				state = conn.prepareStatement(sql);
				state.setInt(1, id);
				state.execute();
				id = -1;
				brandTable.setModel(DBBrandHelper.getAllData());
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	class TableListener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
			int row = brandTable.getSelectedRow();
			id = Integer.parseInt(brandTable.getValueAt(row, 0).toString());
			if(e.getClickCount() == 2) {
			    if(currentTab == 0) {
			    	carBrandTF.setText(brandTable.getValueAt(row, 1).toString());
					countryOfOriginTF.setText(brandTable.getValueAt(row, 2).toString());
			    }
			    
			    
			}
			
		}

		//e.getClickCount()-for edit
		//if getClickCount() == 2 - edit
		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
}