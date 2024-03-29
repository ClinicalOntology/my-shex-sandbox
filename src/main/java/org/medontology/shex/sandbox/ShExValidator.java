package org.medontology.shex.sandbox;

import org.apache.jena.atlas.logging.LogCtl;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shex.*;
import org.apache.jena.shex.sys.ShexLib;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

public class ShExValidator {

    private static final Logger logger = LoggerFactory.getLogger(ShExValidator.class);

    public static void main(String... args) {

        if(args.length != 4) {
            System.out.println("Must provide four arguments: [0] schema path, [1] data path, [2] focus node IRI, and [3] shape IRI");
            System.exit(0);
        } else {
            //http://shex.io/webapps/shex.js/doc/shex-simple.html?schema=BASE%20%3Chttp%3A%2F%2Fa.example%2F%3E%0A%0A%3CS%3E%20%20%7B%0A%20%3Cp%3E%20%5B0%5D%0A%7D%0A%0A%3CT%3E%20extends%20%40%3CS%3E%20%7B%0A%20%3Cp%3E%20%5B1%5D%0A%7D%0A%0A&data=BASE%20%3Chttp%3A%2F%2Fa.example%2F%3E%0A%0A%3Chttp%3A%2F%2Fa.example%2Fx%3E%20%3Cp%3E%200%2C%201%20.%0A&shape-map=%3Chttp%3A%2F%2Fa.example%2Fx%3E%40%3Chttp%3A%2F%2Fa.example%2FT%3E&interface=human&success=proof&regexpEngine=eval-threaded-nerr&manifestURL=http%3A%2F%2Fshex.io%2Fwebapps%2Fshex.js%2Fexamples%2Fmanifest.json

            String schemaPath = args[0];
            String dataFilePath = args[1];
            String focusNodeIri = args[2];
            String shapeIri = args[3];

            //String SHAPES = "shex/schema/ExtendAND3G.shex";
            //String DATA = "shex/data/sABCDE.ttl";

            logger.info("Reading data from: " + dataFilePath);
            Graph dataGraph = RDFDataMgr.loadGraph(dataFilePath);

            logger.info("Loading schema: " + schemaPath);
            ShexSchema shexSchema = Shex.readSchema(schemaPath);

            logger.info("Using focus node: " + focusNodeIri);
            logger.info("Using shape: " + shapeIri);

            // Shapes map.
            Node focusNode = NodeFactory.createURI(focusNodeIri);
//        Triple instancesOfFoo = Triple.create(Shex.FOCUS, RDF.type.asNode(), myClass);
            Node shape = NodeFactory.createURI(shapeIri);

            ShapeMap shapeMap = ShapeMap.newBuilder()
                    .add(focusNode, shape)
                    .build();

            // Equivalent helper for map with one ShapeMap entry
//        ShapeMap shapeMapAlt = ShapeMap.record(myClass, shape1);

            // Validate
            ShexReport report = ShexValidator.get().validate(dataGraph, shexSchema, shapeMap);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ShexLib.printReport(outputStream, report);
            logger.info("Validation - conformant?: " + report.conforms());
            logger.info(new String(outputStream.toByteArray(), Charset.defaultCharset()));
        }
    }
}
