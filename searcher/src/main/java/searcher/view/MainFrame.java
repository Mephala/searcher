package searcher.view;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import searcher.exception.NotAFolderException;
import searcher.exception.SearcherTimeoutExceededException;
import searcher.parser.FileNameParser;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	private final JTextField textField;
	private final List<FileNameParser> parsers = new ArrayList<>();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * 
	 * @throws NotAFolderException
	 * @throws SearcherTimeoutExceededException
	 */
	public MainFrame() throws SearcherTimeoutExceededException {
		File[] roots = File.listRoots();
		for (File file : roots) {
			FileNameParser fnp;
			try {
				fnp = new FileNameParser(file);
			} catch (NotAFolderException e1) {
				continue;
			}
			fnp.parseFileNames();
			parsers.add(fnp);
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		textField = new JTextField();
		textField.setBounds(33, 88, 188, 22);
		contentPane.add(textField);
		textField.setColumns(10);

		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String searchText = textField.getText();
				List<File> searchResults = new ArrayList<>();
				for (FileNameParser fnp : parsers) {
					searchResults.addAll(fnp.getFileByWildCard(searchText));
				}
				JOptionPane.showMessageDialog(null, searchResults.toString(), "Murnum " + searchResults.size() + " results...", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		btnSearch.setBounds(257, 87, 99, 25);
		contentPane.add(btnSearch);
	}
}
