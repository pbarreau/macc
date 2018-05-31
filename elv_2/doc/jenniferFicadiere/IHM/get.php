<?php



//on recupère les valeurs du formulaire 
$allumage=$_POST['allumage'];
$extinction=$_POST['extinction'];
$temp=$_POST['temp'];
$nom_salle=$_POST['nom_salle'];

      // connexion à la base de données
  $db_user = 'pi';
    $db_password = 'Simconolat';
    $db_name     = 'MACC';
   // $db_host     = '192.168.13.91';
	$db_host     = '93.121.180.47';
	
    $db = mysqli_connect($db_host, $db_user, $db_password,$db_name)
       
   or die("Connection failed: " . mysqli_connect_error());
		 
				 
				 
	$sql = "INSERT INTO FIXER_CONSIGNE (`HORAIRE_ALLUMAGE`, `HORAIRE_EXTINC`, `TEMP_FIXE`, `SALLE`)
VALUES ('".$allumage."','".$extinction."','".$temp."','".$nom_salle."')";

if (mysqli_query($db, $sql)) {
    echo "New record created successfully";
} else {
    echo "Error: " . $sql . "<br>" . mysqli_error($db);

}
 header('Refresh:0;url=fin.html');
mysqli_close($db);
?>