package Ejercicio5.codigo;

/*
Jose Saldaña Mercado 76424266-G
Manuel Carmona Pérez 17482989-E

Acabar memoria

*/
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ProcesadorServidor {
	// Referencia a un socket para enviar/recibir las peticiones/respuestas
	private Socket socketServicio;

	// Para simular la memoria (no es objeto de este ejercicio conectar con una DB)
	public HashMap<String, String> usuarios;
	public HashMap<String, ArrayList<Date> > horasTrabajadas;
	public ArrayList<String> departamentos;
	public ArrayList<String> proyectos;

	// Para guardar el estado en el que se encuentra el cliente
	public String proyecto;
	public String departamento;
	public boolean autenticado;
	public String usuario;
	public boolean trabajando;

	// Constructor que tiene como parámetro una referencia al socket abierto en por otra clase
	public ProcesadorServidor(Socket socketServicio) {
		this.socketServicio=socketServicio;

		// Simulamos datos existentes (los cargaría de una DB)
		// Cargamos usuarios (sin admins para simplificar)
		this.usuarios = new HashMap<String, String>();
		this.usuarios.put("76424266","123456");
		this.usuarios.put("17482989","123456");
		// Cargamos horas de trabajo usuarios (Sin asociar a departamentos o proyectos para simplificar)
		this.horasTrabajadas = new HashMap<String, ArrayList<Date> >();
		Date auxiliar1 = new Date(1573459200); // 11/11/2019 08:00
		Date auxiliar2 = new Date(1573480800); // 11/11/2019 14:00
		Date auxiliar3 = new Date(1573545600); // 12/11/2019 08:00
		Date auxiliar4 = new Date(1573567200); // 12/11/2019 14:00
		ArrayList<Date> vectorAuxiliar1 = new ArrayList<>();
		vectorAuxiliar1.add(auxiliar1);
		vectorAuxiliar1.add(auxiliar2);
		vectorAuxiliar1.add(auxiliar3);
		vectorAuxiliar1.add(auxiliar4);
		this.horasTrabajadas.put("76424266",vectorAuxiliar1);
		this.horasTrabajadas.put("17482989",vectorAuxiliar1);
		// Cargamos departamentos
		this.departamentos = new ArrayList<>();
		this.departamentos.add("Equipo Jose");
		this.departamentos.add("Equipo Manuel");
		// Cargamos proyectos
		this.proyectos = new ArrayList<>();
		this.proyectos.add("Atención al cliente");
		this.proyectos.add("Desarrollo");
		this.proyectos.add("Diseño");
		this.proyectos.add("Soporte");
		this.proyectos.add("Administración");

		// Definimos estado inicial del sistema
		this.proyecto = null;
		this.departamento = null;
		this.autenticado = false;
		this.usuario = null;
		this.trabajando = false;
	}

	// Aquí es donde se realiza el procesamiento realmente:
	void procesa(){
		System.out.println("Thread procesador de servido creado");
		// Como máximo leeremos un bloque de 1024 bytes. Esto se puede modificar.
		String datosRecibidos="";

		try {
			// Obtiene los flujos de escritura/lectura
			PrintWriter outPrinter = new PrintWriter(socketServicio.getOutputStream(), true);
			BufferedReader inReader = new BufferedReader(new InputStreamReader(socketServicio.getInputStream()));

			// Recibe los datos del cliente
			while(!datosRecibidos.equals("102+")){
				datosRecibidos = inReader.readLine();

				Date auxiliar;
				String dep_o_pro="";
				String respuesta="";
				String codigo_datosRecibidos="";
				int i;
				for(i=0; i<datosRecibidos.indexOf('+'); ++i){
					codigo_datosRecibidos+=datosRecibidos.charAt(i);
				}//i quedara en el índice que apunta al caracter +

				int indice=i+1;// INDICE DE DESPUES DEL CODIGO
											 // +1 para evitar caracter "+"

				switch (codigo_datosRecibidos){

					case "100":
						if(usuario!=null){
							respuesta="401+ERROR, YA HABÍA UN USUARIO LOGUEADO";
						}else {
							String dni=datosRecibidos.substring(indice,indice+8);//tam dni=9
							indice=indice+9; // evitar caracter "+"
							String pass=datosRecibidos.substring(indice);//hasta eof
							if (usuarios.containsKey(dni) && usuarios.get(dni).equals(pass)) {
								usuario=dni;
								autenticado=true;
								respuesta="200+Logueado correctamente, bienvenido "+dni;
							} else {
								respuesta="400+ERROR DE LOGIN, USUARIO O CONTRASEÑA INCORRECTOS";
							}
						}
						break;

					//case "101":
						//NO SE CONSIDERARAN LOS ADMINS PARA LA IMPLEMENTACIÓN
						//break;

					case "102":
						//NO ES NECESARIO REINICIAR EL ESTADO YA QUE NO ESTAMOS EN UNA DB
						respuesta="202+SE CERRARÁ LA CONEXIÓN, HASTA MAÑANA";
						break;

					case "103":
						if(autenticado==true){
							long seconds = 0;
							int nFechas = horasTrabajadas.get(usuario).size();
							if(trabajando){
								for (int u = 0; u < nFechas - 1; u+=2){
									seconds += horasTrabajadas.get(usuario).get(u+1).getTime() - horasTrabajadas.get(usuario).get(u).getTime();
								}
								seconds += (new Date().getTime() / 1000) - horasTrabajadas.get(usuario).get(nFechas - 1).getTime();
							} else {
								for (int u = 0; u < nFechas; u+=2){
									seconds += horasTrabajadas.get(usuario).get(u+1).getTime() - horasTrabajadas.get(usuario).get(u).getTime();
								}
							}
							long minutes = seconds / 60;
							long hours = minutes / 60;
							minutes = minutes % 60;
							respuesta = "203+LLeva trabajado desde que hay registros: "+hours+" h y "+minutes+" min";
						}else respuesta="411+NO SE HA LOGUEADO";
						break;
					case "108":
						dep_o_pro=datosRecibidos.substring(indice);
						if(autenticado==true && departamento==null
							&& departamentos.contains(dep_o_pro)){
							departamento=dep_o_pro;
							respuesta="208+AHORA PERTENECES AL DEP "+dep_o_pro;
						}else if(!autenticado){
							respuesta="411+ERROR, NO SE HA LOGUEADO";
						}else if (departamento!=null){
							respuesta="410+ERROR, YA SE PERTENECE A UN DEPARTAMENTO";
						}
						break;
					case "109":
						dep_o_pro=datosRecibidos.substring(indice);
						if(autenticado==true && departamento!=null && proyecto==null
							&& proyectos.contains(dep_o_pro)){
							proyecto=dep_o_pro;
							respuesta="209+AHORA PERTENECES AL PRO "+dep_o_pro;
						}else if(!autenticado){
							respuesta="411+ERROR, NO SE HA LOGUEADO";
						}else if (departamento==null){
							respuesta="410+ERROR, NO SE PERTENECE A UN DEPARTAMENTO";
						}else if (proyecto!=null){
							respuesta="409+ERROR, YA SE PERTENECE A UN PROYECTO";
						}
						break;
					case "110":
						if(autenticado==true && departamento!=null && proyecto!=null
							&& !trabajando){
							trabajando=true;
							long timestampAux = new Date().getTime() / 1000;
							auxiliar = new Date(timestampAux);
							horasTrabajadas.get(usuario).add(auxiliar);
							respuesta="210+EMPIEZA EL CONTADOR DE TRABAJO";
						}else if(!autenticado){
							respuesta="411+ERROR, NO SE HA LOGUEADO";
						}else if (departamento==null){
							respuesta="410+ERROR, NO SE PERTENECE A UN DEPARTAMENTO";
						}else if (proyecto==null){
							respuesta="409+ERROR, NO SE PERTENECE A UN PROYECTO";
						}else if (trabajando){
							respuesta="405+ERROR, YA SE ESTABA TRABAJANDO";
						}
						break;
					case "111":
						if(autenticado==true && trabajando== true){
							long seconds = (new Date().getTime() / 1000) - horasTrabajadas.get(usuario).get(horasTrabajadas.get(usuario).size()-1).getTime();
							long minutes = seconds / 60;
							long hours = minutes / 60;
							minutes = minutes % 60;
							respuesta = "211+LLeva trabajado desde que empezo: "+hours+" h y "+minutes+" min";
						}else if(!autenticado){
							respuesta="411+ERROR, NO SE HA LOGUEADO";
						}else if (!trabajando){
							respuesta="405+ERROR, NO SE ESTABA TRABAJANDO";
						}
						break;
					case "114":
						if(autenticado==true && trabajando){
							trabajando=false;
							long timestampAux = new Date().getTime() / 1000;
							auxiliar = new Date(timestampAux);
							horasTrabajadas.get(usuario).add(auxiliar);
							respuesta="214+HAS DEJADO DE TRABAJAR, CONTADOR PARADO";
						}else if(!autenticado){
							respuesta="411+ERROR, NO SE HA LOGUEADO";
						}else if (!trabajando){
							respuesta="405+ERROR, NO SE ESTABA TRABAJANDO";
						}
						break;
					case "406":
						respuesta="406+ERROR, OPERACIÓN DESCONOCIDA";
						break;
				}
				System.out.println("Servidor atiende petición "+codigo_datosRecibidos);
				outPrinter.println(respuesta);
				outPrinter.flush();
			}

		} catch (IOException e) {
			System.err.println("Error al obtener los flujso de entrada/salida.");
		}

	}

}
