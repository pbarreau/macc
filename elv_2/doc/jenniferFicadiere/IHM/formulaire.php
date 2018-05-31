<html>
    <head>
       <meta charset="utf-8">
	   <script>
	   
	   /*lorsqu'un utilisateur sélectionne une personne dans la liste
	   déroulante ci-dessus, une fonction appelée "showUser ()" est exécutée.

	 
	   La fonction est déclenchée par l'événement onchange
	   */
	   function showUser(str) {
    if (str == "") {
        document.getElementById("txtHint").innerHTML = "";
        return;
    } else { 
        if (window.XMLHttpRequest) {
            // code for IE7+, Firefox, Chrome, Opera, Safari
            xmlhttp = new XMLHttpRequest();
        } else {
            // code for IE6, IE5
            xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
        }
        xmlhttp.onreadystatechange = function() {
            if (this.readyState == 4 && this.status == 200) {
                document.getElementById("txtHint").innerHTML = this.responseText;
            }
        };
        xmlhttp.open("GET","getuser.php?q="+str,true);
        xmlhttp.send();
    }
}
</script>
      
    </head>
    <body background="green">


	<center>
		   <a href='index.php?deconnexion=true'><span>Déconnexion</span></a>

</center>
        <div id="container">
	<h1>Superviseur </h1>
            <!-- zone de connexion -->
			 <?php
                session_start();
                if(isset($_POST['deconnexion']))
                { 
                   if($_POST['deconnexion']==true)
                   {  
                      session_unset();
                      header("location:login.php");
                   }
                }
                else if($_SESSION['username'] !== ""){
                    $user = $_SESSION['username'];
                    // afficher un message
                    echo "<br>Bonjour $user, vous êtes connectés actuellement sur le site Superviseur,<br> merci d'insérer toutes vos données  SVP!";
                }
            ?>
			
			<br> 
			
  <center>          
            <form  method="post" action="getuser.php">
<label><b>Selectionner un batiment</b></label>	<br><br>
<select name="sal" onchange="showUser(this.value)">
  <option option>
  <option >BatV</option> 
 
    </select><br> <br>
  <br>
  </form>
  <form  method="post" action="get.php">
<div id="txtHint"><b>les informations du batiment selectionné seront ici...</b></div>
<br>
<br>
  
  <label><b> Nom de la salle :</b></label><br> </br>
<input  type="text" placeholder="Entrer le nom de la salle correctement..." name="nom_salle"><br></br>

  
   <label><b>Horaire d'allumage:</b></label><br> </br>
<input  type="time"  name="allumage"><br></br>

 <label><b>Horaire d'extinction: </b></label><br> </br>
<input  type="time" name="extinction"><br> <br>


 <label><b>Température minimale à ne pas dépasser:</b></label><br> </br>
<input  type="number" min="20" max="30" name="temp" >°C
<br/> <br>

<input type="submit" id='submit' value='Valider' > 

</form>


</center>

                
          
        </div>
    </body>
</html>