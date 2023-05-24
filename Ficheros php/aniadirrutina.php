<?php
$DB_SERVER = "localhost";
$DB_USER = "Xjwojciechowska0";
$DB_PASS = "aQWGmrrWt";
$DB_DATABASE = "Xjwojciechowska0_entrega3";

$conn = new mysqli($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);


if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}


$nombre = $_POST["nombre"];
$usuario = $_POST["usuario"];

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

$conn->close();

?>