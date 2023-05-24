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

$sql = "SELECT IDRutina, Nombre FROM Rutinas WHERE Usuario='$usuario' AND FechaHoraInicio IS NULL AND FechaHoraFinal IS NULL";
$resultado = $conn->query($sql);

$cont=0;

while($row = mysqli_fetch_row($resultado)) {
    	$array[$cont]=array('IDRutina'=>$row[0],'Nombre'=>$row[1]);
	$cont++;
}

echo json_encode($array);

$conn->close();

?>