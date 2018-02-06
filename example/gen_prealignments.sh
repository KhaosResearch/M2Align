#!/bin/bash

for f in *.tfa
do
  f="${f##*/}"
  echo "$f"

  clustalw2 -infile=$1$f -outfile=$f"_clu" -output=FASTA -ALIGN -QUIET -OUTORDER=input
  mafft --auto --inputorder --quiet $1$f > $f"_mafft"
  t_coffee $1$f -output fasta
  muscle -in $1$f -fastaout $f"_muscle" -quiet
  java -jar /usr/local/bin/retalign.jar -out $f"_retalign" $1$f
  kalign -c input -f fasta -q -i $1$f -o $f"_kalign"
  probcons  $1$f > $f"_probcons"
  fsa --refinement 100 $1$f > $f"_fsa"

done

for f in $1$2*.msf
do
  f2="${f##*/}"
  cp $f $f2
done 

rm $1*.dnd
rm *.dnd


