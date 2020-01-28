package Ejercicio5.codigo;

/*
Jose Saldaña Mercado 76424266-G
Manuel Carmona Pérez 17482989-E
*/
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServidorControlHorario {

	public static void main(String[] args) {

		// Puerto de escucha
		int port=8989;
		ServerSocket socketServidor;

		try {
			// Abrimos el socket en modo pasivo, escuchando el en puerto indicado por "port"
			socketServidor = new ServerSocket(port);
			System.out.println("Servidor creado");

			do {
				// Aceptamos una nueva conexión con accept()
				Socket socketServicio = socketServidor.accept();
				System.out.println("Servidor recibe petición de cliente desde el puerto "+port);

				ProcesadorServidor procesador=new ProcesadorServidor(socketServicio);
				procesador.procesa();

			} while (true);
		} catch (IOException e) {
			System.err.println("Error al escuchar en el puerto "+port);
		}
	}
}
