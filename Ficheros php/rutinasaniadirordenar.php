<?php
$DB_SERVER = "localhost";
$DB_USER = "Xjwojciechowska0";
$DB_PASS = "aQWGmrrWt";
$DB_DATABASE = "Xjwojciechowska0_entrega3";

$conn = new mysqli($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$usuario = $_POST["usuario"];
$opcion = $_POST["opcion"];

if ($opcion == "ancategoriayej"){

	$categoria = $_POST["categoria"];
	$nombre = $_POST["ejercicio"];
	$rutina = $_POST["idrutina"];
	
	
	$sql="SELECT Nombre FROM Ejercicios WHERE Nombre='$nombre'";
	$respuesta = $conn->query($sql);
	
	if (mysqli_num_rows($respuesta) == 0){ #Si no existe el ejercicio, se puede crear
		$sql = "INSERT INTO Ejercicios(Nombre, IsDefault, Categoria, Creador) VALUES('$nombre', '0', '$categoria', '$usuario')";
		$resultado = $conn->query($sql);
		
		$sql="SELECT NombreEjercicio FROM Series WHERE NombreEjercicio = '$nombre' AND IDRutina='$id'"; #Para saber si el ejercicio ya estaba en la rutina
		$respuesta = $conn->query($sql);
	
		if (mysqli_num_rows($respuesta) == 0){ #Si el ejercicio no estaba ya añadido en la rutina, se puede añadir
	
			$sql="SELECT MAX(Orden)+1 FROM Series WHERE IDRutina='$rutina' AND Usuario='$usuario' AND NumRepeticiones IS NULL AND Peso IS NULL";
			$respuesta = $conn->query($sql);
			
			$row = mysqli_fetch_row($respuesta);

			if (is_null($row[0])){
				$sql = "INSERT INTO Series(IDRutina, Usuario, NombreEjercicio, Orden, NumRepeticiones,Peso, Notas) VALUES ('$rutina','$usuario','$nombre','1' ,NULL,NULL,NULL)";
				$resultado = $conn->query($sql);
			}
			else{
				$sql = "INSERT INTO Series(IDRutina, Usuario, NombreEjercicio, Orden, NumRepeticiones,Peso, Notas) VALUES ('$rutina','$usuario','$nombre','$row[0]' ,NULL,NULL,NULL)";
				$resultado = $conn->query($sql);
			}
	
		}
		else{
			echo "rutinaYaTieneEjercicio";
		}
	}
	else{
		echo "yaExisteEjercicio";
	}


}
else if ($opcion == "anejerciciorutina"){

	$id = $_POST["idrutina"];
	$nombre = $_POST["nombre"];
	
	$sql="SELECT NombreEjercicio FROM Series WHERE NombreEjercicio = '$nombre' AND IDRutina='$id'";
	$respuesta = $conn->query($sql);
	
	if (mysqli_num_rows($respuesta) == 0){ #Si el ejercicio no estaba ya añadido en la rutina, se puede añadir

		$sql="SELECT MAX(Orden)+1 FROM Series WHERE IDRutina='$id' AND Usuario='$usuario' AND NumRepeticiones IS NULL AND Peso IS NULL";
		$respuesta = $conn->query($sql);

		$row = mysqli_fetch_row($respuesta);

		if (is_null($row[0])){
			$sql = "INSERT INTO Series(IDRutina, Usuario, NombreEjercicio, Orden, NumRepeticiones,Peso, Notas) VALUES ('$id','$usuario','$nombre','1' ,NULL,NULL,NULL)";
			$resultado = $conn->query($sql);
		}
		else{
			$sql = "INSERT INTO Series(IDRutina, Usuario, NombreEjercicio, Orden, NumRepeticiones,Peso, Notas) VALUES ('$id','$usuario','$nombre','$row[0]' ,NULL,NULL,NULL)";
			$resultado = $conn->query($sql);
		}
	}
	else{
		echo "noPosible";
	}
}
else if ($opcion == "anrutina"){

	$nombre = $_POST["nombre"];

	$sql = "SELECT Nombre FROM Rutinas WHERE Nombre='$nombre' AND Usuario='$usuario' AND FechaHoraInicio IS NULL AND FechaHoraFinal IS NULL";
	$resultado = $conn->query($sql);
	
  	if (!$resultado) {
		echo 'Ha ocurrido algún error: ' . mysqli_error($conn);
    	}

 	if (mysqli_num_rows($resultado) == 0){
      	$resultado2 = mysqli_query($conn, "INSERT INTO Rutinas(Nombre,Usuario) VALUES('$nombre','$usuario')");

      	if (!$resultado2) {
            	echo 'Ha ocurrido algún error: ' . mysqli_error($conn);
        	}
        	else{
            	$devolver = '1';
        	}
    	}
	else{
		$devolver = '0';
    	}

	echo $devolver;

}
else if ($opcion == "ordenar"){

	$rutina = $_POST["idrutina"];
	$nombreactual = $_POST["nombreactual"];
	$nombreotro = $_POST["nombreotro"];
	$ordenactual = $_POST["ordenactual"];
	$ordenotro = $_POST["ordenotro"];

	$sql = "UPDATE Series set Orden='$ordenotro' WHERE IDRUTINA='$rutina' AND Usuario='$usuario' AND NombreEjercicio='$nombreactual' AND Orden='$ordenactual' AND NumRepeticiones IS NULL AND Peso IS NULL";
	$resultado = $conn->query($sql);

	$sql2 = "UPDATE Series set Orden='$ordenactual' WHERE IDRUTINA='$rutina' AND Usuario='$usuario' AND NombreEjercicio='$nombreotro' AND Orden='$ordenotro' AND NumRepeticiones IS NULL AND Peso IS NULL";
	$resultado2 = $conn->query($sql2);

	echo json_encode($array);

}



$conn->close();


?>