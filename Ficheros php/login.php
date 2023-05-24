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
$password = $_POST["password"];

$sql = "SELECT usuario,password FROM Usuarios WHERE usuario='$usuario' AND password='$password'";
$resultado = $conn->query($sql);

while($row = $resultado->fetch_assoc()) {
    echo json_encode($row);
}
$conn->close();

?>