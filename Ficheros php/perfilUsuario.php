<?php

$DB_SERVER="localhost"; #la dirección del servidor
$DB_USER="Xjwojciechowska0"; #el usuario para esa base de datos
$DB_PASS="aQWGmrrWt"; #la clave para ese usuario
$DB_DATABASE="Xjwojciechowska0_entrega3"; #la base de datos a la que hay que conectarse

#Se establece la conexión:
$con = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);

#Comprobamos la conexión:
if (mysqli_connect_errno()) {
	echo 'Error de conexion: ' . mysqli_connect_error();
	exit();
}

#Parámetros a utilizar
$usuario = $_POST["usuario"];
$accion = $_POST["accion"];

if ($accion == "select"){ #Hay que pedir los datos del usuario

	$resultado = mysqli_query($con, "SELECT email, foto FROM Usuarios WHERE usuario = '$usuario'");
	
	#Comprobar si se ha ejecutado correctamente
	if (!$resultado) {
		echo 'Ha ocurrido algún error: ' . mysqli_error($con);
	}
	
	
	#Si se ha devuelto algo, la consulta es correcta
	if (mysqli_num_rows($resultado) != 0){
		
		#Acceder al resultado
		$fila = mysqli_fetch_row($resultado);
		
		# Generar el array con los resultados con la forma Atributo - Valor
		$arrayresultados = array(
		'email' => $fila[0],
		'foto' => $fila[1]
		);
		
		#Devolver el resultado
		echo json_encode($arrayresultados);
	
	}
}

else if ($accion == "update"){ #Hay que modificar los datos del usuario

	#Resto de parámetros necesarios que ha recibido el php
	$email = $_POST["email"];
	$foto = $_POST["foto"];
	
	$resultado = mysqli_query($con, "UPDATE Usuarios SET email='$email', foto='$foto' WHERE usuario = '$usuario'");

	#Comprobar si se ha ejecutado correctamente
	if ($resultado == true) {
		$devolver = 'usuarioModificado';
		echo $devolver;
	}
	
}
else if ($accion == "listarEjerciciosUsuario"){ #Listar los ejercicios creados por el usuario
	
	$resultado = mysqli_query($con, "SELECT Nombre FROM Ejercicios WHERE Creador = '$usuario' AND IsDefault = '0'");
	
	#Comprobamos si se ha ejecutado correctamente
	if (!$resultado) {
		echo 'Ha ocurrido algún error: ' . mysqli_error($con);
	}

	$arrayresultados = array();

	#Recorrer el resultado
	if (mysqli_num_rows($resultado) != 0){
		$cont = 0;
		while ($fila = mysqli_fetch_row($resultado)){
			$arrayresultados[$cont] = array(
			'NombreEjercicio' => $fila[0]
			);
			$cont++;
		}
	}
	
	#Devolver el resultado
	echo json_encode($arrayresultados);
}

else if ($accion == "eliminarEjercicio"){ #Eliminar un ejercicio creado por el usuario
	
	#Se invoca este servicio web por cada ejercicio que se elimina, ya que el usuario
	# elige los ejercicios (creados por él) que desea eliminar mediante un diálogo de selección múltiple
	
	#Se recibe el nombre del ejercicio a eliminar
	$nombreEjercicio = $_POST["nombreEjercicio"];	
	
	#Primero se borran las series del ejercicio
	$resultado = mysqli_query($con, "DELETE FROM Series WHERE NombreEjercicio = '$nombreEjercicio' AND Usuario = '$usuario'");
	
	if ($resultado) {
		#Borrar el ejercicio de la tabla "Ejercicios"
		$resultado = mysqli_query($con, "DELETE FROM Ejercicios WHERE Nombre = '$nombreEjercicio' AND Creador = '$usuario' AND IsDefault = '0'");
		
		#Comprobar si se ha ejecutado correctamente
		if ($resultado) {
			$devolver = 'ejercicioEliminado';
		}
		else{
			$devolver = 'error';
		}
		
	}
	else{
		$devolver = 'error';
	}
	
	echo $devolver;
}

#Cerramos la conexión con la base de datos
mysqli_close($con);


?>