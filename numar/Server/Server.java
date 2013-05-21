import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.BorderLayout;

class ClientServer extends Thread {
      public static int contorClient = 0;
      private Socket clientSocket = null;

      public static int Rrandom (){	    
         return (int)Math.floor(Math.random()*10);
    }
      public void run() {
            //informatii despre client
            InetAddress adresaClient=clientSocket.getInetAddress();
            MyFrame.getInstance().addText("Client nou la adresa: "+adresaClient);
           
            //System.out.println("Nume client: <Client " + nrClient + ">");

            int ei = Rrandom ();

            //se executa dialogul cu clientul
            BufferedReader in = null;
            PrintWriter out = null;
                try {
                    //fluxul de intrare de la client
		    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		    //System.out.println(in);
				
		    //fluxul de iesire catre client
		    out = new PrintWriter(clientSocket.getOutputStream(),true);
			String mesaj;
			out.println("STart joc. Scrieti numarul");
		    while((mesaj = in.readLine())!= null){
                        int nrclient = Integer.parseInt(mesaj);

                        //raspuns="<Client " + nrClient + "> : " + mesaj;
                        if (nrclient==ei) {
                        out.println("Adevarat! Doresti sa joci din nou? (Y/N)");
                      		if(in.readLine().equalsIgnoreCase("Y")){//continua sa joace
                        	ei=Rrandom ();
                        	out.println("Numarul ales trebuie sa fie intre 1 si 10");	
                        	}else break;
                        } 
                        else if (nrclient>=ei) 
                        out.println("Nu ai ghicit! Incearca un numar mai mic! "); 
                        else if(nrclient <= ei) 
                        out.println("Nu ai ghicit! Incearca un numar mai mare!");
                        }
                    }
		catch(EOFException e){}
	  	 catch (IOException e) {}
	  	 finally {
	  	 	MyFrame.getInstance().addText(adresaClient+" deconecatat");
	  	 	MyFrame.getInstance().setNrOfPlayers(--contorClient);
		     try {
		  	if (in != null) in.close();
		  	if (out != null) out.close();
		  	if (clientSocket != null) clientSocket.close();
                        }
                      catch (IOException e) {System.err.println(e);}
	  	 }
	    }

public ClientServer(Socket s){
     clientSocket = s;
     MyFrame.getInstance().setNrOfPlayers(++contorClient);
     }
}

class Server{

public static int Rrandom (int a){
        Random r = new Random();
	    int ei = r.nextInt(a);
         return ei;
    }

public static void main(String[] args) throws IOException {
    int PORT = 4567;
    ServerSocket serverSocket = new ServerSocket(PORT);
        MyFrame.getInstance();
        while(true){
       Socket sochet = serverSocket.accept();
       ClientServer ss=new ClientServer(sochet);
       ss.start();
       }
}
}
class MyFrame extends JFrame {
	private JTextArea text;
	private JLabel clientiinjoc;
	private static MyFrame instance;
	
	private MyFrame(){
		super("Server");
		this.text=new JTextArea(30,50);
		text.setEditable(false);
		this.text.append("Server start\n");
		this.getContentPane().add(new JScrollPane(text));
		this.clientiinjoc=new JLabel("Clienti in joc:0");
		this.getContentPane().add(clientiinjoc, BorderLayout.NORTH);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();	
	}
	public static MyFrame getInstance(){
		if(instance==null) instance=new MyFrame();
		return instance;	
	}
	public static void addText(String text){
		instance.text.append(text+"\n");
	}
	public static void setNrOfPlayers(int nr){
		instance.clientiinjoc.setText("Clienti in joc:"+nr);
	}
}