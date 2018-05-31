<html>
    <head>
	<title> Identification supervision </title>
       <meta charset="utf-8">
        <!-- importer le fichier de style -->
        <link rel="stylesheet" href="CSS.css" type="text/css" />
    </head>
    <body>
	<center><h1>Superviseur</h1></center>
        <div id="container">
            <!-- zone de connexion -->
	
            
            <form action="verification.php" method="POST">
               <center> <h1>Connexion</h1></center>
			   
                
                <label><b>Nom d'utilisateur</b></label>
                <input type="text" placeholder="Entrer le nom d'utilisateur" name="username" required>

                <label><b>Mot de passe</b></label>
                <input type="password" placeholder="Entrer le mot de passe" name="password" required>
					
                <input type="submit" id='submit' value='LOGIN' >
				
                <?php
                if(isset($_GET['erreur'])){
                    $err = $_GET['erreur'];
                    if($err==1 || $err==2)
                        echo "<p style='color:red'>Utilisateur ou mot de passe incorrect</p>";
                }
				
                ?>
            </form>
        </div>
    </body>
</html>