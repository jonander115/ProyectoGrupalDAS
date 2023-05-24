<?php
$DB_SERVER = "localhost";
$DB_USER = "Xjwojciechowska0";
$DB_PASS = "aQWGmrrWt";
$DB_DATABASE = "Xjwojciechowska0_entrega3";

$conn = new mysqli($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}


$id = $_POST["id"];

$sql = "DELETE FROM Rutinas WHERE IDRutina='$id'";
$resultado = $conn->query($sql);

echo $resultado;

$conn->close();


?>