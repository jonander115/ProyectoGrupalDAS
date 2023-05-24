<?php
$DB_SERVER = "localhost";
$DB_USER = "Xjwojciechowska0";
$DB_PASS = "aQWGmrrWt";
$DB_DATABASE = "Xjwojciechowska0_entrega3";

$conn = new mysqli($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}


$cabecera= array('Authorization: key=AAAAXyx57xM:APA91bG9K3S0Ezqq7Vsu2nVmMm04lOpLUTKG-3uKWB0nuQsKiLwdf3i8U-QS6TRDXmc8LHf3_4m0g766Ntq4nyKEGBAdC445Yu6zSJ_ToMDKPLCV_NJszHdnDUoFCtXYf4ipS0xra0bP', 'Content-Type: application/json');


$sql="SELECT token FROM Tokens";
$resultado = $conn->query($sql);


while($row = mysqli_fetch_row($resultado)) {

	// Cuerpo de la solicitud HTTP POST
	$msg = array( 'to' => $row[0], 'notification' => array( 'title' => "Prueba Gym Assistant Nofiticacion", 'body' => "Prueba Gym Assistant Notificacion",'sound' => 'default'));

	$msgJSON= json_encode( $msg);

	$ch= curl_init(); #inicializar el handlerde curl 
	#indicar el destino de la petición, el servicio FCM de google 
	curl_setopt( $ch, CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send'); 
	#indicar que la conexión es de tipo POST 
	curl_setopt( $ch, CURLOPT_POST, true); 
	#agregar las cabeceras 	
	curl_setopt( $ch, CURLOPT_HTTPHEADER, $cabecera); 
	#Indicar que se desea recibir la respuesta a la conexión en forma de string 
	curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true); 
	#agregar los datos de la petición en formato JSON 
	curl_setopt( $ch, CURLOPT_POSTFIELDS, $msgJSON);
	#ejecutar la llamada 
	$resultado2 = curl_exec( $ch); 
	#cerrar el handlerde curl 
	curl_close( $ch);

}

echo "Notificacion enviada.";

$conn->close();

?>