# OFD-Discovery
Discovers functional dependencies in a dataset using an ontology for added context.

Use with RDF_OFD.py; alg1.java should be run first, followed by RDF_OFD.py, finally followed by alg2.java.

RDF5.java MUST be edited prior to running the code such that the variables CSV_LOCATION, ONTOLOGY_LOCATION, and OUPUT_FOLDER are set to the correct locations for your machine.

RDF5.java can also be edited to output the times of an experiment; at the bottom of the file in the method finalOutput(), there is code which can be uncommented to change this.  The given code is marked in the file with comments.
