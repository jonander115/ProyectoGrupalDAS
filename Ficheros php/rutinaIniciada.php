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
$accion = $_POST["accion"];
$usuario = $_POST["usuario"];
$idRutina = $_POST["idRutina"];
	

if ($accion == "insertarRutina"){
	
	#idRutina es el id de la rutina plantilla
	
	$nombreRutina = $_POST["nombreRutina"];
	$fechaHoraInicio = $_POST["fechaHoraInicio"];
	
	#Insertar la rutina iniciada partiendo de los datos de la "plantilla" de la rutina
	$resultado = mysqli_query($con, "INSERT INTO Rutinas(Nombre,Usuario,FechaHoraInicio) VALUES('$nombreRutina','$usuario','$fechaHoraInicio')");
	
	if (!$resultado) {
		echo 'Ha ocurrido algún error: ' . mysqli_error($con);
	}
	else{
		
		#Recoger el ID de la rutina iniciada que acabamos de insertar
		$resultado2 = mysqli_query($con, "SELECT IDRutina FROM Rutinas WHERE Usuario = '$usuario' AND Nombre = '$nombreRutina' AND FechaHoraInicio = '$fechaHoraInicio'");
		
		$fila = mysqli_fetch_row($resultado2);
		$idRutinaIniciada = $fila[0];
		
		#Recoger los ejercicios de la rutina plantilla
		$resultado3 = mysqli_query($con, "SELECT NombreEjercicio,Orden FROM Series WHERE Usuario = '$usuario' AND IDRutina = '$idRutina' ORDER BY Orden ASC");
		
		if (!$resultado3) {
			echo 'Ha ocurrido algún error: ' . mysqli_error($con);
		}
		

		#Recorrer el resultado
		if (mysqli_num_rows($resultado3) != 0){
			while ($fila = mysqli_fetch_row($resultado3)){

				#Insertar el ejercicio de la rutina plantilla en la rutina iniciada
				$resultado4 = mysqli_query($con, "INSERT INTO Series(IDRutina,Usuario,NombreEjercicio,Orden) VALUES('$idRutinaIniciada','$usuario','$fila[0]','$fila[1]')");
				
				if (!$resultado4) {
					echo 'Ha ocurrido algún error: ' . mysqli_error($con);
				}
			}
		}

		
		echo $idRutinaIniciada;
	
	}
	
}
else if ($accion == "insertarSerie"){
	
	#idRutina es el id de la rutina iniciada
	
	$ejercicio = $_POST["ejercicio"];
	$peso = $_POST["peso"];
	$repeticiones = $_POST["repeticiones"];
	$notas = $_POST["notas"];
	
	$resultado = mysqli_query($con, "SELECT MAX(Orden) FROM Series WHERE IDRutina = '$idRutina' AND Usuario = '$usuario' AND NombreEjercicio = '$ejercicio'");
	$fila = mysqli_fetch_row($resultado);
	$count = $fila[0] + 1;
	
	#Insertar en la tabla de las series la serie especificada por el usuario
	$resultado2 = mysqli_query($con, "INSERT INTO Series(IDRutina,Usuario,NombreEjercicio,Orden,NumRepeticiones,Peso,Notas) VALUES('$idRutina','$usuario','$ejercicio','$count','$repeticiones','$peso','$notas')");
				
	if (!$resultado2) {
		echo 'Ha ocurrido algún error: ' . mysqli_error($con);
	}
	else{
		echo "correcto";
	}

	
}
else if ($accion == "finalizarEntrenamiento"){
	
	#idRutina es el id de la rutina iniciada a terminar
	
	$fechaHoraFin = $_POST["fechaHoraFin"];
	
	$resultado = mysqli_query($con, "UPDATE Rutinas SET FechaHoraFinal = '$fechaHoraFin' WHERE IDRutina = '$idRutina'");
				
	if (!$resultado) {
		echo 'Ha ocurrido algún error: ' . mysqli_error($con);
	}
	else{
		echo "finalizado";
	}
	
}


#Cerramos la conexión con la base de datos
mysqli_close($con);


?>