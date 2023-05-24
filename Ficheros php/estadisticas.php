<?php

$DB_SERVER="localhost"; #la dirección del servidor
$DB_USER="Xjwojciechowska0"; #el usuario para esa base de datos
$DB_PASS="aQWGmrrWt"; #la clave para ese usuario
$DB_DATABASE="Xjwojciechowska0_entrega3"; #la base de datos a la que hay que conectarse

#Se establece la conexión:
$con = mysqli_connect($DB_SERVER, $DB_USER, $DB_PASS, $DB_DATABASE);

#Comprobamos la conexión:
if (mysqli_connect_errno()) {
	echo 'Error de conexion: ' . mysqli_connect_error();
	exit();
}

#Parámetros a utilizar
$usuario = $_POST["usuario"];
$accion = $_POST["tarea"];



if ($accion == "listadoRutinas"){ #Hay que pedir los datos del usuario

	$resultado = mysqli_query($con, "SELECT DISTINCT Nombre FROM Rutinas WHERE usuario = '$usuario'");
	
	#Comprobar si se ha ejecutado correctamente
	if (!$resultado) {
		echo 'Ha ocurrido algún error: ' . mysqli_error($con);
	}
	
    $cont = 0;
	#Si se ha devuelto algo, la consulta es correcta
	if (mysqli_num_rows($resultado) != 0){
				
        while ($fila = mysqli_fetch_row($resultado)){
       
            $datos[$cont] = array(
                "nombre" => $fila[0]    
        );
        $cont++;
        }
        //Convertimos el array en un json
        $json = json_encode($datos);
        //Devolvemos el json
        echo $json;
		
	}
}elseif($accion == "estadisticas"){

    $nombre = $_POST["rutina"];
    $fechaIni = $_POST["fechaIni"];
    $fechaFin = $_POST["fechaFin"];


    $fechaIni = date('Y-m-d', strtotime(str_replace('/', '-', $fechaIni)));
    $fechaFin = date('Y-m-d', strtotime(str_replace('/', '-', $fechaFin)));

   

    $resultado = mysqli_query($con, "SELECT Nombre, FechaHoraInicio, FechaHoraFinal
    FROM Rutinas
    WHERE Usuario = '$usuario'
        AND Nombre = '$nombre'
        AND FechaHoraInicio IS NOT NULL
        AND FechaHoraFinal IS NOT NULL
        AND DATE(FechaHoraInicio) >= '$fechaIni'
        AND DATE(FechaHoraFinal) <= '$fechaFin'");


	#Comprobar si se ha ejecutado correctamente
	if (!$resultado) {
		echo 'Ha ocurrido algún error: ' . mysqli_error($con);
	}
	
    $cont = 0;
	#Si se ha devuelto algo, la consulta es correcta
	if (mysqli_num_rows($resultado) != 0){
				
        while ($fila = mysqli_fetch_row($resultado)){
       
            $datos[$cont] = array(
                "nombre" => $fila[0],
                "inicio" => $fila[1],
                "fin" => $fila[2]    
        );
        $cont++;
        }
        //Convertimos el array en un json
        $json = json_encode($datos);
        //Devolvemos el json
        echo $json;
		
	}
    
    }elseif($accion == "spinnerEjer"){   
    
        $resultado = mysqli_query($con, "SELECT
        Ejercicios.Categoria,
        Ejercicios.Nombre
        FROM Ejercicios INNER JOIN Series ON Ejercicios.Nombre = Series.NombreEjercicio INNER JOIN Rutinas ON Series.IDRutina = Rutinas.IDRutina 
        WHERE Rutinas.Usuario = '$usuario' AND Ejercicios.IsDefault = 0
        
        UNION

        SELECT
        Ejercicios.Categoria,
        Ejercicios.Nombre
        FROM Ejercicios 
        WHERE Ejercicios.IsDefault = 1");
    
    
        #Comprobar si se ha ejecutado correctamente
        if (!$resultado) {
            echo 'Ha ocurrido algún error: ' . mysqli_error($con);
        }
        
        $cont = 0;
        #Si se ha devuelto algo, la consulta es correcta
        if (mysqli_num_rows($resultado) != 0){
                    
            while ($fila = mysqli_fetch_row($resultado)){
           
                $datos[$cont] = array(
                    
                    "categoria" => $fila[0],
                    "nombreEjer" => $fila[1]

            );
            $cont++;
            }
            //Convertimos el array en un json
            $json = json_encode($datos);
            //Devolvemos el json
            echo $json;
            
        }

}else{ 
    
    $ejercicio = $_POST["ejercicio"];
    $fechaIni = $_POST["fechaIni"];
    $fechaFin = $_POST["fechaFin"];

    

    $fechaIni = date('Y-m-d', strtotime(str_replace('/', '-', $fechaIni)));
    $fechaFin = date('Y-m-d', strtotime(str_replace('/', '-', $fechaFin)));
    
    
    $resultado = mysqli_query($con, "SELECT
    Rutinas.IDRutina, 
    Series.Peso,
    Rutinas.Nombre,
    Series.NumRepeticiones,
    Rutinas.FechaHoraInicio
    FROM Ejercicios INNER JOIN Series ON Ejercicios.Nombre = Series.NombreEjercicio INNER JOIN Rutinas ON Series.IDRutina = Rutinas.IDRutina 
    WHERE Rutinas.Usuario = '$usuario' AND Ejercicios.Nombre = '$ejercicio' AND DATE(Rutinas.FechaHoraInicio) >= '$fechaIni' AND DATE(Rutinas.FechaHoraFinal) <= '$fechaFin'");


    #Comprobar si se ha ejecutado correctamente
    if (!$resultado) {
        echo 'Ha ocurrido algún error: ' . mysqli_error($con);
    }
    
    $cont = 0;
    #Si se ha devuelto algo, la consulta es correcta
    if (mysqli_num_rows($resultado) != 0){
                
        while ($fila = mysqli_fetch_row($resultado)){
       
            $datos[$cont] = array(
                "idRutina" => $fila[0],
                "peso" => $fila[1],
                "nombreRutina" => $fila[2],
                "numRepes" => $fila[3],
                "fechaHora" => $fila[4]

        );
        $cont++;
        }
        //Convertimos el array en un json
        $json = json_encode($datos);
        //Devolvemos el json
        echo $json;
        
    }
}

#Cerramos la conexión con la base de datos
mysqli_close($con);


?>