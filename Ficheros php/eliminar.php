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

if ($opcion == "eliminarejerciciorutina"){

	$id = $_POST["idrutina"];
	$usuario = $_POST["usuario"];
	$nombre = $_POST["nombreejercicio"];
	$orden = $_POST["orden"];
	
	$sql = "DELETE FROM Series WHERE IDRutina='$id' AND Usuario='$usuario' AND NombreEjercicio='$nombre' AND Orden='$orden' AND NumRepeticiones IS NULL AND Peso IS NULL";
	$resultado = $conn->query($sql);

	echo $resultado;

}
else if ($opcion == "eliminarrutina"){
	$id = $_POST["id"];

	$sql = "DELETE FROM Rutinas WHERE IDRutina='$id'";
	$resultado = $conn->query($sql);

	echo $resultado;
}

$conn->close();


?>