<?php
$DB_SERVER = "localhost";
$DB_USER = "Xjwojciechowska0";
$DB_PASS = "aQWGmrrWt";
$DB_DATABASE = "Xjwojciechowska0_entrega3";

$conn = new mysqli($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);


if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$opcion = $_POST["opcion"];

if ( $opcion == "obcategorias"){

	$usuario = $_POST["usuario"];

	$sql = "SELECT DISTINCT Categoria FROM Ejercicios WHERE Creador IS NULL OR Creador='$usuario'";
	$resultado = $conn->query($sql);

	$cont=0;

	while($row = mysqli_fetch_row($resultado)) {
    		$array[$cont]=array('Categoria'=>$row[0]);
		$cont++;
	}

	echo json_encode($array);

}
else if ($opcion == "obejercicioscategoria"){

	$usuario = $_POST["usuario"];
	$categoria = $_POST["categoria"];

	$sql = "SELECT DISTINCT Nombre FROM Ejercicios WHERE (Creador IS NULL OR Creador='$usuario') AND Categoria='$categoria'";
	$resultado = $conn->query($sql);
	
	$cont=0;
	
	while($row = mysqli_fetch_row($resultado)) {
    		$array[$cont]=array('Ejercicio'=>$row[0]);
		$cont++;
	}

	echo json_encode($array);

}
else if ($opcion == "obejerciciosrutina"){

	$usuario = $_POST["usuario"];
	$rutina = $_POST["rutina"];

	$sql = "SELECT IDRutina, Usuario, NombreEjercicio, Orden FROM Series WHERE Usuario='$usuario' AND IDRutina='$rutina' AND NumRepeticiones IS NULL AND Peso IS NULL ORDER BY Orden ASC";
	$resultado = $conn->query($sql);
	
	$cont=0;
	
	while($row = mysqli_fetch_row($resultado)) {
    		$array[$cont]=array('IDRutina'=>$row[0],'Usuario'=>$row[1], 'NombreEjercicio'=>$row[2], 'Orden'=>$row[3]);
		$cont++;
	}
	
	echo json_encode($array);

}
else if ($opcion == "obrutinas"){

	$usuario = $_POST["usuario"];

	$sql = "SELECT IDRutina, Nombre FROM Rutinas WHERE Usuario='$usuario' AND FechaHoraInicio IS NULL AND FechaHoraFinal IS NULL";
	$resultado = $conn->query($sql);

	$cont=0;

	while($row = mysqli_fetch_row($resultado)) {
    		$array[$cont]=array('IDRutina'=>$row[0],'Nombre'=>$row[1]);
		$cont++;
	}

	echo json_encode($array);

}

$conn->close();

?>