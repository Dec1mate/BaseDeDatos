package javaOracle;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import oracle.jdbc.OracleTypes;

import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JScrollBar;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Toolkit;

public class FormularioGoleadores extends JFrame {

	private JPanel contentPane;
	private JFrame ventanaPrincipal;
	private JTable table;
	private File f;
	private JFrame frameAnterior;
	private JFrame frameActual;

	

	public JFrame getFrameActual() {
		return frameActual;
	}



	public void setFrameActual(JFrame frameActual) {
		this.frameActual = frameActual;
	}



	/**
	 * Create the frame.
	 */
	public FormularioGoleadores(File f) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(GestionJugadores.class.getResource("/images/logo_compacto.png")));
		setResizable(false);
		setTitle("Gestion de Jugadores");
		
		
		
		this.f=f;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 783, 433);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		
		JComboBox comboBox = new JComboBox();
		comboBox.setBounds(12, 12, 244, 24);
		contentPane.add(comboBox);
		
		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setBounds(12, 48, 244, 24);
		contentPane.add(comboBox_1);
		
		JComboBox comboBox_2 = new JComboBox();
		comboBox_2.setBounds(12, 84, 244, 24);
		contentPane.add(comboBox_2);
		
		JButton btnConsultar = new JButton("Consultar");
		btnConsultar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				String equipo = comboBox.getSelectedItem().toString();
				String anyo = comboBox_1.getSelectedItem().toString();
				String jugador = comboBox_2.getSelectedItem().toString();
				
				Connection con = null;
		        ResultSet rs = null;
		        CallableStatement cs = null; 
		        
		        try{
		        	
		        	con = (new ConexionOracle(f)).Conectar();
		        	
		        	cs = con.prepareCall(SqlTools.ConstruirLlamadaProcedimiento("MOSTRARGOLEADORES", 4));
		        	
		        	int pos=0;
		        	cs.setString(++pos, equipo);
		        	cs.setString(++pos, anyo);
		        	cs.setString(++pos, jugador);
		        	cs.registerOutParameter(++pos, OracleTypes.CURSOR);
		        	
		        	cs.execute();
		        	
		        	rs = (ResultSet) cs.getObject(2);   // Nuestro cursor, convertido en ResultSet
		        	
		        	String Titulos[]={" DORSAL "," PUESTO "," NOMBRE "," MUNDIALES JUGADOS "};
		            String fila[]=new String[4];
		            
		            DefaultTableModel modelo = new DefaultTableModel(null, Titulos);
		            
		            while (rs.next()) {        
		                fila[0] = rs.getString("N1");
		                fila[1] = rs.getString("C2");
		                fila[2] = rs.getString("C3");
		                fila[3] = rs.getString("C4");
		                modelo.addRow(fila);
		            }

		            table.setModel(modelo);
		            
		        	
		        }catch(Exception e1){		        	
		        	e1.printStackTrace();		        
		        }
		
				
				
			}
		});
		btnConsultar.setBounds(268, 12, 117, 25);
		contentPane.add(btnConsultar);
		
		JButton btnAtras = new JButton("Atras");
		btnAtras.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FormularioPrincipal fp=new FormularioPrincipal();
				fp.setVisible(true);
				dispose();
			}
		});
		btnAtras.setBounds(646, 363, 117, 25);
		contentPane.add(btnAtras);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 137, 751, 214);
		contentPane.add(scrollPane);
		
		table = new JTable();
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				DefaultTableModel tm = (DefaultTableModel) table.getModel();
				
				String jugador = String.valueOf(tm.getValueAt(table.getSelectedRow(),2));
				frameActual.setVisible(false);
				FormularioJugador fj=new FormularioJugador(jugador,f);
				fj.setVisible(true);
				fj.setFrameAnterior(frameActual);
							
			}
		});
		scrollPane.setViewportView(table);
		
		
		
		try{
			ConexionOracle co=new ConexionOracle(this.f);
			
	        Connection con = co.Conectar();
	        String sql="SELECT EQUIPO FROM EQUIPOS";
	        
	        Statement s = con.createStatement(ResultSet.TYPE_FORWARD_ONLY,  ResultSet.CONCUR_READ_ONLY);
	        ResultSet rs = s.executeQuery(sql);
	        
	        comboBox.removeAllItems();
	        comboBox.addItem("");
	        while(rs.next()){
	        	comboBox.addItem(rs.getString("EQUIPO"));
	        }
	        SqlTools.close(rs, s,null, con);
        
		}catch(Exception e){
			e.printStackTrace();        	
		}
		
		try{
			ConexionOracle co=new ConexionOracle(this.f);
			
	        Connection con = co.Conectar();
	        String sql="select distinct(to_char(fecha_part,'YYYY')) ANYO from jugar order by 1 ASC";
	        
	        Statement s = con.createStatement(ResultSet.TYPE_FORWARD_ONLY,  ResultSet.CONCUR_READ_ONLY);
	        ResultSet rs = s.executeQuery(sql);
	        
	        comboBox_1.removeAllItems();
	        comboBox_1.addItem("");
	        while(rs.next()){
	        	comboBox_1.addItem(rs.getString("ANYO"));
	        }
	        SqlTools.close(rs, s,null, con);
        
		}catch(Exception e){
			e.printStackTrace();        	
		}
		
		try{
			ConexionOracle co=new ConexionOracle(this.f);
			
	        Connection con = co.Conectar();
	        String sql="select nombre from jugador order by 1 ASC";
	        
	        Statement s = con.createStatement(ResultSet.TYPE_FORWARD_ONLY,  ResultSet.CONCUR_READ_ONLY);
	        ResultSet rs = s.executeQuery(sql);
	        
	        comboBox_2.removeAllItems();
	        comboBox_2.addItem("");
	        while(rs.next()){
	        	comboBox_2.addItem(rs.getString("nombre"));
	        }
	        SqlTools.close(rs, s,null, con);
        
		}catch(Exception e){
			e.printStackTrace();        	
		}
		
		
	}



	public JFrame getFrameAnterior() {
		return frameAnterior;
	}



	public void setFrameAnterior(JFrame frame) {
		this.frameAnterior = frame;
	}
}