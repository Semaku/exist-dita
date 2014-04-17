package org.exist.xquery.modules.dita;

import org.apache.log4j.Logger;
import org.dita.dost.invoker.CommandLineInvoker;
import org.exist.dom.QName;
import org.exist.xquery.*;
import org.exist.xquery.value.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A function to run DITA OT processing.
 *
 * @author Ivan Lagunov
 */
public class RunDitaOTFunction extends BasicFunction {

    protected final static Logger log = Logger.getLogger(RunDitaOTFunction.class);

    public final static String PARAMETER_INPUT = "i";
    public final static String PARAMETER_TYPE = "transtype";

    public final static FunctionSignature signature = new FunctionSignature(
            new QName("run-dita-ot", DitaModule.NAMESPACE_URI, DitaModule.PREFIX),
            "A function to run DITA OT processing.",
            new SequenceType[]{
                    new FunctionParameterSequenceType(PARAMETER_INPUT, Type.STRING, Cardinality.EXACTLY_ONE,
                            "Specifies the master file for your documentation project. Typically this is a DITA map, however it also can be a DITA topic if you want to transform a single DITA file."),
                    new FunctionParameterSequenceType(PARAMETER_TYPE, Type.STRING, Cardinality.EXACTLY_ONE,
                            "Specifies the output format. By default, the following values are available: (docbook, eclipsehelp, eclipsecontent, htmlhelp, javahelp, legacypdf, odt, pdf, wordrtf, troff, xhtml)"),
                    new FunctionParameterSequenceType("parameters", Type.STRING, Cardinality.ZERO_OR_MORE,
                            "The parameters in format '/parameter-name:value'. " +
                                    "The documentation is available here: http://dita-ot.github.io/1.8/readme/dita-ot_java_properties.html"
                    )
            },
            new SequenceType(Type.ITEM, Cardinality.EMPTY)
    );

    public RunDitaOTFunction(XQueryContext context) {
        super(context, signature);
    }

    @Override
    public Sequence eval(Sequence[] sequences, Sequence sequence) throws XPathException {
        String[] parameters = prepareParameters(sequences);

        log.info("Running DITA OT with parameters: " + Arrays.toString(parameters));
        CommandLineInvoker.main(parameters);
        log.info("Completed DITA OT processing");

        return (Sequence.EMPTY_SEQUENCE);
    }

    private String[] prepareParameters(Sequence[] sequences) throws XPathException {
        List<String> parameters = new ArrayList<>();
        parameters.add(formatParameter(PARAMETER_INPUT, sequences[0].getStringValue()));
        parameters.add(formatParameter(PARAMETER_TYPE, sequences[1].getStringValue()));

        if (!sequences[2].isEmpty()) {
            SequenceIterator iterator = sequences[2].iterate();
            while (iterator.hasNext()) {
                Item next = iterator.nextItem();
                parameters.add(next.getStringValue());
            }
        }
        return parameters.toArray(new String[parameters.size()]);
    }

    private String formatParameter(String name, String value) {
        return String.format("/%s:%s", name, value);
    }
}
