package org.exist.xquery.modules.dita;

import org.dita.dost.Processor;
import org.dita.dost.ProcessorFactory;
import org.exist.dom.QName;
import org.exist.xquery.*;
import org.exist.xquery.value.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;

/**
 * A function to run DITA OT processing.
 *
 * @author Ivan Lagunov
 */
public class RunDitaOTFunction extends BasicFunction {

    private static final Logger LOG = LoggerFactory.getLogger(RunDitaOTFunction.class);

    final static FunctionSignature signature = new FunctionSignature(
            new QName("run-dita-ot", DitaModule.NAMESPACE_URI, DitaModule.PREFIX),
            "A function to run DITA OT processing.",
            new SequenceType[]{
                    new FunctionParameterSequenceType("transtype", Type.STRING, Cardinality.ONE,
                            "The transtype for the processor"),
                    new FunctionParameterSequenceType("input", Type.STRING, Cardinality.ONE,
                            "The input document file"),
                    new FunctionParameterSequenceType("output", Type.STRING, Cardinality.ONE,
                            "The absolute output directory"),
                    new FunctionParameterSequenceType("ditaDir", Type.STRING, Cardinality.ONE,
                            "DITA-OT base directory"),
                    new FunctionParameterSequenceType("tempDir", Type.STRING, Cardinality.ONE,
                            "DITA-OT temporary directory"),
                    new FunctionParameterSequenceType("properties", Type.STRING, Cardinality.ZERO_OR_MORE,
                            "The properties in format 'property-name=value'. " +
                                    "The documentation is available here: https://www.dita-ot.org/2.5/parameters/parameters_intro.html"
                    )
            },
            new SequenceType(Type.ITEM, Cardinality.EMPTY)
    );

    public RunDitaOTFunction(XQueryContext context) {
        super(context, signature);
    }

    @Override
    public Sequence eval(Sequence[] args, Sequence contextSequence) throws XPathException {
        return staticEval(args, contextSequence);
    }

    static Sequence staticEval(Sequence[] args, Sequence contextSequence) throws XPathException {
        LOG.info("Running DITA OT with parameters: " + Arrays.toString(args));
        // Create a reusable processor factory with DITA-OT base directory and temporary directory
        ProcessorFactory pf = ProcessorFactory.newInstance(new File(getArgString(args, 3)));
        pf.setBaseTempDir(new File(getArgString(args, 4)));

        // Create a processor using the factory and configure the processor
        Processor p = pf.newProcessor(getArgString(args, 0))
                .setInput(new File(getArgString(args, 1)))
                .setOutputDir(new File(getArgString(args, 2)));
        if (!args[5].isEmpty()) {
            SequenceIterator iterator = args[5].iterate();
            while (iterator.hasNext()) {
                String[] prop = iterator.nextItem().getStringValue().split("=", 2);
                p.setProperty(prop[0], prop[1]);
            }
        }
        try {
            p.run();
            LOG.info("Completed DITA OT processing");
        } catch (Exception e) {
            String message = "DITA OT process failed: " + e.getCause().getLocalizedMessage();
            LOG.error(message);
            throw new XPathException(message);
        }

        return Sequence.EMPTY_SEQUENCE;
    }

    private static String getArgString(Sequence[] args, int index) throws XPathException {
        return args[index].itemAt(0).getStringValue();
    }
}
