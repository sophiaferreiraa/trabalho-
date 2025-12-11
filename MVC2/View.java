import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class View extends JFrame {

    private JTextField txtBusca;
    private JButton btnBuscar;
    private JTable tabela;
    private DefaultTableModel tableModel;

    public View() {
        setTitle("Sistema de Estudantes");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelBusca = new JPanel(new FlowLayout());
        txtBusca = new JTextField(20);
        btnBuscar = new JButton("Buscar");
        
        panelBusca.add(new JLabel("Nome:"));
        panelBusca.add(txtBusca);
        panelBusca.add(btnBuscar);
        
        add(panelBusca, BorderLayout.NORTH);

        String[] colunas = {"Matrícula", "Nome", "Código Curso"};
        tableModel = new DefaultTableModel(colunas, 0);
        tabela = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(tabela);
        
        add(scrollPane, BorderLayout.CENTER);
        
        setVisible(true);
    }

    public String getTermoBusca() {
        return txtBusca.getText();
    }

    public void addBuscarListener(ActionListener listener) {
        btnBuscar.addActionListener(listener);
    }

    public void atualizarTabela(ArrayList<Estudante> estudantes) {
        tableModel.setRowCount(0);
        for (Estudante e : estudantes) {
            Object[] linha = {e.getMatricula(), e.getNome(), e.getCodigoCurso()};
            tableModel.addRow(linha);
        }
    }
}
