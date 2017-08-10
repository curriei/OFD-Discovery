import csv
import sys

from rdflib import Graph, URIRef


rdffile = sys.argv[1]
datafile = sys.argv[2]
tblfile = datafile.rstrip("csv").rstrip(".") + "out" + ".csv"

g = Graph()
g.load(rdffile)


for ns in g.namespaces():
    if ns[0] == "ex":
        pfx = ns[1]


syn = URIRef(pfx + "synonym")
isa = URIRef(pfx + "is_a")


with open(datafile, 'r') as f:
    reader = csv.reader(f)
    data = list(reader)


for item in data:
    pi = pfx + item[0]
    ur = URIRef(pi)

    alt = g.subjects(None, ur)
    for sn in alt:
        sub = g.subjects(syn, sn)
        for s in sub:
            item.append(s.lstrip(pfx))

    if (len(item) == 1):
        item.append(item[0])

        
    T = True
    sub = ur

    for i in range(len(g)):
        obj = g.objects(sub, isa)
        
        for o in obj:
            item.append(o.lstrip(pfx))
            T = False

        if(T or sub == o):
            break

        sub = URIRef(o)       


with open(tblfile, "w", newline="") as fl:
    writer = csv.writer(fl)
    writer.writerows(data)