<?php

/**
* suppression des caractères accentués d'une string
* @param $str string
* @return string sans les caractères accentués
*/
function removeDiacritics($str) {
	return str_replace(array (
		"à",
		"â",
		"ä",
		"å",
		"ã",
		"á",
		"Â",
		"Ä",
		"À",
		"Å",
		"Ã",
		"Á",
		"æ",
		"Æ",
		"ç",
		"Ç",
		"é",
		"è",
		"ê",
		"ë",
		"É",
		"Ê",
		"Ë",
		"È",
		"ï",
		"î",
		"ì",
		"í",
		"Ï",
		"Î",
		"Ì",
		"Í",
		"ñ",
		"Ñ",
		"ö",
		"ô",
		"ó",
		"ò",
		"õ",
		"Ó",
		"Ô",
		"Ö",
		"Ò",
		"Õ",
		"ù",
		"û",
		"ü",
		"ú",
		"Ü",
		"Û",
		"Ù",
		"Ú",
		"ý",
		"ÿ",
		"Ÿ",
		"ß"
	), array (
		"a",
		"a",
		"a",
		"a",
		"a",
		"a",
		"A",
		"A",
		"A",
		"A",
		"A",
		"A",
		"a",
		"A",
		"c",
		"C",
		"e",
		"e",
		"e",
		"e",
		"E",
		"E",
		"E",
		"E",
		"i",
		"i",
		"i",
		"i",
		"I",
		"I",
		"I",
		"I",
		"n",
		"N",
		"o",
		"o",
		"o",
		"o",
		"o",
		"O",
		"O",
		"O",
		"O",
		"O",
		"u",
		"u",
		"u",
		"u",
		"U",
		"U",
		"U",
		"U",
		"y",
		"y",
		"Y",
		"ss"
	), $str);
}

/*
//INSERT INTO bird_country(bird_fk,country_fk) VALUES(?,?);
*/
function insertCountry($dbh,$birdId,$country_id){
	$query = "INSERT INTO bird_country(bird_fk,country_fk) VALUES(".intval($birdId).",".intval($country_id).");";
	echo $query." \n";
	$qry = $dbh->prepare(
    		'INSERT INTO bird_country(bird_fk,country_fk) VALUES('.intval($birdId).','.intval($country_id).');');
	$qry->execute(array());

}

/*
* MAIN
*/


$scientific_name_index=0;
$country_code_index=4;



try {
	$sqliteFile = "ornidroid.jpg";
	$dbh = new PDO('sqlite:'.$sqliteFile); // success
	$initCoutriesQuery = "select id,code from country";
	$countryMap=array();
	foreach ($dbh->query($initCoutriesQuery) as $row){
		$birdId = $row["id"];
		$countryMap[$row["code"]]=$row["id"];
	}
	print_r($countryMap);

	if (($handle = fopen("species_by_countries_Europe.csv", "r")) !== FALSE) {
	    while (($data = fgetcsv($handle, 1000, ";")) !== FALSE) {
		$findBirdIdQuery = "select id from bird where lower(scientific_name)='".$data[$scientific_name_index]."' or lower(scientific_name2)='".$data[$scientific_name_index]."'";

		$birdId=-1;
		foreach ($dbh->query($findBirdIdQuery) as $row){
			$birdId = $row["id"];
		}
		if ($birdId>=1){
			echo "######insertion des pays pour : " . $data[$scientific_name_index]." ".$data[$country_code_index]."\n";
			insertCountry($dbh,$birdId,$countryMap[$data[$country_code_index]]);

		}
		else {
			echo "nom latin non référencé : " . $data[$scientific_name_index]."\n";
		}

	    }
	    fclose($handle);
	}

	$dbh = null;
}
catch(PDOException $e) {
    echo $e->getMessage();
}

?>
