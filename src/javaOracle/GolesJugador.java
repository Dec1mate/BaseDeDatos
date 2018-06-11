package javaOracle;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.rowset.serial.SerialBlob;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.GroupLayout.Alignment;


public class GolesJugador extends JFrame {

	private JPanel contentPane;
	private JFrame ventanaPrincipal;
	private JTable table;
	private File f;
	private JFrame frameAnterior;
	private JFrame frameActual;
	private String anyo;

	/**
	 * Create the frame.
	 */
	public GolesJugador(String jugador, String anyo, File f) {
		
		
		
		this.f=f;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 783, 433);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
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
				GolesJugador fj=new GolesJugador(jugador,anyo ,f);
				fj.setVisible(true);
				fj.setFrameAnterior(frameActual);
							
			}
		});
		scrollPane.setViewportView(table);
		
		
		setIconImage(Toolkit.getDefaultToolkit().getImage(GestionJugadores.class.getResource("/images/logo_compacto.png")));
		setResizable(false);
		setTitle("Gestion de Jugadores");
		Connection con = null;
        ResultSet rs = null;
        CallableStatement cs = null; 
        
        try{
        	con = (new ConexionOracle(f)).Conectar();
        	
        	cs = con.prepareCall(SqlTools.ConstruirLlamadaFuncion("MOSTRARFECHAGOLv2", 2));
        	
        	int pos=0;
        	cs.registerOutParameter(++pos, OracleTypes.CURSOR);
        	cs.setString(++pos, jugador);
        	cs.setString(++pos, anyo);
        	
        	cs.execute();
        	
        	rs = (ResultSet) cs.getObject(1);   // Nuestro cursor, convertido en ResultSet
        	
        	String Titulos[]={" FECHA "," MINUTO "};
            String fila[]=new String[2];
            
            DefaultTableModel modelo = new DefaultTableModel(null, Titulos);
            
            while (rs.next()) {	            	
                fila[0] = rs.getString("C1");
                fila[1] = rs.getString("C2");
                modelo.addRow(fila);
            }

            table.setModel(modelo);
            
        	
        }catch(Exception e1){		        	
        	e1.printStackTrace();		        
        }
	}
	public JFrame getFrameActual() {
		return frameActual;
	}

	public void setFrameActual(JFrame frameActual) {
		this.frameActual = frameActual;
	}
	

	public JFrame getFrameAnterior() {
		return frameAnterior;
	}

	public void setFrameAnterior(JFrame frame) {
		this.frameAnterior = frame;
	}
}
