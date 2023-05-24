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
$email = $_POST["email"];
$foto=$_POST["foto"];

$sql = "INSERT INTO Usuarios (usuario, password, email, foto) VALUES ('$usuario', '$password', '$email', '$foto')";

if ($conn->query($sql) === TRUE) {
    echo "New user created successfully";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}


$conn->close();

?>