<?php
$allumage=$_request['allumage'];
$extinction=$_POST['extinction'];
$temp=$_POST['temp'];
$nom_salle=$_POST['nom_salle'];

  // connexion à la base de données
  $db_user = 'pi';
    $db_password = 'Simconolat';
    $db_name     = 'MACC';
    $db_host     = '192.168.13.91';
	//$db_host     = '93.121.180.47';

$db = mysqli_connect($db_host, $db_user, $db_password,$db_name)
           or die('could not connect to database');
		   
	$sql = "INSERT INTO FIXER_CONSIGNE (" .", ".", ".",".",".", ".")
VALUES (".",".","$allumage","$extinction","$temp","$nom_salle")";

if (mysqli_query($db, $sql)) {
    echo "New record created successfully";
} else {
    echo "Error: " . $sql . "<br>" . mysqli_error($db);
}


		
	//$query=mysqli_insert("INSERT INTO FIXER_CONSIGNE VALUES('','','$allumage','$extinction','$temp','$salle');") or die ("Erreur d'insertion des donn&eacute;es dans la BDD");				 
				// Echo '<br/> Vos donn&eacute;es ont bien &eacute;t&eacute; enregistr&eacute;es';
                 //on le renvoie à l'accueil
                 // header('Refresh:5;url=index.php');		   


?>