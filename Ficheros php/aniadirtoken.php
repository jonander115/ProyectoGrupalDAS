<?php
$DB_SERVER = "localhost";
$DB_USER = "Xjwojciechowska0";
$DB_PASS = "aQWGmrrWt";
$DB_DATABASE = "Xjwojciechowska0_entrega3";

$conn = new mysqli($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}


$token = $_POST["token"];

$sql = "SELECT token FROM Tokens WHERE token='$token'";
$resultado = $conn->query($sql);

if ($resultado->num_rows == 0){
	$sql2 = "INSERT INTO Tokens(token) VALUES ('$token')";
	$resultado2 = $conn->query($sql2);
}

$conn->close();


?>