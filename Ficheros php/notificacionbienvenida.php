<?php
#Parámetros a utilizar
$token = $_POST["token"];

#Hacemos uso de la mensajería FCM
#El servicio de mensajería Firebase se utiliza en mi aplicación para mandar una notificación de bienvenida a los usuarios nada más registrarse

#Cabecera de la petición
$cabecera= array(
	'Authorization: key=AAAAXyx57xM:APA91bG9K3S0Ezqq7Vsu2nVmMm04lOpLUTKG-3uKWB0nuQsKiLwdf3i8U-QS6TRDXmc8LHf3_4m0g766Ntq4nyKEGBAdC445Yu6zSJ_ToMDKPLCV_NJszHdnDUoFCtXYf4ipS0xra0bP',
	'Content-Type: application/json'
);
	
#A quién va dirigido el mensaje
$msg= array(
		'to'=>$token,
		'data' => array(
				"mensaje" => "Bienvenido a Gym Assistant")
		);
			
#Pasar mensaje a JSON
$msgJSON= json_encode ( $msg);
	
$ch = curl_init(); #inicializar el handler de curl

#indicar el destino de la petición, el servicio FCM de google
curl_setopt( $ch, CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send');

#indicar que la conexión es de tipo POST
curl_setopt( $ch, CURLOPT_POST, true );

#agregar las cabeceras
curl_setopt( $ch, CURLOPT_HTTPHEADER, $cabecera);
	
#Indicar que se desea recibir la respuesta a la conexión en forma de string
curl_setopt( $ch, CURLOPT_RETURNTRANSFER, true );
	
#agregar los datos de la petición en formato JSON
curl_setopt( $ch, CURLOPT_POSTFIELDS, $msgJSON );

#ejecutar la llamada
$resultado= curl_exec( $ch );

#cerrar el handler de curl
curl_close( $ch );
	
if (curl_errno($ch)) {
	print curl_error($ch);
}

#Devolver el resultado
if ($resultado == True){
	$devolver = 'notificacionExitosa';
}
else{
	$devolver = 'notificacionFallada';
}

echo $devolver;

?>