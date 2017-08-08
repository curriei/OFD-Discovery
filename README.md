# OFD-Discovery
Discovers functional dependencies in a dataset using an ontology for added context.


PRIOR TO RUNNING:

	RDF5.java MUST be edited prior to running the code such that the variables CSV_LOCATION, ONTOLOGY_LOCATION, and OUPUT_FOLDER are set to     the correct locations for your machine.

	RDF5.java can also be edited to output the times of an experiment; at the bottom of the file in the method finalOutput(), there is code     which can be uncommented to change this.  The given code is marked in the file with comments.


RUNNING:

	Use with RDF_OFD.py; alg1.java should be run first, followed by RDF_OFD.py, finally followed by alg2.java.

	Both alg1.java and alg2.java must be run with at least 3 arguments passed to them in the following order: csv delimiter, IsA threshold,     fileName.
	Following these three arguments, pass each of the attributes which relate to an ontology as separate arguments to the commands.
  

Please email Ian Currie at curriei@mcmaster.ca with any further questions.
