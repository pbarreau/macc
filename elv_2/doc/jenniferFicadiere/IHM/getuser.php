<!DOCTYPE html>
<html>
<head>
<style>
table {
    width: 100%;
    border-collapse: collapse;
}

table, td, th {
    border: 1px solid black;
    padding: 5px;
}

th {text-align: left;}
</style>
</head>
<body>

<?php


//$con = mysqli_connect('192.168.13.91','pi','Simconolat','MACC');
$con = mysqli_connect('93.121.180.47','pi','Simconolat','MACC');
if (!$con) {
    die('Could not connect: ' . mysqli_error($con));
}

mysqli_select_db($con,"");
$sql="SELECT * FROM SALLE_SALLE ";
$result = mysqli_query($con,$sql);

echo "

</option>
<table>
<tr>
<th>NOM_SALLE</th>
</tr>";
while($row = mysqli_fetch_array($result)) {
    

    echo "<td>" . $row['NOM_SALLE'] . "</td>";
    echo "</tr>";
}
echo "</table>";
mysqli_close($con);


?>

</body>
</html>

