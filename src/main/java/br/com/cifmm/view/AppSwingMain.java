package br.com.cifmm.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.springframework.stereotype.Component;

import br.com.cifmm.control.FuncionarioControl;

import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.border.MatteBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import java.awt.Font;
import java.awt.Image;

import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import javax.swing.table.DefaultTableCellRenderer;


@Component
public class AppSwingMain extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JTable table;	
	private final FuncionarioControl funcionarioControl;
	
	/**
	 * Create the frame.
	 */
	public AppSwingMain(FuncionarioControl funcionarioControl) {
        this.funcionarioControl = funcionarioControl;
        initUI();
    }
	
	private static class ImageRenderer extends DefaultTableCellRenderer {
	    private static final long serialVersionUID = 1L;
	    
	    @Override
	    public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
	            boolean isSelected, boolean hasFocus, int row, int column) {
	        
	        java.awt.Component c = super.getTableCellRendererComponent(table, "", 
	            isSelected, hasFocus, row, column);
	        
	        if (c instanceof JLabel) {
	            JLabel label = (JLabel) c;
	            label.setHorizontalAlignment(JLabel.CENTER);
	            
	            if (value instanceof ImageIcon) {
	                ImageIcon icon = (ImageIcon) value;
	                // Redimensiona a imagem se necessário
	                Image img = icon.getImage().getScaledInstance(80, 100, Image.SCALE_SMOOTH);
	                label.setIcon(new ImageIcon(img));
	            } else {
	                label.setIcon(null);
	            }
	        }
	        
	        return c;
	    }
	}

	private void initUI() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1391, 796);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JPanel Header = new JPanel();
		Header.setBorder(new LineBorder(new Color(192, 192, 192)));
		FlowLayout flowLayout = (FlowLayout) Header.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		contentPane.add(Header, BorderLayout.NORTH);
		
		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setIcon(new ImageIcon("C:\\Users\\Relogio.ponto\\eclipse-workspace\\CIFMM2\\resources\\images\\logo.png"));
		Header.add(lblNewLabel);
		
		JPanel SideBar = new JPanel();
		SideBar.setBorder(new MatteBorder(0, 1, 0, 1, (Color) new Color(192, 192, 192)));
		FlowLayout flowLayout_1 = (FlowLayout) SideBar.getLayout();
		flowLayout_1.setHgap(25);
		contentPane.add(SideBar, BorderLayout.WEST);
		
		JButton btnNewButton = new JButton("Gerar Crachas");
		btnNewButton.setIcon(null);
		SideBar.add(btnNewButton);
		
		JPanel Main = new JPanel();
		contentPane.add(Main, BorderLayout.CENTER);
		
		textField = new JTextField();
		textField.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("Digite o RE:");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		
		JButton btnNewButton_1 = new JButton("Buscar");
		btnNewButton_1.addActionListener(e -> onBuscar());

		
		table = new JTable();
		table.setBorder(new LineBorder(new Color(192, 192, 192)));
		table.setModel(new DefaultTableModel(
		    new Object[][] {
		        {"Dado 1", loadImage("C:\\Users\\Relogio.ponto\\eclipse-workspace\\CIFMM2\\output\\cracha_frente_03093.png"), "Dado 3"},
		        {"Dado 4", loadImage("C:\\Users\\Relogio.ponto\\eclipse-workspace\\CIFMM2\\output\\cracha_verso_03093.png"), "Dado 6"},
		        {"Dado 7", loadImage("C:\\Users\\Relogio.ponto\\eclipse-workspace\\CIFMM2\\output\\cracha_frente_03093.png"), "Dado 9"},
		    },
		    new String[] {
		        "Coluna 1", "Fotos", "Coluna 3"
		    }
		));

		// Configuração do renderizador
		table.getColumnModel().getColumn(1).setCellRenderer(new ImageRenderer());
		table.setRowHeight(100); // Ajuste a altura conforme necessário
		
		GroupLayout gl_Main = new GroupLayout(Main);
		gl_Main.setHorizontalGroup(
			gl_Main.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_Main.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_Main.createParallelGroup(Alignment.LEADING)
						.addComponent(table, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 1192, Short.MAX_VALUE)
						.addComponent(btnNewButton_1, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 109, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_1)
						.addComponent(textField, GroupLayout.DEFAULT_SIZE, 1192, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_Main.setVerticalGroup(
			gl_Main.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_Main.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel_1)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(textField, GroupLayout.PREFERRED_SIZE, 41, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNewButton_1, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(table, GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE)
					.addContainerGap())
		);
		Main.setLayout(gl_Main);		
		
	}

	private void onBuscar() {
	    String re = textField.getText().trim();
	    if (re.isEmpty()) {
	        JOptionPane.showMessageDialog(this, "Digite o RE");
	        return;
	    }

	    try {
	        funcionarioControl.salvarFuncionario(re);
	        JOptionPane.showMessageDialog(this, "Processado.");
	    } catch (Exception e) {
	        JOptionPane.showMessageDialog(this, "Erro ao processar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
	        e.printStackTrace();  // Log para depuração
	    }
	}
	
	private ImageIcon loadImage(String path) {
	    try {
	        if (path != null && !path.isEmpty()) {
	            return new ImageIcon(path);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	
}
