package javaOracle;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

import java.io.InputStream;
import java.net.URL;
import java.sql.Blob;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Dimension;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;

import java.awt.Color;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.Toolkit;

public class FormularioJugador extends JFrame {

	private JPanel contentPane;
	private JTextField tFNombre;
	private JTextField tFDireccion;
	private JTextField tFFechaNacimiento;
	private File f;
	private JLabel lFoto;
	private JRadioButton rdbtnPortero;
	private JRadioButton rdbtnDefensa;
	private JRadioButton rdbtnMedio;
	private JRadioButton rdbtnDelantero;
	private JComboBox comboBox;
	private FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivo de imagen", "jpg");
	private SerialBlob fotoBLOB;
	private JFrame frameAnterior;
	private int fotoWidth=620, fotoHeight=480;

	/**
	 * Create the frame.
	 */
	public FormularioJugador(String jugador, File f) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(FormularioJugador.class.getResource("/images/logo_compacto.png")));
		setResizable(false);
		setTitle("Formulario Jugador");
		this.f = f;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 674, 705);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JLabel lblNombre = new JLabel("Nombre");

		JLabel lblDireccion = new JLabel("Direccion");

		JLabel lblPuestoHabitual = new JLabel("Puesto habitual");

		lFoto = new JLabel("");
		lFoto.setSize(fotoWidth, fotoHeight);
		lFoto.setAlignmentX(Component.CENTER_ALIGNMENT);
		lFoto.setHorizontalAlignment(SwingConstants.CENTER);
		lFoto.setBorder(new LineBorder(new Color(0, 0, 0)));
		
		

		JLabel lblSeleccion = new JLabel("Seleccion");

		comboBox = new JComboBox();

		JButton btnGuardar = new JButton("Guardar");
		btnGuardar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				Connection con = null;
				ResultSet rs = null;
				OracleCallableStatement os = null;
				try {

					ConexionOracle obconeccion = new ConexionOracle(f);
					con = obconeccion.Conectar();

					String sql = "call prueba.grabarJugador2(?,?,?,?,?,?)";
					os = (OracleCallableStatement) con.prepareCall(sql);

					int pos = 0;

					// Cargamos los parametros de entrada IN
					// (Nombre,Equipo,Direccion,Posicion habitual, Fecha
					// nacimiento y Foto
					os.setString(++pos, tFNombre.getText());
					os.setString(++pos, (String) comboBox.getSelectedItem());
					os.setString(++pos, tFDireccion.getText());

					if (rdbtnPortero.isSelected())
						os.setString(++pos, "AR");
					else if (rdbtnDefensa.isSelected())
						os.setString(++pos, "DF");
					else if (rdbtnMedio.isSelected())
						os.setString(++pos, "MC");
					else if (rdbtnDelantero.isSelected())
						os.setString(++pos, "DL");

					java.sql.Date fecha = null;
					try {
						SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
						java.util.Date d;

						d = (java.util.Date) sdf.parse(tFFechaNacimiento.getText());
						long milliseconds = d.getTime();

						fecha = new Date(milliseconds);
					} catch (ParseException e1) {
						e1.printStackTrace();
					}

					os.setDate(++pos, fecha);
							
					if(fotoBLOB!=null) {
						os.setBytesForBlob(++pos, fotoBLOB.getBytes(1, (int) fotoBLOB.length()));
					}else{
						os.setBytesForBlob(++pos, null);
					}

					os.execute();
					JOptionPane.showMessageDialog(null, "Jugador actualizado con exito.");
				} catch (Exception e2) {
					e2.printStackTrace();
				} finally {
					SqlTools.close(rs, null,os, con);
				}

			}
		});

		JButton btnAtras = new JButton("Atras");
		btnAtras.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frameAnterior.setVisible(true);
				dispose();
			}
		});

		rdbtnPortero = new JRadioButton("Portero");

		rdbtnDefensa = new JRadioButton("Defensa");

		rdbtnMedio = new JRadioButton("Centrocampista");

		rdbtnDelantero = new JRadioButton("Delantero");

		ButtonGroup bg = new ButtonGroup();

		bg.add(rdbtnPortero);
		bg.add(rdbtnDefensa);
		bg.add(rdbtnDelantero);
		bg.add(rdbtnMedio);

		JButton btnSubirFoto = new JButton("Subir Foto");
		btnSubirFoto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Creamos el objeto JFileChooser
				JFileChooser fch = new JFileChooser();

				fch.setFileFilter(filter);
				// Abri ventana de dialog
				int opcion = fch.showOpenDialog(null);

				// Si hacemos 
				if (opcion == JFileChooser.APPROVE_OPTION) {
					try {
						// Obtenenemos el nombre del fichero seleccionado
						String nomFichero = fch.getSelectedFile().getPath();

						byte[] imgFoto = new byte[(int) fch.getSelectedFile().length()];
						InputStream inte = new FileInputStream(fch.getSelectedFile());
						inte.read(imgFoto);

						fotoBLOB = new SerialBlob(imgFoto);

						BufferedImage image = null;
						InputStream in = new ByteArrayInputStream(imgFoto);
						image = ImageIO.read(in);

						ImageIcon icono = new ImageIcon(image);
						Image imageToResize = icono.getImage();
						Image nuevaResized = imageToResize.getScaledInstance(lFoto.getWidth(), lFoto.getHeight(), java.awt.Image.SCALE_SMOOTH);
						icono = new ImageIcon(nuevaResized);

						lFoto.setIcon(icono);

					} catch (Exception e1) {
						e1.printStackTrace();
					}

				}
			}
		});

		JLabel lblFechaNacimiento = new JLabel("Fecha nacimiento");

		tFNombre = new JTextField();
		tFNombre.setColumns(10);

		tFDireccion = new JTextField();
		tFDireccion.setColumns(10);

		tFFechaNacimiento = new JTextField();
		tFFechaNacimiento.setColumns(10);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(lblPuestoHabitual)
								.addComponent(lblSeleccion)
								.addComponent(lblNombre, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblDireccion)
								.addComponent(lblFechaNacimiento))
							.addGap(32)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(rdbtnPortero)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(rdbtnDefensa)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(rdbtnMedio)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(rdbtnDelantero))
								.addComponent(tFDireccion, GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
								.addComponent(tFFechaNacimiento, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(tFNombre, GroupLayout.PREFERRED_SIZE, 330, GroupLayout.PREFERRED_SIZE))
							.addGap(49))
						.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
							.addComponent(btnGuardar)
							.addGap(167)
							.addComponent(btnAtras)))
					.addGap(35))
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(49)
					.addComponent(lFoto, GroupLayout.PREFERRED_SIZE, 558, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(49, Short.MAX_VALUE))
				.addGroup(Alignment.TRAILING, gl_contentPane.createSequentialGroup()
					.addContainerGap(276, Short.MAX_VALUE)
					.addComponent(btnSubirFoto)
					.addGap(273))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(11)
					.addComponent(lFoto, GroupLayout.PREFERRED_SIZE, 403, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(btnSubirFoto)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNombre)
						.addComponent(tFNombre, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(12)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblDireccion)
						.addComponent(tFDireccion, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(tFFechaNacimiento, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblFechaNacimiento))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPuestoHabitual)
						.addComponent(rdbtnPortero)
						.addComponent(rdbtnDefensa)
						.addComponent(rdbtnMedio)
						.addComponent(rdbtnDelantero))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblSeleccion)
						.addComponent(comboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnAtras)
						.addComponent(btnGuardar))
					.addContainerGap())
		);
		contentPane.setLayout(gl_contentPane);

		rellenarCombo(comboBox);
		System.out.println("Datos del jugador: " + jugador);
		rellenarDatosJugador(jugador);

	}

	public void rellenarDatosJugador(String jugador) {

		Connection con = (new ConexionOracle(f)).Conectar();
		String sql = SqlTools.ConstruirLlamadaProcedimiento("PRUEBA", "obtener_jugador2", 3);
		OracleCallableStatement os = null;

		try {
			os = (OracleCallableStatement) con.prepareCall(sql);

			int pos = 0;

			// Cargamos los parametros de entrada IN
			os.setString(++pos, jugador);

			// Registramos los parametro de salida OUT. El metodo
			// registerIndexTableOutParameter es propio de la clase
			// OracleCallableStatement
			os.registerIndexTableOutParameter(++pos, 5, OracleTypes.VARCHAR, 100);
			os.registerOutParameter(++pos, java.sql.Types.BLOB);

			// Ejecutamos
			os.execute();

			// Obtenemos el puntero a vector de strings
			String[] resultArray = (String[]) os.getPlsqlIndexTable(2);

			// Obtenemos la variable BLOB y la pasamos a SerialBlob para
			// trabajar con ella en JAVA
			Blob fB = os.getBlob(3);

			// Comprobamos si tiene una foto el jugador
			// En caso afirmativo se muestra, sino se muestra un avatar
			if (fB != null) {
				fotoBLOB = new SerialBlob(fB.getBytes(1, (int) fB.length()));
				byte[] imgData = null;
				imgData = fotoBLOB.getBytes(1, (int) fotoBLOB.length());
				ImageIcon imageIcon = new ImageIcon(imgData);

				Image imageToResize = imageIcon.getImage();
				Image nuevaResized = imageToResize.getScaledInstance(lFoto.getWidth(), lFoto.getHeight(), java.awt.Image.SCALE_SMOOTH);
				imageIcon = new ImageIcon(nuevaResized);

				lFoto.setIcon(imageIcon);

				// JOptionPane.showMessageDialog(null, null, "Imagen del
				// Jugador", JOptionPane.INFORMATION_MESSAGE, imageIcon);
			} else {
				String path = "/images/avatar.png";

				URL url = this.getClass().getResource(path);
				ImageIcon icon = new ImageIcon(url);

				// redimensionamos la imagen
				Image image = icon.getImage().getScaledInstance(lFoto.getWidth(), lFoto.getHeight(), Image.SCALE_SMOOTH);
				icon = new ImageIcon(image, "Avatar");

				lFoto.setIcon(icon);

			}
			
			System.out.println("Dimension" + lFoto.getSize().toString());

			// Obtenmos los valores de nombre,direccion, posicion,fecha y
			// seleccion del array de Strings
			tFNombre.setText(resultArray[0]);

			if (resultArray[1] != null)
				tFDireccion.setText(resultArray[1]);

			switch (resultArray[2]) {
			case "AR":
				rdbtnPortero.setSelected(true);
				break;
			case "DF":
				rdbtnDefensa.setSelected(true);
				break;
			case "MC":
				rdbtnMedio.setSelected(true);
				break;
			case "DL":
				rdbtnDelantero.setSelected(true);
				break;
			}

			if (resultArray[3] != null)
				tFFechaNacimiento.setText(resultArray[3]);

			comboBox.setSelectedItem(resultArray[4]);

			// Mostramos por consola los datos para comprobar.
			System.out.println("Nombre: " + resultArray[0]);
			System.out.println("Direccion: " + resultArray[1]);
			System.out.println("Puesto habital: " + resultArray[2]);
			System.out.println("Fecha: " + resultArray[3]);
			System.out.println("Seleccion: " + resultArray[4]);
			System.out.println((fotoBLOB != null) ? "Tiene una Foto" : "Sin foto");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SqlTools.close(null, null,os, con);
		}
	}

	// Metodo para rellenar un Combo con las diferentes selecciones.
	public void rellenarCombo(JComboBox comboBox) {

		Connection con = null;
		Statement s = null;
		ResultSet rs = null;

		try {
			con = (new ConexionOracle(f)).Conectar();

			String sql = "SELECT EQUIPO FROM EQUIPOS";
			s = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);

			rs = s.executeQuery(sql);
			while (rs.next()) {
				comboBox.addItem(rs.getString("EQUIPO"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SqlTools.close(rs, s, null,con);
		}
	}

	// Setters y getters de los frames anteriores y posteriores de la
	// aplicacion.
	public JFrame getFrameAnterior() {
		return frameAnterior;
	}

	public void setFrameAnterior(JFrame frameAnterior) {
		this.frameAnterior = frameAnterior;
	}

}
