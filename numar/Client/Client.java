import java.net.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Client {

    public static void main(String[] arg) {
        String host = "localhost";
        if (arg.length != 0) {
            host = arg[0];
        }
        new MyFrame(host);
    }
}

class MyFrame extends JFrame implements Runnable {

    private JLabel left;
    private String host;
    private JLabel right;
    private JLabel answer;
    private JTextArea jocuri;
    private JTextField text;
    private JButton send;
    private boolean ok;
    private PrintWriter out;
    private int nr;
    private int nrincercari = 0;
    private int nrJocuri = 0;

    public MyFrame(String host) {
        super("Client");
        this.host = host;
        this.left = new JLabel("");
        this.right = new JLabel("");
        this.setLeft(1);
        this.setRight(10);
        this.jocuri = new JTextArea(20, 30);
        this.jocuri.setEditable(false);
        this.answer = new JLabel("");
        this.getContentPane().add(answer, BorderLayout.NORTH);
        this.getContentPane().add(new JScrollPane(jocuri), BorderLayout.SOUTH);
        this.send = new JButton("Trimite");
        this.send.addActionListener(new TrimiteAscultator());
        this.text = new JTextField();
        this.text.addActionListener(new TrimiteAscultator());
        JPanel p = new JPanel(new GridLayout(2, 2, 10, 10));
        p.add(text); p.add(send);
        p.add(left); p.add(right);
        JPanel pp = new JPanel(new FlowLayout());
        pp.add(p);
        this.getContentPane().add(pp);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(350, 500);
        this.run();
    }

    public void setAnswer(String text) {
        answer.setText(text);
    }

    class TrimiteAscultator implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (ok) {
                try {
                    nr = Integer.parseInt(text.getText());
                    out.println(nr);
                    nrincercari++;
                    text.setText("");
                } catch (NumberFormatException err) {
                    setAnswer("Introduceti numar");
                    text.setText("");
                }
            } else {
                out.println(text.getText());
                text.setText("");
            }
        }
    }

    public void setLeft(int nrl) {
        left.setText("> " + nrl);
    }

    public void setRight(int nrl) {
        right.setText("< " + nrl);
    }

    public void addGame(String text) {
        jocuri.append(text + "\n");
    }

    public void run() {

        BufferedReader in = null;
        Socket sock = null;
        try {
            sock = new Socket(host, 4567);
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new PrintWriter(sock.getOutputStream(), true);
            String raspuns;
            ok = true;
            do {
                raspuns = in.readLine();
                setAnswer(raspuns);
                ok = raspuns.startsWith("Adevarat!") ? false : true;
                if (!ok) {
                    this.nrJocuri++;
                    this.addGame(this.nrJocuri + ")\t incercari " + this.nrincercari);
                    this.nrincercari=0;
                    this.setLeft(1);
                    this.setRight(10);
                }
                if (raspuns.indexOf("mai mare") > 0) {
                    this.setLeft(nr);
                } else if (raspuns.indexOf("mai mic") > 0){
                    this.setRight(nr);
                }
            } while (true);
        } catch (UnknownHostException e) {
            System.err.println(e);
        } catch (EOFException e) {
            System.err.println(e);
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            try {
                if (sock != null) {
                    sock.close();
                }
            } catch (IOException e) {
                System.err.println(e);
            }
            System.exit(0);
        }
    }
}
