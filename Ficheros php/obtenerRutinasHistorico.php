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

if ($accion == "obtenerFechasDelHistorico"){
	
	$resultado = mysqli_query($con, "SELECT FechaHoraInicio FROM Rutinas WHERE Usuario = '$usuario' AND FechaHoraInicio IS NOT NULL ORDER BY FechaHoraInicio DESC");
	
	if (!$resultado) {
		echo 'Ha ocurrido algún error: ' . mysqli_error($con);
	}
	
	$arrayresultados = array();
	$cont = 0;
	
	#Recorrer el resultado
	if (mysqli_num_rows($resultado) != 0){
		while ($fila = mysqli_fetch_assoc($resultado)){
			$fecha = $fila['FechaHoraInicio'];
			$mesAnio = date('m-Y', strtotime($fecha));
			
			# Verificar si el par mes-año ya existe en el array
			$existe = false;
			for ($i = 0; $i < $cont; $i++) {
				if ($arrayresultados[$i]['MesAnio'] == $mesAnio) {
					$existe = true;
					break;
				}
			}
			
			# Agregar la fecha al array solo si el par mes-año no existe
			if (!$existe) {
				$arrayresultados[$cont] = array('Fecha' => $fecha, 'MesAnio' => $mesAnio);
				$cont++;
			}
	
		}
	}

	#Devolver el resultado
	echo json_encode($arrayresultados);
	
}
else if ($accion == "obtenerRutinasDelMes"){
	
	$numMes = $_POST["numMes"];
	$numAnio = $_POST["numAnio"];
	
	$fechaInicio = $numAnio . '-' . $numMes . '-01'; // Formatear la fecha de inicio
	$fechaFin = date('Y-m-t', strtotime($fechaInicio)); // Obtener el último día del mes

	$resultado = mysqli_query($con, "SELECT IDRutina, Nombre, FechaHoraInicio FROM Rutinas WHERE Usuario = '$usuario' AND FechaHoraInicio >= '$fechaInicio' AND FechaHoraInicio <= '$fechaFin' ORDER BY FechaHoraInicio DESC");
	
	if (!$resultado) {
		echo 'Ha ocurrido algún error: ' . mysqli_error($con);
	}
	
	$arrayresultados = array();
	
	#Recorrer el resultado
	if (mysqli_num_rows($resultado) != 0){
		$cont = 0;
		while ($fila = mysqli_fetch_row($resultado)){
			$fechaHoraInicio = $fila[2];
			$dia = date('d', strtotime($fechaHoraInicio));

			$arrayresultados[$cont] = array(
				'IDRutina' => $fila[0],
				'Nombre' => $fila[1],
				'Dia' => $dia
			);
			$cont++;
		}
	}

	#Devolver el resultado
	echo json_encode($arrayresultados);
	
}
else if ($accion == "obtenerFechasDeRutina"){
	
	$idRutina = $_POST["idRutina"];
	
	$resultado = mysqli_query($con, "SELECT Nombre,FechaHoraInicio,FechaHoraFinal FROM Rutinas WHERE Usuario = '$usuario' AND IDRutina = '$idRutina'");
	
	if (!$resultado) {
		echo 'Ha ocurrido algún error: ' . mysqli_error($con);
	}
	
	#Accedemos al resultado
	$fila = mysqli_fetch_row($resultado);
	
	#Generar el array con los resultados con la forma Atributo - Valor
	$arrayresultados = array(
		'Nombre' => $fila[0],
		'FechaHoraInicio' => $fila[1],
		'FechaHoraFinal' => $fila[2]
	);

	#Devolver el resultado
	echo json_encode($arrayresultados);
	
}
else if ($accion == "obtenerEjerciciosDeRutina"){
	
	$idRutina = $_POST["idRutina"];
	
	$resultado = mysqli_query($con, "SELECT NombreEjercicio FROM Series WHERE Usuario = '$usuario' AND IDRutina = '$idRutina' ORDER BY Orden ASC");
	
	if (!$resultado) {
		echo 'Ha ocurrido algún error: ' . mysqli_error($con);
	}
	
	$arrayresultados = array();
	$cont = 0;
	
	#Recorrer el resultado
	if (mysqli_num_rows($resultado) != 0){
		while ($fila = mysqli_fetch_assoc($resultado)){
			
			# Verificar si el nombre del ejercicio ya existe en el array
			$existe = false;
			for ($i = 0; $i < $cont; $i++) {
				if ($arrayresultados[$i]['NombreEjercicio'] == $fila['NombreEjercicio']) {
					$existe = true;
					break;
				}
			}
			
			# Agregar el nombre al array solo si no existe
			if (!$existe) {
				$arrayresultados[$cont] = array('NombreEjercicio' => $fila['NombreEjercicio']);
				$cont++;
			}
			
		}
	}
	

	#Devolver el resultado
	echo json_encode($arrayresultados);
	
}
else if ($accion == "obtenerSeriesDeEjercicio"){
	
	$idRutina = $_POST["idRutina"];
	$nombreEjercicio = $_POST["nombreEjercicio"];
	
	$resultado = mysqli_query($con, "SELECT Peso, NumRepeticiones, Notas FROM Series WHERE Usuario = '$usuario' AND IDRutina = '$idRutina' AND NombreEjercicio = '$nombreEjercicio' ORDER BY Orden");
	
	if (!$resultado) {
		echo 'Ha ocurrido algún error: ' . mysqli_error($con);
	}
	
	$arrayresultados = array();
	
	#Recorrer el resultado
	if (mysqli_num_rows($resultado) != 0){
		$cont = 0;
		while ($fila = mysqli_fetch_row($resultado)){
			$arrayresultados[$cont] = array(
			'Peso' => $fila[0],
			'NumRepeticiones' => $fila[1],
			'Notas' => $fila[2]
			);
			$cont++;
		}
	}

	#Devolver el resultado
	echo json_encode($arrayresultados);
	
}


#Cerramos la conexión con la base de datos
mysqli_close($con);


?>