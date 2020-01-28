package Ejercicio5.codigo;

/*
Jose Saldaña Mercado 76424266-G
Manuel Carmona Pérez 17482989-E
*/
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClienteControlHorario {

	public static void main(String[] args) {

		String datosEnvio="";

		String host="localhost";
		int port=8989;

		Socket socketServicio=null;

		try {

			socketServicio = new Socket(host, port);
			System.out.println("Cliente creado");

			PrintWriter outPrinter = new PrintWriter(socketServicio.getOutputStream(), true);
			BufferedReader inReader = new BufferedReader(new InputStreamReader(socketServicio.getInputStream()));

			String accion="";
			Scanner capt = new Scanner(System.in);

			do {
				System.out.print("Introduzca acción a realizar:\t");
				accion = capt.nextLine();
				accion = accion.toUpperCase();

				String dni="",pass="",dep="",pro="";

				switch (accion){
					case "LOGIN":
						System.out.print("Introduzca login:\t");
						dni = capt.nextLine();
						System.out.print("Introduzca password:\t");
						pass = capt.nextLine();
						datosEnvio="100+"+dni+"+"+pass;
						break;
					case "LOGIN_ADM":
						System.out.print("Introduzca login:\t");
						dni = capt.nextLine();
						System.out.print("Introduzca password:\t");
						pass = capt.nextLine();
						datosEnvio="101+"+dni+"+"+pass;
						break;
					case "LOGOUT":
						datosEnvio="102+";
						break;
					case "CONSULT_TIME_WORKED":
						datosEnvio="103+";
						break;
					case "SEL_DEP":
						System.out.print("Introduzca departamento:\t");
						dep = capt.nextLine();
						datosEnvio="108+"+dep;
						break;
					case "SEL_PRO":
						System.out.print("Introduzca proyecto:\t");
						pro = capt.nextLine();
						datosEnvio="109+"+pro;
						break;
					case "START_WORKING":
						datosEnvio="110+";
						break;
					case "CONSULT_TIME_WORKED_TODAY":
						datosEnvio="111+";
						break;
					case "STOP_WORKING":
						datosEnvio="114+";
						break;
					default:
						datosEnvio="406+"; //error de operacion
						break;
				}

				outPrinter.println(datosEnvio);
				System.out.println("Cliente envía datos");
				outPrinter.flush();
				String respuesta=inReader.readLine();


				String codigo_estadoServidor="";
				String descripcionRespuesta="";
				int i;
				for(i=0; i<respuesta.indexOf('+'); ++i){
					codigo_estadoServidor+=respuesta.charAt(i);
				}//i quedara en el índice que apunta al caracter +

				int indice=i+1;// INDICE DE DESPUES DEL CODIGO
											 // +1 para evitar caracter "+"
				descripcionRespuesta=respuesta.substring(indice);
				System.out.println("Recibido: Código respuesta: "+codigo_estadoServidor+"\tRespuesta: "+descripcionRespuesta);

			} while (!accion.equals("LOGOUT"));

			capt.close();
			socketServicio.close();
			System.out.println("Cliente finalizado");

		} catch (UnknownHostException e) {
			System.err.println("Error: Nombre de host no encontrado.");
		} catch (IOException e) {
			System.err.println("Error de entrada/salida al abrir el socket.");
		}
	}
}
