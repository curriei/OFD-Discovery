# OFD-Discovery
Discovers functional dependencies in a dataset using an ontology for added context.

RUNNING INSTRUCTIONS:
  1. Edit RDF5.java such that the variables CSV_LOCATION, ONTOLOGY_LOCATION and OUTPUT_FOLDER are set to folders on your machine.  Ensure you keep the same formatting from the previous examples, including the slash at the end of the last folder name.
  2. If you want to view the times of the experiment in the output file, uncomment the marked code at the bottom of RDF5.java within the method finalOutput().
  3. RDF_OFD.py, as well as any .rdf files being used must be placed in the folder set to ONTOLOGY_LOCATION.
  4. The dataset which you are analysing must be placed in the folder set to CSV_LOCATION.
  5. Run alg1.java, pass it at least three arguments in the following order: csv delimiter, Ontology IsA threshold, csv file name.  Following these three arguments, pass the names of all attributes which have an ontology relating to them. For example, a common command prompt for the code might look like the following:
  ```
  $ java ofd5.alg1 , 3 fileNameExample ontologyAttribute1 ontologyAttribute2 ontologyAttribute3
  ```
  6. alg1.java will create files with the name *attributeName*.csv in the folder set to ONTOLOGY_LOCATION.
  7. For each attribute which is associated with an ontology, run RDF_OFD.py, passing it two arguments: the name of the .rdf file representing the ontology for said attribute, and *attributeName*.csv.  For example, if IsA_Medicine.rdf is associated with a column titled "medicine", your command should look like this:
  ```
  $ python RDF_OFD.py IsA_Medicine.rdf medicine.csv
  ```
  8. Each time it is run, RDF_OFD.py will output a csv file to the ONTOLOGY_LOCATION folder, titled *attributeName*out.csv.
  9. Run alg2.java, passing it the exact same arguments that you passed alg1.java (see step 5).
  10. alg2.java will create an output file named *fileName*_out.txt in the output folder, it will contain all found OFD's, as well as the time spent at various stages of the experiment (if you completed step 2).


Please email Ian Currie at curriei@mcmaster.ca with any further questions.
